import java.util.Scanner;

public class Menu {
    public Scanner scanner;
    Filter filter = new Filter();

    public Menu() {
        scanner = new Scanner(System.in);
    }

    public void menuOptions() {

        System.out.println("Welcome to the Space API!");
        System.out.println("Please select an option:");
        System.out.println("1. Select what you want to find");
        System.out.println("2. Settings");
        System.out.println("3. Cache");
        System.out.println("4. Exit");
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
                    System.out.println("You have selected settings...");
                    break;
                case "3":
                    System.out.println("You have selected cache...");
                    break;
                case "4":
                    System.out.println("You have selected exit...");
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
                    menuOptions();
                    break;

            }
        }
    }

    public void displayFilterMenu() {
        System.out.println("Please select an option:");
        System.out.println("1. Filter");
        System.out.println("2. Find by ID");
        System.out.println("3. Exit");
    }

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
                    System.out.println("You have left the data search menu...");
                    displayMenu();
                    break;
                default:
                    System.out.println("Invalid input. Please try again.");
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
