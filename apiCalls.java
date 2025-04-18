//Maksym Shtymak 3151565

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;


public class apiCalls {
    //this is the base URL for the SpaceX API
    public final String baseUrl = "https://api.spacexdata.com/v4";


    //this method is used to make a GET request to the SpaceX API
    public JSONObject get(String endpoint) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        try {
            // get the response of the api call
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            // use the string builder to store the response
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
        // this property is used to make the post particulary in the body ob api
        connection.setDoOutput(true);


        try {
            try {
                OutputStream outputStream = connection.getOutputStream();
                // divide the filter into bytes and then write it to the output stream
                byte[] input = filter.toString().getBytes();
                outputStream.write(input, 0, input.length);
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            // get the filtered response of the api
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

    // this enum is used to store tge basic endpoints of the api which are used in another methods and classes
    public enum Category {
        LAUNCHES("/launches"),
        ROCKETS("/rockets"),
        LAUNCHPADS("/launchpads"),
        CREW("/crew"),
        CAPSULES("/capsules"),
        STARLINK("/starlink");

        private final String endpoint;

        // a constructor for the enum
        Category(String endpoint) {
            this.endpoint = endpoint;
        }

        // this method gets the endpoint of the enum
        public String getEndpoint() {
            return endpoint;
        }
    }

    // thsi method finds the object based in his id
    public JSONObject getItemById(Category category, String id) throws Exception {
        return get(category.getEndpoint() + "/" + id);
    }

    // this method is a main filtering method which is used to filter the data by the user with the use of query file of api
    public JSONObject queryCategory(Category category, JSONObject filters) throws Exception {
        return post(category.getEndpoint() + "/query", filters);
    }
}
