package data_access;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import use_case.get_color_scheme.GetColorSchemeUserDataAccessInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for The Color API.
 * Implements fetching color schemes from https://www.thecolorapi.com
 */
public class ColorApiDataAccessObject implements GetColorSchemeUserDataAccessInterface {
    private static final String BASE_URL = "https://www.thecolorapi.com/scheme";
    private static final int COLOR_COUNT = 5;
    private final OkHttpClient client;

    public ColorApiDataAccessObject() {
        this.client = new OkHttpClient().newBuilder().build();
    }

    @Override
    public List<String> getMonochromaticScheme(String hexColor) {
        return getColorScheme(hexColor, "monochrome", COLOR_COUNT);
    }

    @Override
    public List<String> getAnalogousScheme(String hexColor) {
        return getColorScheme(hexColor, "analogic", COLOR_COUNT);
    }

    @Override
    public List<String> getComplementaryScheme(String hexColor) {
        return getColorScheme(hexColor, "complement", COLOR_COUNT);
    }

    @Override
    public List<String> getNeutralScheme(String hexColor) {
        return getColorScheme(hexColor, "monochrome-light", COLOR_COUNT);
    }

    /**
     * Fetches a color scheme from The Color API.
     * @param hexColor the hex color (without #)
     * @param mode the scheme mode (monochrome, analogic, complement, monochrome-light)
     * @param count number of colors to return
     * @return list of hex color codes
     */
    private List<String> getColorScheme(String hexColor, String mode, int count) {
        String url = String.format("%s?hex=%s&mode=%s&count=%d", BASE_URL, hexColor, mode, count);
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray colorsArray = jsonResponse.getJSONArray("colors");
            
            List<String> hexColors = new ArrayList<>();
            for (int i = 0; i < colorsArray.length(); i++) {
                JSONObject colorObj = colorsArray.getJSONObject(i);
                JSONObject hexObj = colorObj.getJSONObject("hex");
                String cleanHex = hexObj.getString("clean");
                hexColors.add(cleanHex);
            }
            
            return hexColors;
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch color scheme from API", e);
        }
    }
}

