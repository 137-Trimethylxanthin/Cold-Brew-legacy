package studio.maxis;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class DaemonLogic {

    static List<MusicFile> queue = new ArrayList<>();

    private static boolean myTest = false;
    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("DaemonLogic: Daemon has awakend.");

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        long pid = Long.parseLong(jvmName.split("@")[0]);

        System.out.println("Current PID: " + pid);


        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(6969), 0);
            server.createContext("/test", new testHandler());
            server.createContext("/stop", new stopHandler());
            server.createContext("/add", new addHandler());
            server.createContext("/list", new listHandler());

            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        while (true){
            try {
                if (!queue.isEmpty()) {
                    MusicFile item = queue.get(0);
                    System.out.println("DaemonLogic: playing " + item.Name);
                    if (item.Filetype.equalsIgnoreCase("mp3")){
                        MusicPlayer.mp3Player(item.Path);
                    } else {
                        MusicPlayer.wavPlayer(item.Path);
                    }
                    queue.remove(0);
                }
                System.out.println("DaemonLogic: sleeping for 1 second");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }




    static class testHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "This is the response "+ myTest;
            if (myTest) {
                myTest = false;
            } else {
                myTest = true;
            }
            sendResponse(exchange, response);
        }
    }

    static class stopHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "exited the daemon";
            sendResponse(exchange, response);
            System.out.println("DaemonLogic: Daemon has exited. bey :)");
            System.exit(0);
        }
    }

    static class addHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");
            String response;
            if (parts.length >= 2) {
                String path = parts[1];
                System.out.println("Extracted path: " + path);
                MusicFile musicFile = MusicPlayer.getMusicDetails(path);
                response = "added "+ musicFile.Name +" to queue\n" +requestContent+" | "+ path;
                queue.add(musicFile);
            } else {
                response = "Error cant get path/ wrong path";
            }
            //queue.add(new QueueItem(){{path = "/home/maxi/Music/internet/Säkkijärven Polkka.wav"; name = "pls dont be loud";}});

            sendResponse(exchange, response);
        }
    }

    static class listHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder("List of queue:\n");
            int i = 0;
            for (MusicFile item : queue) {
                response.append(i).append(": ").append(item.Name).append(" | ").append(item.Path).append("\n");
                i++;
            }
            sendResponse(exchange, response.toString());
        }
    }

    private static String readRequestContent(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        StringBuilder requestContent = new StringBuilder();
        String inputLine;
        while ((inputLine = br.readLine()) != null) {
            requestContent.append(inputLine);
        }
        br.close();
        return requestContent.toString();
    }
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        String contentType = "text/html"; // Set the content type to HTML
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes();
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }


}
