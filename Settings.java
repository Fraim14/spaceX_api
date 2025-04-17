import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Settings {
    private static Settings instance;
    private JSONObject settings;
    private static final String SETTINGS_FILE = "settings.json";

    // ANSI color codes

    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    private Settings() {
        loadSettings();
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private void loadSettings() {
        try {
            if (Files.exists(Paths.get(SETTINGS_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(SETTINGS_FILE)));
                settings = new JSONObject(content);
            } else {
                setDefaultSettings();
            }
        } catch (IOException e) {
            System.out.println("Error loading settings. Using defaults.");
            setDefaultSettings();
        }
    }

    private void setDefaultSettings() {
        settings = new JSONObject();
        JSONObject display = new JSONObject();
        display.put("coloredOutput", true);
        display.put("dateFormat", "UTC");
        settings.put("display", display);
        saveSettings();
    }

    public void saveSettings() {
        try {
            Files.write(Paths.get(SETTINGS_FILE), settings.toString(2).getBytes());
        } catch (IOException e) {
            System.out.println("Error saving settings: " + e.getMessage());
        }
    }

    public boolean getColoredOutput() {
        return settings.getJSONObject("display").getBoolean("coloredOutput");
    }

    public String getDateFormat() {
        return settings.getJSONObject("display").getString("dateFormat");
    }

    public void setColoredOutput(boolean enabled) {
        settings.getJSONObject("display").put("coloredOutput", enabled);
        saveSettings();
    }

    public void setDateFormat(String format) {
        settings.getJSONObject("display").put("dateFormat", format);
        saveSettings();
    }

    public String formatHeader(String text) {
        if (!getColoredOutput()) {
            return "\n┌─" + "─".repeat(text.length() + 2) + "─┐\n" +
                    "│ " + text + " │\n" +
                    "└─" + "─".repeat(text.length() + 2) + "─┘";
        }
        return CYAN + "\n┌─" + "─".repeat(text.length() + 2) + "─┐\n" +
                "│ " + text + " │\n" +
                "└─" + "─".repeat(text.length() + 2) + "─┘" + RESET;
    }

    public String formatSuccess(String text) {
        return getColoredOutput() ? GREEN + "✓ " + text + RESET : "+ " + text;
    }

    public String formatError(String text) {
        return getColoredOutput() ? RED + "✗ " + text + RESET : "- " + text;
    }

    public String formatHighlight(String text) {
        return getColoredOutput() ? BLUE + text + RESET : text;
    }

    public String formatWarning(String text) {
        return getColoredOutput() ? YELLOW + "! " + text + RESET : "! " + text;
    }

    public String formatMenuItem(String number, String description) {
        if (!getColoredOutput()) {
            return number + ". " + description;
        }
        return PURPLE + number + RESET + " → " + WHITE_BOLD + description + RESET;
    }

    public String formatMenuHeader(String title) {
        if (!getColoredOutput()) {
            return "\n=== " + title + " ===";
        }
        String line = "═".repeat(title.length() + 6);
        return "\n" + CYAN + line + "\n" +
                "  " + WHITE_BOLD + title + CYAN + "  \n" +
                line + RESET;
    }

    public String formatPrompt(String text) {
        return getColoredOutput() ? YELLOW + text + RESET : text;
    }

    public String formatBoxedInfo(String text) {
        if (!getColoredOutput()) {
            return "\n=== " + text + " ===";
        }
        return CYAN + "\n┌─" + "─".repeat(text.length()) + "─┐\n" +
                "│ " + WHITE_BOLD + text + CYAN + " │\n" +
                "└─" + "─".repeat(text.length()) + "─┘" + RESET;
    }
}

