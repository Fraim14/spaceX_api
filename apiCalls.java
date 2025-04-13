
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;


public class apiCalls {
    private final String baseUrl = "https://api.spacexdata.com/v4";
    public URL url;

    //this method is used to make a GET request to the SpaceX API
    public JSONObject get(String endpoint) throws Exception {
        url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

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
        url = new URL(baseUrl + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");


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
}
