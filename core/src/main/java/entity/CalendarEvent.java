package entity;

import java.time.LocalDateTime;
public class CalendarEvent {
    private final String id;
    private final String title;
    private final LocalDateTime startTime;
    private final String description;

    public CalendarEvent(String id, String title, LocalDateTime startTime, String description) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }
}
