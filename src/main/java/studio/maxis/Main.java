package studio.maxis;

import org.apache.commons.cli.*;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        Options options = new Options();
        options.addOption("h", "help",false , "Print this help message");
        options.addOption("v", "version", false, "Print version information and quit");
        options.addOption("C", "config", true, "Specify config file");

        options.addOption("G", "gui", false, "Launch GUI mode");
        options.addOption("T", "tui", false, "Launch TUI mode");
        options.addOption("s", "status", false, "Display status");
        options.addOption("d", "daemon", false, "Run as daemon");

        options.addOption("p", "play", false, "Play the music");
        options.addOption("Pa", "pause", false, "Pause the music");
        options.addOption("s", "stop", false, "Stop the music");
        options.addOption("n", "next", false, "Play the next track");
        options.addOption("P", "previous", false, "Play the previous track");
        options.addOption("t", "toggle", false, "Toggle play/pause");
        options.addOption("R", "random", false, "Enable random playback");
        options.addOption("L", "loop", false, "Enable loop mode");
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
            if (cmd.hasOption("h")) {
                String[] remainingArgs = cmd.getArgs();
                if (remainingArgs.length >= 1) {
                    // User provided a command name with -h option
                    String commandName = remainingArgs[0];
                    Help.displayHelpForCommand(commandName);
                } else {
                    // User provided the -h option without a command name
                    Help.generalHelp(options);
                }
            } else if (cmd.hasOption("v")) {
                Help.version();
            } else if (cmd.hasOption("d")) {
                String[] daemonAction = cmd.getArgs();
                if (daemonAction.length == 0) {
                    Help.displayHelpForCommand("d");
                } else if (daemonAction[0].equals("start")) {
                    DaemonLifecycle.init();
                } else if (daemonAction[0].equals("stop")) {
                    DaemonLifecycle.stop();

                } else if (daemonAction[0].equals("restart")) {
                    DaemonLifecycle.stop();
                    System.out.println("----------------");
                    DaemonLifecycle.init();
                } else {
                    System.err.println("Invalid daemon action: " + daemonAction[0]);
                    Help.displayHelpForCommand("daemon");
                }
            } else if (cmd.hasOption("n")) {
                Controlls.sendGetRequest("next");

            } else if (cmd.hasOption("a")) {
                String path = cmd.getOptionValue("a");
                System.out.println(path);
                String data = "path="+path;
                Controlls.sendPostRequest("add", data);

            } else if (cmd.hasOption("l")) {
                Controlls.sendGetRequest("list");

            } else if (cmd.hasOption("r")) { //not implemented TODO
                String path = cmd.getOptionValue("r");
                System.out.println(path);

                MusicPlayer.otherPlayer(path);


            } else if (cmd.hasOption("C")) {
                Config.loadConf();

            } else if (cmd.hasOption("p")) {
                Controlls.sendGetRequest("play");

            } else if (cmd.hasOption("Pa")) {
                Controlls.sendGetRequest("pause");
            } else if (cmd.hasOption("s")) {
                Controlls.sendGetRequest("stopSong");
            } else if (cmd.hasOption("P")) {
                Controlls.sendGetRequest("previous");
            } else {
                Help.generalHelp(options);
            }
        } catch (ParseException e) {
            System.err.println("Error parsing command line options: " + e.getMessage());
            // Print usage or error message here
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}