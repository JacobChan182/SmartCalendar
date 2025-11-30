package entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    private final UUID id;
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String location;
    private final CategoryType category;
    private final String reminder;

    public Event(UUID id, String title, LocalDateTime start, LocalDateTime end, String location, CategoryType category, String reminder) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.category = category;
        this.reminder = reminder;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public LocalDateTime getStart() { return start; }
    public LocalDateTime getEnd() { return end; }
    public String getLocation() { return location; }
    public CategoryType getCategory() {return category;}
    public String getReminderMessage() {return reminder;}

    @Override
    public String toString() {
        return String.format("%s (%s - %s) [%s]",
                title,
                start.toLocalTime(),
                end.toLocalTime(),
                category.name());
    }

    public enum CategoryType {
        BUSINESS,
        GYM,
        FORMAL,
        CASUAL,
        WORK,
        OFFICE,
        MEETING
            }
}

