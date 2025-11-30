package interface_adapter.addEvent;


import use_case.addEvent.AddEventInputData;
import use_case.addEvent.AddEventInteractor;
import use_case.addEvent.AddEventOutputBoundary;
import use_case.addEvent.AddEventOutputData;

public class AddEventPresenter implements AddEventOutputBoundary {
    private final AddEventView view;

    public AddEventPresenter(AddEventView view) {
        this.view = view;
    }

    @Override
    public void present(AddEventOutputData outputData) {
        if (outputData.getSuccess()) {
            view.showAddedEvent(outputData.getEvent());
        }   else {
            view.showError("Error");
        }
    }

}
