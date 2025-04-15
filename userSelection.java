
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Scanner;

public class userSelection extends apiCalls {
    private final Scanner scanner = new Scanner(System.in);

    public void displayItemById() {

    }

    public void filterData() {
        Category selectedCategory = selectCategory();
        if (selectedCategory != null) {
            JSONObject filters = createFilters(selectedCategory);

            try {
                // First get total count without limit
                JSONObject initialResult = queryCategory(selectedCategory, filters);
                int totalAvailable = initialResult.getInt("totalDocs");

                System.out.println("\nTotal available results: " + totalAvailable);

                if (totalAvailable == 0) {
                    displayNoResultsFound(selectedCategory);
                }
                int limit = selectLimit(totalAvailable);
                if (limit > 0) {
                    JSONObject options = new JSONObject();
                    options.put("limit", limit);
                    filters.put("options", options);

                    // Query again with limit if needed
                    initialResult = queryCategory(selectedCategory, filters);
                }

                if (initialResult.getJSONArray("docs").length() == 0) {
                    displayNoResultsFound(selectedCategory);
                } else {
                    System.out.println("\n=== Initial Filter Results ===");
                    System.out.println("Displaying " + initialResult.getJSONArray("docs").length() +
                            " out of " + totalAvailable + " total results");

                    // Display options
                    System.out.println("\nHow would you like to view the results?");
                    System.out.println("1. Select specific fields to display");
                    System.out.println("2. Show all information");
                    System.out.print("Enter your choice (1-2): ");

                    if (scanner.hasNextInt()) {
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Clear buffer

                        if (choice == 1) {
                            displaySelectedFields(selectedCategory, initialResult);
                        } else {
                            // Show all results
                            System.out.println("\n=== Complete Results ===");
                            System.out.println(initialResult.toString(2));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private Category selectCategory() {
        while (true) {
            try {
                System.out.println("\n=== Select Category ===");
                System.out.println("1. Launches");
                System.out.println("2. Rockets");
                System.out.println("3. Launchpads");
                System.out.println("4. Crew");
                System.out.println("5. Capsules");
                System.out.println("6. Exit");

                System.out.print("Enter your choice 1-6: ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    scanner.nextLine();
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        return Category.LAUNCHES;
                    case 2:
                        return Category.ROCKETS;
                    case 3:
                        return Category.LAUNCHPADS;
                    case 4:
                        return Category.CREW;
                    case 5:
                        return Category.CAPSULES;
                    case 6:
                        System.out.println("You have left the select category menu...");
                        return null;
                    default:
                        System.out.println("Please enter a number between 1 and 6.");
                        continue;
                }
            } catch (Exception e) {
                System.out.println("Error selecting category: " + e.getMessage());
                scanner.nextLine(); // Clear any bad input
            }
        }
    }

    private int selectLimit(int totalAvailable) {
        while (true) {
            try {
                System.out.println("\n=== Select Number of Results ===");
                System.out.println("1. Show all results");
                System.out.println("2. Specify number of results");
                System.out.println("(Maximum available results: " + totalAvailable + ")");

                System.out.print("Enter your choice (1-2): ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    scanner.nextLine(); // Clear invalid input
                    continue;
                }

                int choice = scanner.nextInt();
                scanner.nextLine(); // Clear buffer

                switch (choice) {
                    case 1:
                        return 0;
                    case 2:
                        while (true) {
                            System.out.printf("Enter number of results to show (1-%d): ", totalAvailable);
                            if (!scanner.hasNextInt()) {
                                System.out.println("Please enter a valid number.");
                                scanner.nextLine();
                                continue;
                            }
                            int limit = scanner.nextInt();
                            scanner.nextLine();
                            if (limit <= 0) {
                                System.out.println("Please enter a positive number.");
                                continue;
                            }
                            if (limit > totalAvailable) {
                                System.out.printf("Please enter a number not exceeding %d.\n", totalAvailable);
                                continue;
                            }
                            return limit;
                        }
                    default:
                        System.out.println("Please enter either 1 or 2.");
                }
            } catch (Exception e) {
                System.out.println("Error selecting limit: " + e.getMessage());
                scanner.nextLine(); // Clear any bad input
            }
        }
    }

    private boolean getValidBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase().trim();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            }
            System.out.println("Please enter either 'true' or 'false'.");
        }
    }

    private String getValidInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    private int getValidChoice(int min, int max) {
        while (true) {
            try {
                System.out.print("Enter choice: ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number.");
                    scanner.nextLine();
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine();
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Please enter a number between %d and %d.\n", min, max);
            } catch (Exception e) {
                System.out.println("Invalid input. Please try again.");
                scanner.nextLine();
            }
        }
    }

    private JSONObject createFilters(Category category) {
        JSONObject filters = new JSONObject();
        JSONObject query = new JSONObject();

        System.out.println("\n=== Create Filters ===");

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
        }

        filters.put("query", query);
        return filters;
    }

    private void createLaunchFilters(JSONObject query) {
        System.out.println("Select launch filters:");
        System.out.println("1. Filter by success");
        System.out.println("2. Filter by upcoming");
        System.out.println("3. Both filters");

        int choice = getValidChoice(1, 3);
        switch (choice) {
            case 1:
                query.put("success", getValidBoolean("Success (true/false): "));
                break;
            case 2:
                query.put("upcoming", getValidBoolean("Upcoming (true/false): "));
                break;
            case 3:
                query.put("success", getValidBoolean("Success (true/false): "));
                query.put("upcoming", getValidBoolean("Upcoming (true/false): "));
                break;
        }
    }

    private void createRocketFilters(JSONObject query) {
        System.out.println("Select rocket filters:");
        System.out.println("1. Filter by active status");
        System.out.println("2. Filter by type");
        System.out.println("3. Both filters");

        int choice = getValidChoice(1, 3);
        switch (choice) {
            case 1:
                query.put("active", getValidBoolean("Active (true/false): "));
                break;
            case 2:
                query.put("type", getValidInput("Type: "));
                break;
            case 3:
                query.put("active", getValidBoolean("Active (true/false): "));
                query.put("type", getValidInput("Type: "));
                break;
        }
    }


    private void createLaunchpadFilters(JSONObject query) {
        System.out.println("Select launchpad filters:");
        System.out.println("1. Filter by status");
        System.out.println("2. Filter by region");
        System.out.println("3. Both filters");

        int choice = getValidChoice(1, 3);
        switch (choice) {
            case 1:
                query.put("status", getValidInput("Status (active/inactive): "));
                break;
            case 2:
                System.out.println("\nAvailable regions:");
                System.out.println("- Florida: Cape Canaveral, Kennedy Space Center");
                System.out.println("- Texas: Boca Chica, McGregor");
                System.out.println("- California: Vandenberg");
                System.out.println("- Pacific Ocean: Kwajalein Atoll");
                query.put("region", getValidInput("Region (e.g., Florida, Texas, California): "));
                break;
            case 3:
                query.put("status", getValidInput("Status (active/inactive): "));
                System.out.println("\nAvailable regions:");
                System.out.println("- Florida: Cape Canaveral, Kennedy Space Center");
                System.out.println("- Texas: Boca Chica, McGregor");
                System.out.println("- California: Vandenberg");
                System.out.println("- Pacific Ocean: Kwajalein Atoll");
                query.put("region", getValidInput("Region (e.g., Florida, Texas, California): "));
                break;
        }
    }

    private void createCrewFilters(JSONObject query) {
        System.out.println("Select crew filters:");
        System.out.println("1. Filter by agency");
        System.out.println("2. Filter by status");
        System.out.println("3. Both filters");

        int choice = getValidChoice(1, 3);
        switch (choice) {
            case 1:
                query.put("agency", getValidInput("Agency (e.g., NASA, SpaceX): "));
                break;
            case 2:
                query.put("status", getValidInput("Status (active/inactive): "));
                break;
            case 3:
                query.put("agency", getValidInput("Agency (e.g., NASA, SpaceX): "));
                query.put("status", getValidInput("Status (active/inactive): "));
                break;
        }
    }


    private void createCapsuleFilters(JSONObject query) {
        System.out.println("Select capsule filters:");
        System.out.println("1. Filter by status");
        System.out.println("2. Filter by type");
        System.out.println("3. Both filters");

        int choice = getValidChoice(1, 3);
        switch (choice) {
            case 1:
                query.put("status", getValidInput("Status (active/retired/unknown): "));
                break;
            case 2:
                query.put("type", getValidInput("Type (e.g., Dragon 1.0, Dragon 2.0): "));
                break;
            case 3:
                query.put("status", getValidInput("Status (active/retired/unknown): "));
                query.put("type", getValidInput("Type (e.g., Dragon 1.0, Dragon 2.0): "));
                break;
        }
    }

    private void displayNoResultsFound(Category selectedCategory) {
        System.out.println("\n┌──────────────────────────────────────┐");
        System.out.println("│          No Results Found            │");
        System.out.println("└──────────────────────────────────────┘");
        System.out.println("\nPossible reasons:");
        System.out.println("• Your filter criteria might be too restrictive");
        System.out.println("• The combination of filters might not match any data");

        System.out.println("\nSuggestions for " + selectedCategory.name() + ":");
        switch (selectedCategory) {
            case LAUNCHES:
                System.out.println("• Try different success/upcoming combinations");
                System.out.println("• Most launches are either upcoming or have a success status");
                break;
            case ROCKETS:
                System.out.println("• Common rocket types: 'v1.0', 'v1.1', 'v1.2'");
                System.out.println("• Try checking active rockets (active: true)");
                break;
            case LAUNCHPADS:
                System.out.println("• Available regions: Florida, Texas, California");
                System.out.println("• Status can be 'active' or 'inactive'");
                break;
            case CREW:
                System.out.println("• Common agencies: 'NASA', 'SpaceX'");
                System.out.println("• Status is typically 'active' or 'inactive'");
                break;
            case CAPSULES:
                System.out.println("• Types include: 'Dragon 1.0', 'Dragon 2.0'");
                System.out.println("• Status can be 'active', 'retired', or 'unknown'");
                break;
        }
        System.out.println("\nTry modifying your filters and search again!");
    }

    private void displaySelectedFields(Category category, JSONObject result) {
        String[] availableFields;

        // Define available fields based on category
        switch (category) {
            case LAUNCHES:
                availableFields = new String[]{"name", "flight_number", "date_utc", "success", "details", "links", "id"};
                System.out.println("\nAvailable Launch fields:");
                System.out.println("1. Name (Launch name)");
                System.out.println("2. Flight Number");
                System.out.println("3. Date");
                System.out.println("4. Success Status");
                System.out.println("5. Details");
                System.out.println("6. Wikipedia Link");
                System.out.println("7. ID");
                break;
            case ROCKETS:
                availableFields = new String[]{"name", "type", "active", "description", "height", "wikipedia", "id"};
                System.out.println("\nAvailable Rocket fields:");
                System.out.println("1. Name");
                System.out.println("2. Type");
                System.out.println("3. Active Status");
                System.out.println("4. Description");
                System.out.println("5. Height");
                System.out.println("6. Wikipedia Link");
                System.out.println("7. ID");
                break;
            case LAUNCHPADS:
                availableFields = new String[]{"name", "full_name", "region", "status", "details", "id"};
                System.out.println("\nAvailable Launchpad fields:");
                System.out.println("1. Name");
                System.out.println("2. Full Name");
                System.out.println("3. Region");
                System.out.println("4. Status");
                System.out.println("5. Details");
                System.out.println("6. ID");
                break;
            case CREW:
                availableFields = new String[]{"name", "agency", "status", "launches", "wikipedia", "id"};
                System.out.println("\nAvailable Crew fields:");
                System.out.println("1. Name");
                System.out.println("2. Agency");
                System.out.println("3. Status");
                System.out.println("4. Launches");
                System.out.println("5. Wikipedia Link");
                System.out.println("6. ID");
                break;
            case CAPSULES:
                availableFields = new String[]{"serial", "type", "status", "launches", "last_update", "id"};
                System.out.println("\nAvailable Capsule fields:");
                System.out.println("1. Serial Number");
                System.out.println("2. Type");
                System.out.println("3. Status");
                System.out.println("4. Launches");
                System.out.println("5. Last Update");
                System.out.println("6. ID");
                break;
            default:
                return;
        }

        // Get user's field selections
        System.out.println("\nSelect fields to display (enter numbers separated by spaces)");
        System.out.println("Example: '1 3 4' to show fields 1, 3, and 4");
        System.out.print("Enter your selection: ");

        String[] selections = scanner.nextLine().trim().split("\\s+");
        JSONArray docs = result.getJSONArray("docs");

        // Display selected fields for each document
        System.out.println("\n=== Selected Fields Results ===");
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            System.out.println("\nResult " + (i + 1) + ":");
            System.out.println("------------------------");

            for (String selection : selections) {
                try {
                    int fieldIndex = Integer.parseInt(selection) - 1;
                    if (fieldIndex >= 0 && fieldIndex < availableFields.length) {
                        String fieldName = availableFields[fieldIndex];
                        String displayName = fieldName.substring(0, 1).toUpperCase() +
                                fieldName.substring(1).replace("_", " ");

                        // Special handling for links in launches
                        if (category == Category.LAUNCHES && fieldName.equals("links")) {
                            JSONObject links = doc.getJSONObject("links");
                            String wikiLink = links.optString("wikipedia", "N/A");
                            System.out.println("Wikipedia Link: " + wikiLink);
                        }
                        // Regular fields
                        else {
                            Object value = doc.has(fieldName) ? doc.get(fieldName) : "N/A";
                            System.out.println(displayName + ": " + value);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid selection: " + selection);
                }
            }
        }
    }
}

