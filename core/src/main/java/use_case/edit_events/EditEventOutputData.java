package use_case.edit_events;

import java.time.LocalDateTime;
import java.util.UUID;

public class EditEventOutputData {

    private final UUID id;
    private final String title;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final String location;
    private final CategoryType category;
    private final Reminder reminder;

    public EditEventOutputData(UUID id, String title, LocalDateTime start, LocalDateTime end,
                               String location, CategoryType category, Reminder reminder) {
        this.id = id;
        this.title = title;
        this.start = start;
        this.end = end;
        this.location = location;
        this.category = category;
    }

}
