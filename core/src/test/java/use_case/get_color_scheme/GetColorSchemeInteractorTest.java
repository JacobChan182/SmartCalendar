package use_case.get_color_scheme;

import data_access.InMemoryColorApiDataAccessObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for the GetColorSchemeInteractor following clean architecture principles.
 * Tests the use case layer in isolation using mock data access objects.
 */
class GetColorSchemeInteractorTest {

    @Test
    void successTest() {
        // Arrange
        String hexColor = "FF5733";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColor);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        // Create a presenter that verifies success
        GetColorSchemeOutputBoundary successPresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                // Verify all color schemes are returned
                assertNotNull(outputData.getMonochromaticColors());
                assertNotNull(outputData.getAnalogousColors());
                assertNotNull(outputData.getComplementaryColors());
                assertNotNull(outputData.getNeutralColors());

                // Verify each scheme has 5 colors
                assertEquals(5, outputData.getMonochromaticColors().size());
                assertEquals(5, outputData.getAnalogousColors().size());
                assertEquals(5, outputData.getComplementaryColors().size());
                assertEquals(5, outputData.getNeutralColors().size());

                // Verify the first color in each scheme matches the input (for mock implementation)
                assertEquals(hexColor, outputData.getMonochromaticColors().get(0));
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected. Error: " + error);
            }
        };

        // Act
        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void successTestWithHashPrefix() {
        // Test that hex colors with # prefix are handled correctly
        String hexColorWithHash = "#FF5733";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColorWithHash);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary successPresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                // Should succeed even with # prefix
                assertNotNull(outputData.getMonochromaticColors());
                assertEquals(5, outputData.getMonochromaticColors().size());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected. Error: " + error);
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void successTestWithLowercaseHex() {
        // Test that lowercase hex colors are accepted
        String hexColor = "ff5733";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColor);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary successPresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                assertNotNull(outputData.getMonochromaticColors());
                assertEquals(5, outputData.getMonochromaticColors().size());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected. Error: " + error);
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, successPresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureTestInvalidFormatTooShort() {
        // Test with invalid hex format (too short)
        String invalidHex = "FF57";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(invalidHex);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary failurePresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("Invalid hex color format"));
                assertTrue(error.contains("6-digit hex code"));
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureTestInvalidFormatTooLong() {
        // Test with invalid hex format (too long)
        String invalidHex = "FF57333";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(invalidHex);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary failurePresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("Invalid hex color format"));
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureTestInvalidFormatNonHexCharacters() {
        // Test with invalid hex format (contains non-hex characters)
        String invalidHex = "GG5733";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(invalidHex);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary failurePresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("Invalid hex color format"));
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureTestEmptyString() {
        // Test with empty string
        String emptyHex = "";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(emptyHex);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary failurePresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("Invalid hex color format"));
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void failureTestApiException() {
        // Test handling of API exceptions
        String hexColor = "FF5733";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColor);
        
        // Create a mock data access that throws an exception
        GetColorSchemeUserDataAccessInterface failingDataAccess = new GetColorSchemeUserDataAccessInterface() {
            @Override
            public List<String> getMonochromaticScheme(String hexColor) {
                throw new RuntimeException("Network error");
            }

            @Override
            public List<String> getAnalogousScheme(String hexColor) {
                throw new RuntimeException("Network error");
            }

            @Override
            public List<String> getComplementaryScheme(String hexColor) {
                throw new RuntimeException("Network error");
            }

            @Override
            public List<String> getNeutralScheme(String hexColor) {
                throw new RuntimeException("Network error");
            }
        };

        GetColorSchemeOutputBoundary failurePresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertTrue(error.contains("Failed to fetch color schemes"));
                assertTrue(error.contains("Network error"));
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                failingDataAccess, failurePresenter);
        interactor.execute(inputData);
    }

    @Test
    void successTestWithWhitespace() {
        // Test that whitespace is trimmed correctly
        String hexColorWithSpaces = "  FF5733  ";
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColorWithSpaces);
        GetColorSchemeUserDataAccessInterface colorApiDataAccess = new InMemoryColorApiDataAccessObject();

        GetColorSchemeOutputBoundary successPresenter = new GetColorSchemeOutputBoundary() {
            @Override
            public void prepareSuccessView(GetColorSchemeOutputData outputData) {
                assertNotNull(outputData.getMonochromaticColors());
                assertEquals(5, outputData.getMonochromaticColors().size());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected. Error: " + error);
            }
        };

        GetColorSchemeInputBoundary interactor = new GetColorSchemeInteractor(
                colorApiDataAccess, successPresenter);
        interactor.execute(inputData);
    }
}

