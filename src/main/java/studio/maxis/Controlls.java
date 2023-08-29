package studio.maxis;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Controlls {
    public static void sendGetRequest(String urlEnd) throws Exception{
        String req = "http://localhost:6969/" + urlEnd;
        sendHttpRequest(req);

    }

    public static void sendPostRequest(String urlEnd ,String data) throws Exception {
        String req = "http://localhost:6969/" + urlEnd;
        sendPostHttpRequest(req, data); // String postData = "key1=value1&key2=value2";
    }

    private static void sendHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n"); // Append newline character
        }
        in.close();

        System.out.print("Response: " + response); // Use System.out.print() to preserve newlines
    }

    private static void sendPostHttpRequest(String urlString, String data) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] postDataBytes = data.getBytes(StandardCharsets.UTF_8);
            os.write(postDataBytes);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append("\n"); // Append newline character
        }
        in.close();

        System.out.print("Response: " + response);

    }

}
