
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;


public class apiCalls {
    public final String baseUrl = "https://api.spacexdata.com/v4";


    //this method is used to make a GET request to the SpaceX API
    public JSONObject get(String endpoint) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        try {

            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
            return new JSONObject(response.toString());

        } finally {
            connection.disconnect();
        }


    }

    //this method is used to make a POST request to the SpaceX API to filter the input data by the user
    public JSONObject post(String endpoint, JSONObject filter) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);


        try {
            try {
                OutputStream outputStream = connection.getOutputStream();
                byte[] input = filter.toString().getBytes();
                outputStream.write(input, 0, input.length);
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            return new JSONObject(response.toString());

        } finally {
            connection.disconnect();
        }


    }

    public enum Category {
        LAUNCHES("/launches"),
        ROCKETS("/rockets"),
        LAUNCHPADS("/launchpads"),
        CREW("/crew"),
        CAPSULES("/capsules");

        private final String endpoint;

        Category(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getEndpoint() {
            return endpoint;
        }
    }


    public JSONObject getCategory(Category category) throws Exception {
        return get(category.getEndpoint());
    }

    public JSONObject getItemById(Category category, String id) throws Exception {
        return get(category.getEndpoint() + "/" + id);
    }

    public JSONObject queryCategory(Category category, JSONObject filters) throws Exception {
        return post(category.getEndpoint() + "/query", filters);
    }
}
