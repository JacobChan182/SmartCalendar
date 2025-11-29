package data_access;

import entity.User;
import entity.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

import java.sql.*;
import java.io.File;

/**
 * DAO for user data implemented using SQLite database.
 */
public class SQLiteUserDataAccessObject implements SignupUserDataAccessInterface,
                                                    LoginUserDataAccessInterface,
                                                    ChangePasswordUserDataAccessInterface,
                                                    LogoutUserDataAccessInterface {

    private static final String DB_URL_PREFIX = "jdbc:sqlite:";
    private final String dbPath;
    private final UserFactory userFactory;
    private String currentUsername;

    // Explicitly load the SQLite JDBC driver
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found. Make sure sqlite-jdbc is on the classpath.", e);
        }
    }

    /**
     * Constructs this DAO for saving to and reading from a SQLite database.
     * @param dbPath the path to the SQLite database file
     * @param userFactory factory for creating user objects
     */
    public SQLiteUserDataAccessObject(String dbPath, UserFactory userFactory) {
        this.dbPath = dbPath;
        this.userFactory = userFactory;
        initializeDatabase();
    }

    /**
     * Initializes the database by creating the users table if it doesn't exist.
     */
    private void initializeDatabase() {
        try (Connection connection = getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT NOT NULL
                )
                """;
            
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }
    }

    /**
     * Gets a connection to the SQLite database.
     * @return a Connection to the database
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL_PREFIX + dbPath);
    }

    @Override
    public boolean existsByName(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to check if user exists", ex);
        }
        return false;
    }

    @Override
    public void save(User user) {
        try (Connection connection = getConnection()) {
            String sql = "INSERT OR REPLACE INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getName());
                statement.setString(2, user.getPassword());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save user", ex);
        }
    }

    @Override
    public User get(String username) {
        try (Connection connection = getConnection()) {
            String sql = "SELECT username, password FROM users WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("username");
                        String password = resultSet.getString("password");
                        return userFactory.create(name, password);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to get user", ex);
        }
        return null;
    }

    @Override
    public void setCurrentUsername(String name) {
        this.currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return this.currentUsername;
    }

    @Override
    public void changePassword(User user) {
        try (Connection connection = getConnection()) {
            String sql = "UPDATE users SET password = ? WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getPassword());
                statement.setString(2, user.getName());
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("User not found: " + user.getName());
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to change password", ex);
        }
    }
}