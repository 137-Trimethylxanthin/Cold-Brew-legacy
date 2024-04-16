package studio.maxis.daemon;



import java.io.*;
import java.net.InetSocketAddress;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class DaemonLogic {

    static List<MusicFile> queue = new ArrayList<>();
    static int currentPlayingIndex = 0;
    public static float currentVolumen = 0.0f;
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
            server.createContext("/remove", new removeHandler());
            server.createContext("/list", new listHandler());
            server.createContext("/play", new playHandler());
            server.createContext("/pause", new pauseHandler());
            server.createContext("/next", new nextHandler());
            server.createContext("/previous", new previousHandler());
            server.createContext("/stopSong", new stopSongHandler());
            server.createContext("/addPlaylist", new addPlaylistHandler());
            server.createContext("/skipTo", new skipTo());
            server.createContext("/volume", new volumeHandler());
            server.createContext("/infoOfQueue", new infoHandler());
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        while (true){
            try {
                if (!queue.isEmpty() && !isPaused && !MusicPlayer.currentlyPlaying() && currentPlayingIndex < queue.size()){

                    MusicFile item = queue.get(currentPlayingIndex);
                    System.out.println("DaemonLogic: playing " + item.Name);

                    if (item.Filetype.equalsIgnoreCase("wav")) {
                        MusicPlayer.wavPlayer(item.Path);
                    }
                    else if (item.Filetype.equalsIgnoreCase("http")) {
                            //MusicPlayer.streamPlayer(URI.create(item.Path));
                    } else {
                        MusicPlayer.otherPlayer(item.Path);
                    }
                }
                System.out.println("DaemonLogic: sleeping for 1 half second");
                Thread.sleep(500);
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
                if (queue.size() > 0 && currentPlayingIndex < queue.size()){
                    currentPlayingIndex++;
                    response = "next success";
                }else {
                    response = "no next song";
                }

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
            try {
                MusicPlayer.stop();
                if (currentPlayingIndex > 0){
                    currentPlayingIndex--;
                }
                response = "previous success";
                isPaused = false;
            } catch (Exception e) {
                e.printStackTrace();
                response = "error";
            }

            sendResponse(exchange, response);
        }
    }

    static class skipTo implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");

            if (parts.length >= 2) {
                String part = parts[1];
                System.out.println("the number: " + part);
                int skipIndex = -1;

                try {
                    skipIndex = Integer.parseInt(part);

                } catch (NumberFormatException e){
                    response = "Error Thats not a number";
                    sendResponse(exchange, response);
                    return;

                }

                if (!queue.isEmpty() && skipIndex < queue.size() || skipIndex < 0){
                    currentPlayingIndex = skipIndex;
                    MusicPlayer.stop();
                    response = "skipped to " + skipIndex;
                }
                else {
                    response = "Error index out of bounds";
                }
            } else {
                response = "Error cant get number";
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

    static class removeHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");
            String response = "";
            if (parts.length >= 2) {
                String part = parts[1];
                System.out.println("the number: " + part);
                int removeIndex = -1;

                try {
                    removeIndex = Integer.parseInt(part);

                } catch (NumberFormatException e){
                    response = "Error Thats not a number";
                    sendResponse(exchange, response);
                    return;

                }

                if (!queue.isEmpty() && removeIndex < queue.size() || removeIndex < 0){
                    queue.remove(removeIndex);
                    response = "removed " + removeIndex;
                }
                else {
                    response = "Error index out of bounds";
                }
                if (currentPlayingIndex == removeIndex){
                    currentPlayingIndex--;
                    MusicPlayer.stop();
                } else if (currentPlayingIndex > removeIndex) {
                    currentPlayingIndex--;
                }

            } else {
                response = "Error cant get number";
            }
            sendResponse(exchange, response);
        }
    }

    static class addPlaylistHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");
            String response;
            if (parts.length >= 2) {
                String path = parts[1];
                //System.out.println("Extracted path: " + path);
                FileInputStream fstream = new FileInputStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
                String strLine;
                int i = 0;

                //Read File Line By Line
                while ((strLine = br.readLine()) != null)   {
                    // Print the content on the console
                    if (!strLine.startsWith("#")){
                        MusicFile musicFile = MusicPlayer.getMusicDetails(strLine);
                        queue.add(musicFile);
                        i++;
                    }
                }

                //Close the input stream
                fstream.close();
                response = "added "+ i +" songs to queue\n" +requestContent+" | "+ path;
            } else {
                response = "Error cant get path/ wrong path";
            }
            //queue.add(new QueueItem(){{path = "/home/maxi/Music/internet/Säkkijärven Polkka.wav"; name = "pls dont be loud";}});

            sendResponse(exchange, response);
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
                //System.out.println("Extracted path: " + path);
                
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
            response.append("volume: ").append(currentVolumen).append("\n");
            int i = 0;
            for (MusicFile item : queue) {
                if (currentPlayingIndex == i){
                    response.append("-->");
                }
                response.append(i).append(": ").append(item.Name).append(" | ").append(item.Path).append("\n");
                i++;
            }
            sendResponse(exchange, response.toString());
        }
    }

    static class volumeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");
            String response;
            if (parts.length >= 2) {
                String volume = parts[1];
                System.out.println("Extracted volume: " + volume);
                float vol = 0.0f;
                try {
                    if (volume.endsWith("%")){
                        volume = volume.substring(0, volume.length() - 1);
                        int percentage = Math.min(Math.max(Integer.parseInt(volume), 1), 100);
                        // Map the percentage to the decibel range
                        float minDecibels = -65.0f;
                        float maxDecibels = 0.0f;
                        vol = minDecibels + (percentage / 100.0f) * (maxDecibels - minDecibels);
                    } else if (volume.toLowerCase().endsWith("db")){
                        volume = volume.substring(0, volume.length() - 2);
                        vol = Float.parseFloat(volume);
                    } else {
                        throw new NumberFormatException();
                    }

                    System.out.println("set volume to " + vol);
                    currentVolumen = vol;
                    MusicPlayer.setVolume(vol);
                    response = "set volume to " + volume;
                } catch (NumberFormatException e){
                    response = "Error Thats not a number";
                }
            } else {
                response = "Error cant get number";
            }
            sendResponse(exchange, response);
        }
    }

    static class infoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestContent = readRequestContent(exchange);
            String[] parts = requestContent.split("=");
            String response;
            if (parts.length >= 2) {
                String index = parts[1];
                System.out.println("Extracted index: " + index);
                int a = Integer.parseInt(index);
                if (a < queue.size()){
                    MusicFile item = queue.get(a);
                    response = "Name: " + item.Name + "\nPath: " + item.Path + "\nFiletype: " + item.Filetype;
                } else {
                    response = "Error index out of bounds";
                }
            } else if (parts.length == 1) {
                response = "Name: " + queue.get(currentPlayingIndex).Name + "\nPath: " + queue.get(currentPlayingIndex).Path + "\nFiletype: " + queue.get(currentPlayingIndex).Filetype;
            } else {
                response = "Error cant get number";
            }

            sendResponse(exchange, response);
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
