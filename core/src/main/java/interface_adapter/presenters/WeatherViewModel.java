package interface_adapter.presenters;

/**
 * View model that the UI layer can bind to.
 * This is a simple data holder for what the view should display.
 */
public class WeatherViewModel {

    private String locationDisplay;
    private String currentTempText;
    private String todayRangeText;
    private String descriptionText;
    private String error;

    public String getLocationDisplay() {
        return locationDisplay;
    }

    public void setLocationDisplay(String locationDisplay) {
        this.locationDisplay = locationDisplay;
    }

    public String getCurrentTempText() {
        return currentTempText;
    }

    public void setCurrentTempText(String currentTempText) {
        this.currentTempText = currentTempText;
    }

    public String getTodayRangeText() {
        return todayRangeText;
    }

    public void setTodayRangeText(String todayRangeText) {
        this.todayRangeText = todayRangeText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
