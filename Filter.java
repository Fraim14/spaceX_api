// Maksym Shtymak 3151565

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Scanner;

public class Filter extends apiCalls {
    private final Scanner scanner = new Scanner(System.in);
    private final Settings settings = Settings.getInstance();
    private final Cache cache = new Cache();


    // this method is used to return to the main menu
    private void returnToMainMenu() {
        Menu menu = new Menu();
        // the user is asked if they want to leave the menu
        System.out.print(settings.formatPrompt("Return to main menu? (y/n): "));
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("y") || choice.equals("yes")) {
            menu.displayMenu();
        } else if (choice.equals("n") || choice.equals("no")) {
            System.out.println(settings.formatSuccess("Exiting program. Goodbye!"));
            System.exit(0);
        } else {
            System.out.println(settings.formatError("Invalid choice: " + choice));
            System.out.println(settings.formatHighlight("Please enter either 'y' or 'n'"));
            returnToMainMenu();
        }
    }

    // this method is used to filter the data
    public void filterData() {
        // the category is selected by the user and the filters are created for the selected category
        Category selectedCategory = selectCategory();
        // if the category is not null the filter is created and the filtering is executed
        if (selectedCategory != null) {
            JSONObject filters = createFilters(selectedCategory);
            JSONObject result = executeInitialQuery(selectedCategory, filters);

            // if the result is not null the results are displayed and processed
            if (result != null) {
                displayAndProcessResults(selectedCategory, result);
            }
            returnToMainMenu();
        }
    }

    // this method is used to perform the initial query filtering
    private JSONObject executeInitialQuery(Category selectedCategory, JSONObject filters) {
        try {
            JSONObject result = queryCategory(selectedCategory, filters);
            // this variable is used to store the total number of results after filtering
            int totalAvailable = result.getInt("totalDocs");
            // this condition executes the no results ui if no results are found
            if (totalAvailable == 0) {
                displayNoResultsFound(selectedCategory);
                return null;
            }

            System.out.println(settings.formatMenuHeader("Search Results"));
            System.out.println(settings.formatHighlight("Total available results: " + totalAvailable));
            // this variable is used to store the number of results to display based on user choice
            int limit = selectLimit(totalAvailable);
            // if limit is greater than 0 the query gets an extra limit of amount of results to be displayed
            if (limit > 0) {
                JSONObject options = new JSONObject();
                options.put("limit", limit);
                filters.put("options", options);
                result = queryCategory(selectedCategory, filters);
                // after the filtering if fully executed the results are saved into cache
                cache.saveOperation(selectedCategory.name(), "filter", result);
            }
            return result;
        } catch (Exception e) {
            System.out.println(settings.formatWarning("Cannot connect to the API. Checking cache..."));
            return cache.handleCachedResult(selectedCategory.name(), "filter");
        }
    }

    //    this method is used to display the results in different ways based on user's choice
    private void displayAndProcessResults(Category selectedCategory, JSONObject result) {
        System.out.println(settings.formatMenuHeader("View Options"));
        System.out.println(settings.formatMenuItem("1", "Select specific fields to display"));
        System.out.println(settings.formatMenuItem("2", "Show all information"));
        System.out.print(settings.formatPrompt("Enter your choice (1-2): "));

        if (scanner.hasNextInt()) {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            // this part displays only the fields selected by the user
            if (choice == 1) {
                displaySelectedFields(selectedCategory, result);
            } else {
                // this part displays all  information
                System.out.println(settings.formatMenuHeader("Complete Results"));
                displayAllInformation(selectedCategory, result);
            }
        }
    }

    // this method is used to select the category
    private Category selectCategory() {
        while (true) {
            displayCategoryMenu();

            try {
                if (!scanner.hasNextInt()) {
                    System.out.println(settings.formatError("Please enter a valid number."));
                    // clear the invalid input
                    scanner.nextLine();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();
                // this switch is used to select the category based on the user's choice
                return switch (choice) {
                    case 1 -> Category.LAUNCHES;
                    case 2 -> Category.ROCKETS;
                    case 3 -> Category.LAUNCHPADS;
                    case 4 -> Category.CREW;
                    case 5 -> Category.CAPSULES;
                    case 6 -> Category.STARLINK;
                    default -> {
                        System.out.println(settings.formatError("Invalid choice: " + choice));
                        System.out.println(settings.formatHighlight("Please enter a number between 1 and 6"));
                        yield null;
                    }
                };
            } catch (Exception e) {
                System.out.println(settings.formatError("Error selecting category: " + e.getMessage()));
                scanner.nextLine(); // Clear any bad input
            }
        }
    }

    // this is a ui part for the category selection menu
    private void displayCategoryMenu() {
        System.out.println(settings.formatMenuHeader("Select Category"));
        System.out.println(settings.formatMenuItem("1", "Launches"));
        System.out.println(settings.formatMenuItem("2", "Rockets"));
        System.out.println(settings.formatMenuItem("3", "Launchpads"));
        System.out.println(settings.formatMenuItem("4", "Crew"));
        System.out.println(settings.formatMenuItem("5", "Capsules"));
        System.out.println(settings.formatMenuItem("6", "Starlinks"));
        System.out.print("\n" + settings.formatPrompt("Enter your choice (1-6): "));
    }

    // this methods constraints the number of results that user want to see
    private int selectLimit(int totalAvailable) {
        while (true) {
            System.out.println(settings.formatMenuHeader("Select Number of Results"));
            System.out.println(settings.formatMenuItem("1", "Show all results"));
            System.out.println(settings.formatMenuItem("2", "Specify number of results"));

            // this part displays the maximum number of results
            String maxResults = String.format("Maximum available results: %d", totalAvailable);
            System.out.println(settings.formatBoxedInfo(maxResults));

            System.out.print("\n" + settings.formatPrompt("Enter your choice (1-2): "));

            if (!scanner.hasNextInt()) {
                String invalidInput = scanner.nextLine();
                System.out.println(settings.formatError("Invalid input: '" + invalidInput + "'"));
                System.out.println(settings.formatHighlight("Please enter either 1 or 2"));
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            // this condition displays all available results
            if (choice == 1) {
                return totalAvailable;
                // this part gives user an option to select the number of results
            } else if (choice == 2) {
                System.out.print(settings.formatPrompt("Enter number of results (1-" + totalAvailable + "): "));
                if (scanner.hasNextInt()) {
                    int limit = scanner.nextInt();
                    scanner.nextLine();
                    if (limit > 0 && limit <= totalAvailable) {
                        return limit;
                    }
                } else {
                    String invalidInput = scanner.nextLine();
                    System.out.println(settings.formatError("Invalid input: '" + invalidInput + "'"));
                }
                System.out.println(settings.formatHighlight("Please enter a number between 1 and " + totalAvailable));
            } else {
                System.out.println(settings.formatError("Invalid choice: " + choice));
                System.out.println(settings.formatHighlight("Please enter either 1 or 2"));
            }
        }
    }

    // this method is used to verify the boolean values written by the user
    private boolean getValidBoolean(String prompt) {
        while (true) {
            System.out.print(settings.formatPrompt(prompt));
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("true") || input.equals("false")) {
                // parse the value into boolean to ensure that it will be boolean
                return Boolean.parseBoolean(input);
            }
            System.out.println(settings.formatError("Invalid input: '" + input + "'"));
            System.out.println(settings.formatHighlight("Please enter either 'true' or 'false'"));
        }
    }

    // this method is responsible for handling the string inputs
    private String getValidInput(String prompt) {
        while (true) {
            System.out.print(settings.formatPrompt(prompt));
            // remove the extra redundant spaces
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(settings.formatError("Input cannot be empty"));
            System.out.println(settings.formatHighlight("Please enter a valid value"));
        }
    }

    // this method is responsible for handling the choices in the menus
    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                System.out.print(settings.formatPrompt("Enter your choice (" + min + "-" + max + "): "));
                if (!scanner.hasNextInt()) {
                    String invalidInput = scanner.nextLine();
                    System.out.println(settings.formatError("Invalid input: '" + invalidInput + "'"));
                    System.out.println(settings.formatHighlight("Please enter a number between " + min + " and " + max));
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine();
                // min and max are the number of options in the menu
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println(settings.formatError("Invalid number: " + choice));
                System.out.println(settings.formatHighlight("Please enter a number between " + min + " and " + max));
            } catch (Exception e) {
                System.out.println(settings.formatError("Invalid input format"));
                System.out.println(settings.formatHighlight("Please enter a valid number"));
                scanner.nextLine();
            }
        }
    }

    // this method is used to create the filters for selected category
    private JSONObject createFilters(Category category) {
        // this is the main query object that is used to store the filters
        JSONObject filters = new JSONObject();
        // this is the query object that is used to store the filters
        JSONObject query = new JSONObject();

        System.out.println(settings.formatMenuHeader("Create Filters"));

        // this switch is used to select the category based on the user's choice
        switch (category) {
            case LAUNCHES:
                createLaunchFilters(query);
                break;
            case ROCKETS:
                createRocketFilters(query);
                break;
            case LAUNCHPADS:
                createLaunchpadFilters(query);
                break;
            case CREW:
                createCrewFilters(query);
                break;
            case CAPSULES:
                createCapsuleFilters(query);
                break;
            case STARLINK:
                createStarlinkFilters(query);
                break;
        }
        // send the filters to api query file
        filters.put("query", query);
        return filters;
    }

    // now there wil be 6 methods for creation the methods which will have quite the same code
    private void createLaunchFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Launch Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by success"));
        System.out.println(settings.formatMenuItem("2", "Filter by upcoming"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        // this switch is used to display basic filters for each category(there are 2 of them for each category)
        switch (choice) {
            case 1 -> {
                System.out.println(settings.formatHighlight("\nEnter 'true' for successful launches, 'false' for failed launches"));
                boolean success = getValidBoolean(settings.formatPrompt("Success (true/false): "));
                // add the filter to the query
                query.put("success", success);
                System.out.println(settings.formatSuccess("Filter applied: success = " + success));
            }
            case 2 -> {
                System.out.println(settings.formatHighlight("\nEnter 'true' for upcoming launches, 'false' for past launches"));
                boolean upcoming = getValidBoolean(settings.formatPrompt("Upcoming (true/false): "));
                query.put("upcoming", upcoming);
                System.out.println(settings.formatSuccess("Filter applied: upcoming = " + upcoming));
            }
            // this case is responsible for displaying both filters and handling both of their outputs
            case 3 -> {
                System.out.println(settings.formatHighlight("\nEnter 'true' for successful launches, 'false' for failed launches"));
                boolean success = getValidBoolean(settings.formatPrompt("Success (true/false): "));
                System.out.println(settings.formatHighlight("\nEnter 'true' for upcoming launches, 'false' for past launches"));
                boolean upcoming = getValidBoolean(settings.formatPrompt("Upcoming (true/false): "));
                query.put("success", success);
                query.put("upcoming", upcoming);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Success: " + success));
                System.out.println(settings.formatHighlight("• Upcoming: " + upcoming));
            }
        }
    }

    // these are the filters for the rocket category
    private void createRocketFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Rocket Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by active status"));
        System.out.println(settings.formatMenuItem("2", "Filter by type"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        System.out.print("\n" + settings.formatPrompt("Enter your choice (1-3): "));
        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        switch (choice) {
            case 1 -> {
                boolean active = getValidBoolean(settings.formatPrompt("Active (true/false): "));
                query.put("active", active);
                System.out.println(settings.formatSuccess("Filter applied: active = " + active));
            }
            case 2 -> {
                System.out.println(settings.formatHighlight("\nAvailable types:"));
                System.out.println(settings.formatMenuItem("•", "v1.0 - Falcon 9 Block 1"));
                System.out.println(settings.formatMenuItem("•", "v1.1 - Falcon 9 Block 2"));
                System.out.println(settings.formatMenuItem("•", "v1.2 - Falcon 9 Block 3"));
                String type = getValidInput(settings.formatPrompt("Type: "));
                query.put("type", type);
                System.out.println(settings.formatSuccess("Filter applied: type = " + type));
            }
            case 3 -> {
                boolean active = getValidBoolean(settings.formatPrompt("Active (true/false): "));
                System.out.println(settings.formatHighlight("\nAvailable types:"));
                System.out.println(settings.formatMenuItem("•", "v1.0 - Falcon 9 Block 1"));
                System.out.println(settings.formatMenuItem("•", "v1.1 - Falcon 9 Block 2"));
                System.out.println(settings.formatMenuItem("•", "v1.2 - Falcon 9 Block 3"));
                String type = getValidInput(settings.formatPrompt("Type: "));
                query.put("active", active);
                query.put("type", type);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Active: " + active));
                System.out.println(settings.formatHighlight("• Type: " + type));
            }
        }
    }


    // these are the filters for the launchpad category
    private void createLaunchpadFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Launchpad Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by status"));
        System.out.println(settings.formatMenuItem("2", "Filter by region"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        System.out.print("\n" + settings.formatPrompt("Enter your choice (1-3): "));
        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        switch (choice) {
            case 1 -> {
                System.out.println(settings.formatHighlight("\nAvailable statuses:"));
                System.out.println(settings.formatMenuItem("•", "active   - Currently operational"));
                System.out.println(settings.formatMenuItem("•", "inactive - Not in use"));
                String status = getValidInput(settings.formatPrompt("Status (active/inactive): "));
                query.put("status", status);
                System.out.println(settings.formatSuccess("Filter applied: status = " + status));
            }
            case 2 -> {
                System.out.println(settings.formatMenuHeader("Available Regions"));
                System.out.println(settings.formatMenuItem("•", "Florida"));
                System.out.println(settings.formatHighlight("  ↳ Cape Canaveral"));
                System.out.println(settings.formatHighlight("  ↳ Kennedy Space Center"));
                System.out.println(settings.formatMenuItem("•", "Texas"));
                System.out.println(settings.formatHighlight("  ↳ Boca Chica"));
                System.out.println(settings.formatHighlight("  ↳ McGregor"));
                System.out.println(settings.formatMenuItem("•", "California"));
                System.out.println(settings.formatHighlight("  ↳ Vandenberg"));
                String region = getValidInput(settings.formatPrompt("Region: "));
                query.put("region", region);
                System.out.println(settings.formatSuccess("Filter applied: region = " + region));
            }
            case 3 -> {
                System.out.println(settings.formatHighlight("\nAvailable statuses:"));
                System.out.println(settings.formatMenuItem("•", "active   - Currently operational"));
                System.out.println(settings.formatMenuItem("•", "inactive - Not in use"));
                String status = getValidInput(settings.formatPrompt("Status (active/inactive): "));

                System.out.println(settings.formatMenuHeader("Available Regions"));
                System.out.println(settings.formatMenuItem("•", "Florida"));
                System.out.println(settings.formatHighlight("  ↳ Cape Canaveral"));
                System.out.println(settings.formatHighlight("  ↳ Kennedy Space Center"));
                System.out.println(settings.formatMenuItem("•", "Texas"));
                System.out.println(settings.formatHighlight("  ↳ Boca Chica"));
                System.out.println(settings.formatHighlight("  ↳ McGregor"));
                System.out.println(settings.formatMenuItem("•", "California"));
                System.out.println(settings.formatHighlight("  ↳ Vandenberg"));
                String region = getValidInput(settings.formatPrompt("Region: "));

                query.put("status", status);
                query.put("region", region);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Status: " + status));
                System.out.println(settings.formatHighlight("• Region: " + region));
            }
        }
    }

    // these are the filters for the crew category
    private void createCrewFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Crew Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by agency"));
        System.out.println(settings.formatMenuItem("2", "Filter by status"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        switch (choice) {
            case 1 -> {
                System.out.println(settings.formatHighlight("\nMajor Space Agencies:"));
                System.out.println(settings.formatMenuItem("•", "NASA    - National Aeronautics and Space Administration"));
                System.out.println(settings.formatMenuItem("•", "SpaceX  - Space Exploration Technologies Corp"));
                System.out.println(settings.formatMenuItem("•", "ESA     - European Space Agency"));
                System.out.println(settings.formatMenuItem("•", "JAXA    - Japan Aerospace Exploration Agency"));
                System.out.println(settings.formatMenuItem("•", "CSA     - Canadian Space Agency"));
                String agency = getValidInput(settings.formatPrompt("Agency: "));
                query.put("agency", agency);
                System.out.println(settings.formatSuccess("Filter applied: agency = " + agency));
            }
            case 2 -> {
                System.out.println(settings.formatHighlight("\nAvailable Statuses:"));
                System.out.println(settings.formatMenuItem("•", "active    - Currently assigned to missions"));
                System.out.println(settings.formatMenuItem("•", "inactive  - Retired or no longer flying"));
                System.out.println(settings.formatMenuItem("•", "training  - In preparation for future missions"));
                String status = getValidInput(settings.formatPrompt("Status: "));
                query.put("status", status);
                System.out.println(settings.formatSuccess("Filter applied: status = " + status));
            }
            case 3 -> {
                System.out.println(settings.formatHighlight("\nMajor Space Agencies:"));
                System.out.println(settings.formatMenuItem("•", "NASA    - National Aeronautics and Space Administration"));
                System.out.println(settings.formatMenuItem("•", "SpaceX  - Space Exploration Technologies Corp"));
                System.out.println(settings.formatMenuItem("•", "ESA     - European Space Agency"));
                System.out.println(settings.formatMenuItem("•", "JAXA    - Japan Aerospace Exploration Agency"));
                System.out.println(settings.formatMenuItem("•", "CSA     - Canadian Space Agency"));
                String agency = getValidInput(settings.formatPrompt("Agency: "));

                System.out.println(settings.formatHighlight("\nAvailable Statuses:"));
                System.out.println(settings.formatMenuItem("•", "active    - Currently assigned to missions"));
                System.out.println(settings.formatMenuItem("•", "inactive  - Retired or no longer flying"));
                System.out.println(settings.formatMenuItem("•", "training  - In preparation for future missions"));
                String status = getValidInput(settings.formatPrompt("Status: "));

                query.put("agency", agency);
                query.put("status", status);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Agency: " + agency));
                System.out.println(settings.formatHighlight("• Status: " + status));
            }
        }
    }


    // these are the filters for the capsule category
    private void createCapsuleFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Capsule Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by status"));
        System.out.println(settings.formatMenuItem("2", "Filter by type"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        switch (choice) {
            case 1 -> {
                System.out.println(settings.formatHighlight("\nAvailable Statuses:"));
                System.out.println(settings.formatMenuItem("•", "active    - Currently in service"));
                System.out.println(settings.formatMenuItem("•", "retired   - No longer in operation"));
                System.out.println(settings.formatMenuItem("•", "destroyed - Lost during mission"));
                System.out.println(settings.formatMenuItem("•", "unknown   - Status not confirmed"));
                String status = getValidInput(settings.formatPrompt("Status: "));
                query.put("status", status);
                System.out.println(settings.formatSuccess("Filter applied: status = " + status));
            }
            case 2 -> {
                System.out.println(settings.formatHighlight("\nCapsule Types:"));
                System.out.println(settings.formatMenuItem("•", "Dragon 1.0  - First generation cargo capsule"));
                System.out.println(settings.formatMenuItem("•", "Dragon 1.1  - Updated cargo variant"));
                System.out.println(settings.formatMenuItem("•", "Dragon 2.0  - Second generation capsule"));
                System.out.println(settings.formatMenuItem("•", "Crew Dragon - Human-rated capsule"));
                String type = getValidInput(settings.formatPrompt("Type: "));
                query.put("type", type);
                System.out.println(settings.formatSuccess("Filter applied: type = " + type));
            }
            case 3 -> {
                System.out.println(settings.formatHighlight("\nAvailable Statuses:"));
                System.out.println(settings.formatMenuItem("•", "active    - Currently in service"));
                System.out.println(settings.formatMenuItem("•", "retired   - No longer in operation"));
                System.out.println(settings.formatMenuItem("•", "destroyed - Lost during mission"));
                System.out.println(settings.formatMenuItem("•", "unknown   - Status not confirmed"));
                String status = getValidInput(settings.formatPrompt("Status: "));

                System.out.println(settings.formatHighlight("\nCapsule Types:"));
                System.out.println(settings.formatMenuItem("•", "Dragon 1.0  - First generation cargo capsule"));
                System.out.println(settings.formatMenuItem("•", "Dragon 1.1  - Updated cargo variant"));
                System.out.println(settings.formatMenuItem("•", "Dragon 2.0  - Second generation capsule"));
                System.out.println(settings.formatMenuItem("•", "Crew Dragon - Human-rated capsule"));
                String type = getValidInput(settings.formatPrompt("Type: "));

                query.put("status", status);
                query.put("type", type);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Status: " + status));
                System.out.println(settings.formatHighlight("• Type: " + type));
            }
        }
    }

    // these are the filters for the starlink category
    private void createStarlinkFilters(JSONObject query) {
        System.out.println(settings.formatMenuHeader("Starlink Filters"));
        System.out.println(settings.formatMenuItem("1", "Filter by version"));
        System.out.println(settings.formatMenuItem("2", "Filter by launch date"));
        System.out.println(settings.formatMenuItem("3", "Both filters"));

        int choice = getValidChoice(1, 3);

        System.out.println(settings.formatBoxedInfo("Configure Filters"));
        switch (choice) {
            case 1 -> {
                System.out.println(settings.formatHighlight("\nStarlink Versions:"));
                System.out.println(settings.formatMenuItem("•", "v0.9   - Prototype satellites"));
                System.out.println(settings.formatMenuItem("•", "v1.0   - First operational version"));
                System.out.println(settings.formatMenuItem("•", "v1.5   - Enhanced bandwidth variant"));
                System.out.println(settings.formatMenuItem("•", "v2.0   - Second generation satellites"));
                String version = getValidInput(settings.formatPrompt("Version: "));
                query.put("version", version);
                System.out.println(settings.formatSuccess("Filter applied: version = " + version));
            }
            case 2 -> {
                System.out.println(settings.formatHighlight("\nDate Format: YYYY-MM-DD"));
                System.out.println(settings.formatMenuItem("•", "Example: 2020-01-30"));
                System.out.println(settings.formatMenuItem("•", "Note: Must be a past or present date"));
                String launchDate = getValidInput(settings.formatPrompt("Launch date: "));
                query.put("launch_date", launchDate);
                System.out.println(settings.formatSuccess("Filter applied: launch date = " + launchDate));
            }
            case 3 -> {
                System.out.println(settings.formatHighlight("\nStarlink Versions:"));
                System.out.println(settings.formatMenuItem("•", "v0.9   - Prototype satellites"));
                System.out.println(settings.formatMenuItem("•", "v1.0   - First operational version"));
                System.out.println(settings.formatMenuItem("•", "v1.5   - Enhanced bandwidth variant"));
                System.out.println(settings.formatMenuItem("•", "v2.0   - Second generation satellites"));
                String version = getValidInput(settings.formatPrompt("Version: "));

                System.out.println(settings.formatHighlight("\nDate Format: YYYY-MM-DD"));
                System.out.println(settings.formatMenuItem("•", "Example: 2020-01-30"));
                System.out.println(settings.formatMenuItem("•", "Note: Must be a past or present date"));
                String launchDate = getValidInput(settings.formatPrompt("Launch date: "));

                query.put("version", version);
                query.put("launch_date", launchDate);
                System.out.println(settings.formatSuccess("Filters applied:"));
                System.out.println(settings.formatHighlight("• Version: " + version));
                System.out.println(settings.formatHighlight("• Launch date: " + launchDate));
            }
        }
    }

    // this method is used to show  the user a message if no results are found with possibles causes of this promblem
    private void displayNoResultsFound(Category selectedCategory) {
        System.out.println(settings.formatMenuHeader("No Results Found"));
        System.out.println(settings.formatError("No matching results were found for your search criteria"));

        System.out.println(settings.formatMenuHeader("Possible Reasons"));
        System.out.println(settings.formatMenuItem("•", "Your filter criteria might be too restrictive"));
        System.out.println(settings.formatMenuItem("•", "The combination of filters might not match any data"));
        System.out.println(settings.formatMenuItem("•", "The requested data might not exist in the database"));

        System.out.println(settings.formatMenuHeader("Suggestions"));
        System.out.println(settings.formatMenuItem("•", "Try broadening your search criteria"));
        System.out.println(settings.formatMenuItem("•", "Check if the values are correctly formatted"));
        System.out.println(settings.formatMenuItem("•", "Try using fewer filters"));
    }

    // this method is used to display the particular fields of the result, which user has selected
    private void displaySelectedFields(Category category, JSONObject result) {
        JSONArray docs = result.getJSONArray("docs");
        // this condition ensures that result exists
        if (docs.length() == 0) {
            System.out.println(settings.formatError("No matching records found"));
            return;
        }

        // this array is used to store the avaliable fields for each category, which user can choose
        String[] availableFields;
        switch (category) {
            case LAUNCHES ->
                    availableFields = new String[]{"name", "flight_number", "date_utc", "success", "details", "links", "id"};
            case ROCKETS ->
                    availableFields = new String[]{"name", "type", "active", "description", "height", "wikipedia", "id"};
            case LAUNCHPADS -> availableFields = new String[]{"name", "full_name", "region", "status", "details", "id"};
            case CREW -> availableFields = new String[]{"name", "agency", "status", "launches", "wikipedia", "id"};
            case CAPSULES ->
                    availableFields = new String[]{"serial", "status", "type", "last_update", "launches", "id"};
            case STARLINK ->
                    availableFields = new String[]{"version", "launch_date", "longitude", "latitude", "height_km", "velocity_kms", "id"};
            default -> availableFields = new String[]{};
        }

        while (true) {
            //this part is responsible for the available fields
            System.out.println(settings.formatMenuHeader("Available Fields"));
            for (int i = 0; i < availableFields.length; i++) {
                String fieldName = availableFields[i];
                // this part formats the field name properly
                String displayName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1).replace("_", " ");
                System.out.println(settings.formatMenuItem((i + 1) + "", displayName));
            }

            // Get user's field selections
            System.out.print(settings.formatPrompt("Select fields to display (enter numbers separated by spaces): "));
            // ensure that the input is in valid format
            String[] selections = scanner.nextLine().trim().split("\\s+");

            // ensure that the user input will be valid
            boolean hasInvalidSelection = false;
            for (String selection : selections) {
                try {
                    int fieldIndex = Integer.parseInt(selection) - 1;
                    if (fieldIndex < 0 || fieldIndex >= availableFields.length) {
                        System.out.println(settings.formatError("Invalid selection: " + selection));
                        System.out.println(settings.formatHighlight("Please enter numbers between 1 and " + availableFields.length));
                        hasInvalidSelection = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(settings.formatError("Invalid input: '" + selection + "' is not a number"));
                    System.out.println(settings.formatHighlight("Please enter valid numbers separated by spaces"));
                    hasInvalidSelection = true;
                    break;
                }
            }

            // If any selection was invalid, continue the loop
            if (hasInvalidSelection) {
                System.out.println();
                continue;
            }

            //  display results
            System.out.println(settings.formatMenuHeader("Results"));
            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i);

                for (String selection : selections) {
                    int fieldIndex = Integer.parseInt(selection) - 1;
                    String fieldName = availableFields[fieldIndex];
                    String displayName = fieldName.substring(0, 1).toUpperCase() +
                            fieldName.substring(1).replace("_", " ");

                    // Special handling for links in launches
                    if (category == Category.LAUNCHES && fieldName.equals("links")) {
                        JSONObject links = doc.getJSONObject("links");
                        String wikiLink = links.optString("wikipedia", "N/A");
                        System.out.println(settings.formatMenuItem("Wikipedia Link", wikiLink));
                    }
                    // Regular fields and replacing the null value with N/A
                    else {
                        Object value = doc.has(fieldName) ? doc.get(fieldName) : "N/A";
                        System.out.println(settings.formatMenuItem(displayName, value.toString()));
                    }
                }

                // Add separator between results if not the last one
                if (i < docs.length() - 1) {
                    System.out.println(settings.formatHighlight("─".repeat(50)));
                }
            }

            break;
        }
    }

    // this method is used to find the object by id
    public void findById() {
        Category selectedCategory = selectCategory();
        // ensure that the selected category is not null
        if (selectedCategory != null) {
            try {
                System.out.println(settings.formatMenuHeader("Find " + selectedCategory.name() + " by ID"));

                // Display ID format example based on category
                System.out.println(settings.formatHighlight("ID Format Example:"));
                // exapmles of id for each category
                switch (selectedCategory) {
                    case LAUNCHES -> System.out.println(settings.formatMenuItem("•", "5eb87cd9ffd86e000604b32a"));
                    case ROCKETS -> System.out.println(settings.formatMenuItem("•", "5e9d0d95eda69955f709d1eb"));
                    case LAUNCHPADS -> System.out.println(settings.formatMenuItem("•", "5e9e4501f509094ba4566f84"));
                    case CREW -> System.out.println(settings.formatMenuItem("•", "5ebf1a6e23a9a60006e03a7a"));
                    case CAPSULES -> System.out.println(settings.formatMenuItem("•", "5e9e2c5bf35918ed873b2664"));
                    case STARLINK -> System.out.println(settings.formatMenuItem("•", "5eed770f096e59000698560d"));
                }

                String id = getValidInput(settings.formatPrompt("Enter ID: "));

                try {
                    JSONObject result = getItemById(selectedCategory, id);
                    System.out.println(settings.formatMenuHeader("View Options"));
                    System.out.println(settings.formatMenuItem("1", "Select specific fields to display"));
                    System.out.println(settings.formatMenuItem("2", "Show all information"));
                    System.out.print(settings.formatPrompt("Enter your choice (1-2): "));

                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Clear buffer

                        // Create a JSONObject structure similar to query results
                        JSONObject formattedResult = new JSONObject();
                        JSONArray docsArray = new JSONArray();
                        docsArray.put(result);
                        formattedResult.put("docs", docsArray);

                        if (choice == 1) {
                            displaySelectedFields(selectedCategory, formattedResult);
                        } else {
                            System.out.println(settings.formatMenuHeader("Complete Information"));
                            displayAllInformation(selectedCategory, formattedResult);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(settings.formatError("Item not found"));
                    System.out.println(settings.formatMenuHeader("Troubleshooting"));
                    System.out.println(settings.formatMenuItem("•", "Check if the ID is correct"));
                    System.out.println(settings.formatMenuItem("•", "Verify the ID format"));
                    System.out.println(settings.formatMenuItem("•", "Make sure the item exists"));
                }
            } catch (Exception e) {
                System.out.println(settings.formatError("Error: " + e.getMessage()));
            }
            // if the execution of the code is successful the user is asked to return to the main menu
            returnToMainMenu();
        }
    }

    // this method is used to display all information about the  object if user selects this option
    private void displayAllInformation(Category category, JSONObject result) {
        JSONArray docs = result.getJSONArray("docs");
        // this list is used to store the information about the object from the jsonArray
        List<DTO> dtoList = DTO.fromJSONArray(docs, category);

        // Display each result
        for (int i = 0; i < dtoList.size(); i++) {
            //. styled header for each result
            System.out.println(settings.formatMenuHeader("Result " + (i + 1)));

            // Split the information into lines and format each line
            String[] lines = dtoList.get(i).displayAllInformation(category).split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    System.out.println(settings.formatMenuItem(parts[0].trim(), parts[1].trim()));
                } else {
                    System.out.println(settings.formatHighlight(line));
                }
            }

            // Add separator between results if not the last one
            if (i < dtoList.size() - 1) {
                System.out.println(settings.formatHighlight("─".repeat(50)));
            }
        }
    }

}


