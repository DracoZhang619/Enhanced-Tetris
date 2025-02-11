package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationsTest {

    private Configurations configurations;
    private static final String TEST_CONFIG_FILE = "test_configurations.json";

    @BeforeEach
    public void setUp() {
        configurations = new Configurations(TEST_CONFIG_FILE); // Use test config file
    }

    @AfterEach
    public void tearDown() {
        // Clean up the test file
        File file = new File(TEST_CONFIG_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSaveToFile() {
        configurations.setFieldWidth(12);
        configurations.setGameLevel(3);
        configurations.saveToFile();

        File file = new File(TEST_CONFIG_FILE);
        assertTrue(file.exists(), "Config file should be saved");

        try {
            String content = Files.readString(file.toPath());
            assertTrue(content.contains("\"fieldWidth\":12"), "Saved configuration should contain fieldWidth=12");
            assertTrue(content.contains("\"gameLevel\":3"), "Saved configuration should contain gameLevel=3");
        } catch (IOException e) {
            fail("Failed to read the saved config file");
        }
    }


    @Test
    public void testLoadFromFile() {
        configurations.setFieldWidth(14);
        configurations.setGameLevel(6);
        configurations.saveToFile();

        Configurations loadedConfigurations = Configurations.loadFromFile(TEST_CONFIG_FILE); // Load from file

        assertEquals(14, loadedConfigurations.getFieldWidth(), "Loaded configuration should have fieldWidth=14");
        assertEquals(6, loadedConfigurations.getGameLevel(), "Loaded configuration should have gameLevel=6");
    }

}
