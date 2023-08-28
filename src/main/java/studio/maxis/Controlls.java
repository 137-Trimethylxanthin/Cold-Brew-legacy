package studio.maxis;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
public class Controlls {
    public static void sendRequest(String var) throws Exception{
        String req = "http://localhost:6969/" + var;
        sendHttpRequest(req);

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

}
