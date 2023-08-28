package studio.maxis;



import javax.sound.sampled.*;
import java.io.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import javazoom.jl.decoder.JavaLayerException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



class QueueItem {
    String path;
    String name;
}
public class DaemonLogic {

    static List<QueueItem> queue = new ArrayList<>();

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
            server.createContext("/add", new newHandler());
            server.createContext("/list", new listHandler());

            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        while (true){
            try {
                if (!queue.isEmpty()) {
                    QueueItem item = queue.get(0);
                    System.out.println("DaemonLogic: playing " + item.name);

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

    static class newHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            queue.add(new QueueItem(){{path = "/home/maxi/Music/astronaut in the forest.mp3"; name = "pls dont be loud";}});
            String response = "added ... to queue";
            sendResponse(exchange, response);
        }
    }

    static class listHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder("List of queue:\n");
            int i = 0;
            for (QueueItem item : queue) {
                response.append(i).append(": ").append(item.name).append(" | ").append(item.path).append("\n");
                i++;
            }
            sendResponse(exchange, response.toString());
        }
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
