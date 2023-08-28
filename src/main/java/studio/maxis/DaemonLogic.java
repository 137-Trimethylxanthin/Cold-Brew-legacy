package studio.maxis;


import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class DaemonLogic {
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
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        while (true){
            if (myTest) {
                System.out.println("the api is working");
            } else {
                System.out.println("the api is not working wait 5 seconds");
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
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

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


}
