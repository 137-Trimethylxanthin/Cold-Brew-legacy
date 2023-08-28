package studio.maxis;

import org.apache.commons.cli.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        Options options = new Options();
        options.addOption("h", "help", true, "Print this help message");
        options.addOption("v", "version", false, "Print version information and quit");
        options.addOption("c", "config", true, "Specify config file");

        options.addOption("G", "gui", false, "Launch GUI mode");
        options.addOption("T", "tui", false, "Launch TUI mode");
        options.addOption("s", "status", false, "Display status");
        options.addOption("d", "daemon", false, "Run as daemon");

        options.addOption("p", "play", false, "Play the music");
        options.addOption("s", "stop", false, "Stop the music");
        options.addOption("n", "next", false, "Play the next track");
        options.addOption("p", "previous", false, "Play the previous track");
        options.addOption("t", "toggle", false, "Toggle play/pause");
        options.addOption("r", "random", false, "Enable random playback");
        options.addOption("l", "loop", false, "Enable loop mode");
        options.addOption("s", "shuffle", false, "Enable shuffle mode");
        options.addOption("S", "true-shuffle", false, "Enable true shuffle mode");
        options.addOption("a", "add", true, "Add a track from the specified path");
        options.addOption("r", "remove", true, "Remove the specified filename from the playlist");
        options.addOption("c", "clear", false, "Clear the playlist");
        options.addOption("l", "list", false, "List tracks in the playlist");
        options.addOption("i", "info", true, "Display information about the specified filename");
        options.addOption("I", "current-info", false, "Display information about the current track");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

        } catch (ParseException e) {
            System.err.println("Error parsing command line options: " + e.getMessage());
            // Print usage or error message here
        }

    }
}