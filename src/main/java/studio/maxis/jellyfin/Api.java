package studio.maxis.jellyfin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//USE APPLE MUSIC AAC for audio streams
public class Api {
    private String server;
    private String port;
    private String username;
    private String password;
    private String token;


    public Api(String server, String port, String username, String password) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;


    }

    public void getApiToken() {
        //send request to server POST /Users/AuthenticateByName
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(server + ":" + port + "/Users/AuthenticateByName"))
                .header("Content-type", "application/json")
                .header("X-Application", "Cold Brew")
                .header("x-emby-authorization", getAuthString())
                .POST(HttpRequest.BodyPublishers.ofString("{\"Username\":\"" + this.username + "\", \"Pw\":\"" + this.password + "\"}"))
                .build();
        sendRequest(request);
    }

    private String getAuthString() {
        String authStr = "MediaBrowser Client=" + "Coffee" + ", Device=" + "PiCi" + ", DeviceId=-, Version=" + "0.1.1";
        if ( this.token != null && !this.token.isEmpty()) {
            authStr += ", Token=" + this.token;
        }
        return authStr;
    }


    public void testConnection() {
        System.out.println("Testing connection to Jellyfin server...");
        System.out.println("Server: " + server);
        System.out.println("Port: " + port);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        //send request to server GET /Auth/Provders
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(server + ":" + port + "/Auth/Providers"))
                .header("Authorization", "MediaBrowser Token=\"your_api_key\"")
                .GET()
                .build();
        sendRequest(request);
    }


    private void sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //nice formating:
            System.out.println("Response: " + response.body().replace(",", ",\n"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }



}
