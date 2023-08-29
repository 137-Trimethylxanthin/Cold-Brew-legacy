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


public class DaemonLogic {

    static List<MusicFile> queue = new ArrayList<>();


    private static boolean isPaused = false;
    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("DaemonLogic: Daemon has awakend.");

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        String jvmName = runtimeBean.getName();
        long pid = Long.parseLong(jvmName.split("@")[0]);

        System.out.println("Current PID: " + pid);


        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(6969), 0);
            server.createContext("/stop", new stopHandler());
            server.createContext("/add", new addHandler());
            server.createContext("/list", new listHandler());
            server.createContext("/play", new playHandler());
            server.createContext("/pause", new pauseHandler());
            server.createContext("/next", new nextHandler());
            server.createContext("/previous", new previousHandler());
            server.createContext("/stopSong", new stopSongHandler());


            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        while (true){
            try {
                if (!queue.isEmpty() && !isPaused && !MusicPlayer.currentlyPlaying()){
                    MusicFile item = queue.get(0);
                    System.out.println("DaemonLogic: playing " + item.Name);
                    if (item.Filetype.equalsIgnoreCase("wav")){
                        MusicPlayer.wavPlayer(item.Path);
                    } else {
                        MusicPlayer.otherPlayer(item.Path);

                    }

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




    static class playHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            try {
                if (isPaused){
                    MusicPlayer.resume();
                    response = "resume success";
                    isPaused = false;
                } else {
                    response = "already playing";
                }
            }catch (Exception e){
                e.printStackTrace();
                response = "error resume";
            }
            sendResponse(exchange, response);
        }
    }

    static class pauseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            try {
                if (!isPaused){
                    MusicPlayer.pause();
                    response = "pause success";
                    isPaused = true;
                } else {
                    response = "already paused";
                }
            }catch (Exception e){
                e.printStackTrace();
                response = "error pause";
            }
            sendResponse(exchange, response);
        }
    }
    static class stopSongHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            try {
                MusicPlayer.stop();
                response = "STOP success";
                isPaused = true;
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }
            sendResponse(exchange, response);
        }
    }

    static class nextHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            try {
                MusicPlayer.stop();
                if (queue.size() > 0){
                    queue.remove(0);
                }
                response = "next success";
                isPaused = false;
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }
            sendResponse(exchange, response);
        }
    }

    static class previousHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            response = "previous not implemented yet";
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
