package use_case.addEvent;


import entity.Event;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Input Data for the Add Event Use Case
 */

public class AddEventInputData {
    private final UUID id;
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String location;
    private final Event.CategoryType category;
    private final String reminder;

    public AddEventInputData(UUID id,
                             String title,
                             LocalDateTime start,
                             LocalDateTime end,
                             String location,
                             Event.CategoryType category,
                             String reminder) {
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
    public Event.CategoryType getCategory() {return category;}
    public String getReminder() {return reminder;}


    public enum CategoryType {
        Business,
        Gym,
        Formal,
        Casual,
        Class,
        Coffee,
    }

}
