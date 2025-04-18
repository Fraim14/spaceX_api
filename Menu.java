import java.util.Scanner;

public class Menu {
    public Scanner scanner;
    Filter filter = new Filter();
    private final Settings settings = Settings.getInstance();

    public Menu() {
        scanner = new Scanner(System.in);
    }

    // this is the ui for main menu
    public void menuOptions() {
        System.out.println(settings.formatMenuHeader("Space-X API Menu"));
        System.out.println(settings.formatMenuItem("1", "Select what you want to find"));
        System.out.println(settings.formatMenuItem("2", "Settings"));
        System.out.println(settings.formatMenuItem("3", "Exit"));
        System.out.print("\n" + settings.formatPrompt("Enter your choice: "));
    }

    // this is the method that is used to handle the user's choice and response to it
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
                    System.out.println(settings.formatSuccess("Exiting program. Goodbye!"));
                    System.exit(0);
                    break;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 3."));
                    menuOptions();
                    break;
            }
        }
    }

    // this is the ui and handler for settings menu
    private void displaySettingsMenu() {
        while (true) {
            System.out.println(settings.formatMenuHeader("Settings"));
            System.out.println(settings.formatHighlight("Current Settings:"));
            System.out.println(settings.formatMenuItem("Colored Output",
                    settings.getColoredOutput() ? settings.formatSuccess("Enabled") : settings.formatError("Disabled")));
            System.out.println(settings.formatMenuItem("Date Format", settings.formatHighlight(settings.getDateFormat())));

            System.out.println(settings.formatHighlight("\nOptions:"));
            System.out.println(settings.formatMenuItem("1", "Toggle Colored Output"));
            System.out.println(settings.formatMenuItem("2", "Change Date Format"));
            System.out.println(settings.formatMenuItem("3", "Back to Main Menu"));

            System.out.print(settings.formatPrompt("Enter your choice (1-3): "));
            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    toggleColoredOutput();
                    break;
                case "2":
                    changeDateFormat();
                    break;
                case "3":
                    displayMenu();
                    break;
                default:
                    System.out.println(settings.formatError("Invalid input. Please enter a number between 1 and 3."));
                    System.out.println(settings.formatPrompt("\nPlease select an option:"));
            }
        }
    }

    // this is the method that is used to change the colored output
    private void toggleColoredOutput() {
        boolean current = settings.getColoredOutput();
        settings.setColoredOutput(!current);
        System.out.println(settings.formatSuccess("Colored output: " + (!current ? "Enabled" : "Disabled")));
    }

    // this is the method that is used to change the date format
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

    // this is the ui for the serch menu
    public void displayFilterMenu() {
        System.out.println(settings.formatHeader("Search Options"));
        System.out.println(settings.formatMenuItem("1", "Filter your data"));
        System.out.println(settings.formatMenuItem("2", "Find by ID"));
        System.out.println(settings.formatMenuItem("3", "Back to Main Menu"));
    }

    // this is the handler for the search menu
    public void castFilterMenu() {
        while (true) {
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

    // this method starts the menu
    public void displayMenu() {
        menuOptions();
        castMenu();
    }
}