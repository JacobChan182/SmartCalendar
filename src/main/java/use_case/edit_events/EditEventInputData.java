package use_case.edit_events;

import java.util.UUID;
import java.time.LocalDateTime;

public class EditEventInputData {
    private final UUID id;
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String location;
    private final CategoryType category;
    private final Reminder reminder;

    public EditEventInputData(UUID id, String title, LocalDateTime start, LocalDateTime end,
                              String location, CategoryType category, Reminder reminder){
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.category = category;
        this.reminder = reminder;
    }

    String getId() {return id.toString();}
    String getTitle() {return title;}
    LocalDateTime getStart() {return start;}
    LocalDateTime getEnd() {return end;}
    String getLocation() {return location;}
    CategoryType getCategory() {return category;}
    Reminder getReminder() {return reminder;}

}
