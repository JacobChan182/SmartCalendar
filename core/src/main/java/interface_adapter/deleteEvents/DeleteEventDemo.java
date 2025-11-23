package interface_adapter.deleteEvents;

import use_case.deleteEvents.DeleteEventOutputBoundary;
import use_case.deleteEvents.DeleteEventInputBoundary;
import data_access.InMemoryEventDataAccessObject;
import use_case.deleteEvents.EventDataAccessInterface;
import use_case.deleteEvents.DeleteEventInteractor;
//editEvents excluded!

public class DeleteEventDemo {
    public static void main(String[] args) {
        EventDataAccessInterface dao = new InMemoryEventDataAccessObject();
        DeleteEventViewModel viewModel = new DeleteEventViewModel();
        DeleteEventOutputBoundary presenter = new DeleteEventPresenter(viewModel);
        DeleteEventInputBoundary interactor = new DeleteEventInteractor(dao, presenter);
        DeleteEventController controller = new DeleteEventController(interactor);

        System.out.println("=== Delete e2 ===");
        controller.deleteEvent("e2");  // success

        System.out.println("\n=== Delete non-existing ===");
        controller.deleteEvent("e999");  // delete empty event

        System.out.println("\n=== Delete with empty id ===");
        controller.deleteEvent("");  // empty id
    }
}
