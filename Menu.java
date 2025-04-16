import java.util.Scanner;

public class Menu {
    public Scanner scanner;
    Filter filter = new Filter();
    private final Settings settings = Settings.getInstance();

    public Menu() {
        scanner = new Scanner(System.in);
    }

    public void menuOptions() {
        System.out.println(settings.formatMenuHeader("Space-X API Menu"));
        System.out.println(settings.formatMenuItem("1", "Select what you want to find"));
        System.out.println(settings.formatMenuItem("2", "Settings"));
        System.out.println(settings.formatMenuItem("3", "Cache"));
        System.out.println(settings.formatMenuItem("4", "Exit"));
        System.out.print("\n" + settings.formatPrompt("Enter your choice: "));
    }

    public void castMenu() {
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    displayFilterMenu();
                    castFilterMenu();
                    break;
                case "2":
                    displaySettingsMenu();
                    break;
                case "3":
                    break;
                case "4":
                    System.out.println(settings.formatSuccess("Exiting program. Goodbye!"));
                    System.exit(0);
                    break;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 4."));
                    menuOptions();
                    break;
            }
        }
    }

    private void displaySettingsMenu() {
        while (true) {
            System.out.println(settings.formatMenuHeader("Settings"));
            System.out.println(settings.formatHighlight("Current Settings:"));
            System.out.println(settings.formatMenuItem("Colored Output", 
                settings.getColoredOutput() ? settings.formatSuccess("Enabled") : settings.formatError("Disabled")));
            System.out.println(settings.formatMenuItem("Date Format", settings.formatHighlight(settings.getDateFormat())));
            System.out.println(settings.formatMenuItem("Show Emoji", 
                settings.getShowEmoji() ? settings.formatSuccess("Enabled") : settings.formatError("Disabled")));
            
            System.out.println(settings.formatHighlight("\nOptions:"));
            System.out.println(settings.formatMenuItem("1", "Toggle Colored Output"));
            System.out.println(settings.formatMenuItem("2", "Change Date Format"));
            System.out.println(settings.formatMenuItem("3", "Toggle Emoji"));
            System.out.println(settings.formatMenuItem("4", "Back to Main Menu"));
            
            System.out.print(settings.formatPrompt("Enter your choice (1-4): "));

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    toggleColoredOutput();
                    break;
                case "2":
                    changeDateFormat();
                    break;
                case "3":
                    toggleEmoji();
                    break;
                case "4":
                    return;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 4."));
                    System.out.println(settings.formatPrompt("\nPlease select an option:"));
            }
        }
    }

    private void toggleColoredOutput() {
        boolean current = settings.getColoredOutput();
        settings.setColoredOutput(!current);
        System.out.println(settings.formatSuccess("Colored output: " + (!current ? "Enabled" : "Disabled")));
    }

    private void changeDateFormat() {
        while (true) {
            System.out.println(settings.formatMenuHeader("Date Format Settings"));
            System.out.println(settings.formatMenuItem("1", "UTC"));
            System.out.println(settings.formatMenuItem("2", "Local"));
            System.out.println(settings.formatMenuItem("3", "Back to Settings"));
            
            System.out.print(settings.formatPrompt("Enter your choice (1-3): "));
            
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    settings.setDateFormat("UTC");
                    System.out.println(settings.formatSuccess("Date format updated to UTC"));
                    return;
                case "2":
                    settings.setDateFormat("Local");
                    System.out.println(settings.formatSuccess("Date format updated to Local"));
                    return;
                case "3":
                    return;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 3."));
                    System.out.println(settings.formatPrompt("\nPlease select an option:"));
            }
        }
    }

    private void toggleEmoji() {
        boolean current = settings.getShowEmoji();
        settings.setShowEmoji(!current);
        System.out.println(settings.formatSuccess("Emoji display: " + (!current ? "Enabled" : "Disabled")));
    }

    public void displayFilterMenu() {
        System.out.println(settings.formatHeader("Search Options"));
        System.out.println(settings.formatMenuItem("1", "Filter"));
        System.out.println(settings.formatMenuItem("2", "Find by ID"));
        System.out.println(settings.formatMenuItem("3", "Back to Main Menu"));
    }

    public void castFilterMenu() {
        while(true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    filter.filterData();
                    break;
                case "2":
                    filter.findById();
                    break;
                case "3":
                    System.out.println(settings.formatSuccess("Returning to main menu..."));
                    displayMenu();
                    break;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 3."));
                    System.out.println(settings.formatPrompt("\nPlease select an option:"));
                    displayFilterMenu();
                    break;
            }
        }
    }

    public void displayMenu() {
        menuOptions();
        castMenu();
    }
}