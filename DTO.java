import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DTO extends apiCalls {
    // these are the variables for the api response data
    private String id;
    private String name;
    private String type;
    private String status;
    private boolean active;
    private String details;
    private List<String> launches;
    private String wikipediaLink;
    private String dateUtc;
    private int flightNumber;
    private boolean success;
    private boolean upcoming;
    private String agency;
    private String serial;
    private String version;
    private double latitude;
    private double longitude;
    private double heightKm;
    private double velocityKms;
    private String region;
    private String lastUpdate;

    // Default constructor
    public DTO() {
        this.launches = new ArrayList<>();
    }

    // Constructor from JSONObject
    public DTO(JSONObject json, Category category) {
        // call the default constructor
        this();
        // this switch is used to parse the json object based on the category
        switch (category) {
            case LAUNCHES -> parseLaunch(json);
            case ROCKETS -> parseRocket(json);
            case LAUNCHPADS -> parseLaunchpad(json);
            case CREW -> parseCrew(json);
            case CAPSULES -> parseCapsule(json);
            case STARLINK -> parseStarlink(json);
        }
    }

    // this is a data format template for launch category
    private void parseLaunch(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.name = json.optString("name", "N/A");
        this.flightNumber = json.optInt("flight_number", -1);
        this.dateUtc = json.optString("date_utc", "N/A");
        this.success = json.optBoolean("success", false);
        this.details = json.optString("details", "N/A");
        this.upcoming = json.optBoolean("upcoming", false);
        // special handling for links
        if (json.has("links")) {
            JSONObject links = json.getJSONObject("links");
            this.wikipediaLink = links.optString("wikipedia", "N/A");
        }
    }

    // this is a data format template for rocket category
    private void parseRocket(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.name = json.optString("name", "N/A");
        this.type = json.optString("type", "N/A");
        this.active = json.optBoolean("active", false);
        this.details = json.optString("description", "N/A");
        this.wikipediaLink = json.optString("wikipedia", "N/A");
    }

    // this is a data format template for launchpad category
    private void parseLaunchpad(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.name = json.optString("name", "N/A");
        this.status = json.optString("status", "N/A");
        this.region = json.optString("region", "N/A");
        this.details = json.optString("details", "N/A");
        this.latitude = json.optDouble("latitude", 0.0);
        this.longitude = json.optDouble("longitude", 0.0);
    }

    // this is a data format template for crew category
    private void parseCrew(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.name = json.optString("name", "N/A");
        this.agency = json.optString("agency", "N/A");
        this.status = json.optString("status", "N/A");
        this.wikipediaLink = json.optString("wikipedia", "N/A");
        // special handling for launches
        if (json.has("launches")) {
            JSONArray launchesArray = json.getJSONArray("launches");
            for (int i = 0; i < launchesArray.length(); i++) {
                this.launches.add(launchesArray.getString(i));
            }
        }
    }

    // this is a data format  template for capsule category
    private void parseCapsule(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.serial = json.optString("serial", "N/A");
        this.type = json.optString("type", "N/A");
        this.status = json.optString("status", "N/A");
        this.lastUpdate = json.optString("last_update", "N/A");

        // special handling for launches
        if (json.has("launches")) {
            JSONArray launchesArray = json.getJSONArray("launches");
            for (int i = 0; i < launchesArray.length(); i++) {
                this.launches.add(launchesArray.getString(i));
            }
        }
    }

    // this is a data format template for starlink category
    private void parseStarlink(JSONObject json) {
        this.id = json.optString("id", "N/A");
        this.version = json.optString("version", "N/A");
        this.dateUtc = json.optString("launch_date", "N/A");
        this.heightKm = json.optDouble("height_km", 0.0);
        this.velocityKms = json.optDouble("velocity_kms", 0.0);
        this.latitude = json.optDouble("latitude", 0.0);
        this.longitude = json.optDouble("longitude", 0.0);
    }


    //this method is used to display all info about the object in formatted way
    public String displayAllInformation(Category category) {
        StringBuilder display = new StringBuilder();

        switch (category) {
            case LAUNCHES -> display.append(displayLaunchInfo());
            case ROCKETS -> display.append(displayRocketInfo());
            case LAUNCHPADS -> display.append(displayLaunchpadInfo());
            case CREW -> display.append(displayCrewInfo());
            case CAPSULES -> display.append(displayCapsuleInfo());
            case STARLINK -> display.append(displayStarlinkInfo());
        }

        return display.toString();
    }

    // this is display format for launch category
    private String displayLaunchInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Launch Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Name: %s\n", name));
        info.append(String.format("Flight Number: %d\n", flightNumber));
        info.append(String.format("Date: %s\n", dateUtc));
        info.append(String.format("Success: %b\n", success));
        info.append(String.format("Upcoming: %b\n", upcoming));
        info.append(String.format("Details: %s\n", details));
        info.append(String.format("Wikipedia Link: %s\n", wikipediaLink));
        return info.toString();
    }

    // this is display format for rocket category
    private String displayRocketInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Rocket Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Name: %s\n", name));
        info.append(String.format("Type: %s\n", type));
        info.append(String.format("Active: %b\n", active));
        info.append(String.format("Description: %s\n", details));
        info.append(String.format("Wikipedia Link: %s\n", wikipediaLink));
        return info.toString();
    }

    // this is display format for launchpad category
    private String displayLaunchpadInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Launchpad Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Name: %s\n", name));
        info.append(String.format("Status: %s\n", status));
        info.append(String.format("Region: %s\n", region));
        info.append(String.format("Details: %s\n", details));
        info.append(String.format("Latitude: %.6f\n", latitude));
        info.append(String.format("Longitude: %.6f\n", longitude));
        return info.toString();
    }

    // this is display format for crew category
    private String displayCrewInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Crew Member Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Name: %s\n", name));
        info.append(String.format("Agency: %s\n", agency));
        info.append(String.format("Status: %s\n", status));
        info.append(String.format("Wikipedia Link: %s\n", wikipediaLink));
        info.append("Launches: \n");
        for (String launch : launches) {
            info.append(String.format("  - %s\n", launch));
        }
        return info.toString();
    }

    // this is display format for capsule category
    private String displayCapsuleInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Capsule Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Serial: %s\n", serial));
        info.append(String.format("Type: %s\n", type));
        info.append(String.format("Status: %s\n", status));
        info.append(String.format("Last Update: %s\n", lastUpdate));
        info.append("Launches: \n");
        for (String launch : launches) {
            info.append(String.format("  - %s\n", launch));
        }
        return info.toString();
    }

    // this is display format for starlink category
    private String displayStarlinkInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Starlink Satellite Information:\n");
        info.append("------------------------\n");
        info.append(String.format("ID: %s\n", id));
        info.append(String.format("Version: %s\n", version));
        info.append(String.format("Launch Date: %s\n", dateUtc));
        info.append(String.format("Height (km): %.2f\n", heightKm));
        info.append(String.format("Velocity (km/s): %.2f\n", velocityKms));
        info.append(String.format("Latitude: %.6f\n", latitude));
        info.append(String.format("Longitude: %.6f\n", longitude));
        return info.toString();
    }

    // Static method to convert JSONArray to List<DTO>
    public static List<DTO> fromJSONArray(JSONArray jsonArray, Category category) {
        List<DTO> dtoList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            dtoList.add(new DTO(json, category));
        }
        return dtoList;
    }

    // Method to format date based on user's preference
    private String formatDate(String dateStr, String format) {
        if (dateStr == null) return "N/A";
        // Implement date formatting based on UTC or Local setting
        // You'll need to add actual date formatting logic here
        return format.equals("UTC") ? dateStr : convertToLocalTime(dateStr);
    }

    // Helper method to convert UTC date to local time
    private String convertToLocalTime(String utcDate) {
        // Implement conversion from UTC to local time
        // You'll need to add actual conversion logic here
        return utcDate; // Placeholder
    }
}
