package data_access;

import entity.Event;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SQLite DAO for the "events" table.
 * This class:
 *   1) Ensures the table exists.
 *   2) Provides simple CRUD methods for events.
 */
public class SQLiteEventDataAccessObject {

    // You can change the path if needed; for now it creates "smartcalendar.db" in the working dir.
    private static final String DB_URL = "jdbc:sqlite:smartcalendar.db";

    public SQLiteEventDataAccessObject() {
        // When this DAO is created, make sure the table exists
        initializeEventsTable();
    }

    /** Get a connection to the SQLite database. */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /** Create the "events" table if it does not already exist. */
    private void initializeEventsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS events (
                id TEXT PRIMARY KEY,
                username TEXT NOT NULL,
                title TEXT NOT NULL,
                start_datetime TEXT NOT NULL,
                end_datetime TEXT NOT NULL,
                location TEXT,
                category TEXT,
                reminder_message TEXT,
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize events table", ex);
        }
    }

    /**
     * Insert or update an event for a given user.
     * If the id already exists, the row is updated.
     */
    public void saveEvent(String username, Event event) {
        String sql = """
            INSERT INTO events (id, username, title, start_datetime, end_datetime,
                                location, category, reminder_message)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET
                title = excluded.title,
                start_datetime = excluded.start_datetime,
                end_datetime = excluded.end_datetime,
                location = excluded.location,
                category = excluded.category,
                reminder_message = excluded.reminder_message
            """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, event.getId().toString());
            ps.setString(2, username);
            ps.setString(3, event.getTitle());
            ps.setString(4, event.getStart().toString()); // ISO-8601, e.g. 2025-11-30T14:30
            ps.setString(5, event.getEnd().toString());
            ps.setString(6, event.getLocation());
            ps.setString(7, event.getCategory() == null ? null : event.getCategory().name());
            ps.setString(8, event.getReminderMessage());

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save event", ex);
        }
    }

    /**
     * Delete an event by id for a given user.
     */
    public void deleteEvent(String username, UUID eventId) {
        String sql = "DELETE FROM events WHERE id = ? AND username = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, eventId.toString());
            ps.setString(2, username);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to delete event", ex);
        }
    }

    /**
     * Get all events for a specific user on a given date.
     */
    public List<Event> getEventsForDay(String username, LocalDate date) {
        String sql = """
            SELECT id, title, start_datetime, end_datetime,
                   location, category, reminder_message
            FROM events
            WHERE username = ?
              AND date(start_datetime) = ?
            ORDER BY start_datetime
            """;

        List<Event> result = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, date.toString()); // e.g. 2025-11-30

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UUID id = UUID.fromString(rs.getString("id"));
                    String title = rs.getString("title");
                    LocalDateTime start = LocalDateTime.parse(rs.getString("start_datetime"));
                    LocalDateTime end   = LocalDateTime.parse(rs.getString("end_datetime"));
                    String location = rs.getString("location");
                    String categoryStr = rs.getString("category");
                    Event.CategoryType category = categoryStr == null
                            ? null
                            : Event.CategoryType.valueOf(categoryStr);
                    String reminder = rs.getString("reminder_message");

                    Event event = new Event(
                            id,
                            title,
                            start,
                            end,
                            location,
                            category,
                            reminder
                    );
                    result.add(event);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to query events", ex);
        }

        return result;
    }
}
