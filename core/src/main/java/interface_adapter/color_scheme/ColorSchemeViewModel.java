package interface_adapter.color_scheme;

import interface_adapter.ViewModel;

/**
 * The View Model for the Color Scheme View.
 */
public class ColorSchemeViewModel extends ViewModel<ColorSchemeState> {

    public ColorSchemeViewModel() {
        super("color scheme");
        setState(new ColorSchemeState());
    }
}

