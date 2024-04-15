package studio.maxis;

import com.sun.tools.jconsole.JConsoleContext;
import org.apache.commons.cli.*;
import studio.maxis.daemon.Controlls;
import studio.maxis.daemon.DaemonLifecycle;
import studio.maxis.daemon.MusicPlayer;
import studio.maxis.jellyfin.Api;

import java.net.URI;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, world!");

        System.out.println("Setting env vars...");
        try{
            Config.loadConf();
            Config.setKey("jellyfinServerName", "Maxi");
            Config.setKey("jellyfinServerPassword", "gNtFiFglCNiNejFFRgfGDvJIuTCvENbRdunGnE");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Options options = new Options();
        options.addOption("h", "help",false , "Print this help message"); // done
        options.addOption("v", "version", false, "Print version information and quit"); // done
        options.addOption("C", "config", true, "Specify config file");

        options.addOption("G", "gui", false, "Launch GUI mode");
        options.addOption("T", "tui", false, "Launch TUI mode");
        options.addOption("st", "status", false, "Display status");
        options.addOption("d", "daemon", false, "Run as daemon"); // done

        options.addOption("p", "play", false, "Play the music"); // done
        options.addOption("Pa", "pause", false, "Pause the music"); // done
        options.addOption("s", "stop", false, "Stop the music"); // done
        options.addOption("n", "next", false, "Play the next track"); // done
        options.addOption("P", "previous", false, "Play the previous track"); // done
        options.addOption("t", "toggle", false, "Toggle play/pause");
        options.addOption("R", "random", false, "Enable random playback");
        options.addOption("L", "loop", false, "Enable loop mode");
        options.addOption("S", "shuffle", false, "Enable shuffle mode");
        options.addOption("ts", "true-shuffle", false, "Enable true shuffle mode");
        options.addOption("a", "add", true, "Add a track from the specified path"); // done
        options.addOption("r", "remove", true, "Remove the specified filename from the queue"); // done
        options.addOption("c", "clear", false, "Clear the queue");
        options.addOption("l", "list", false, "List tracks in the queue"); // done
        options.addOption("i", "info", true, "Display information about the specified filename"); // half-baked
        options.addOption("I", "current-info", false, "Display information about a track in the queue"); // half-baked
        options.addOption("aPl", "add-Playlist", true, "Adds a playlist to the queue (path for the playlist)"); // done
        options.addOption("sT", "skip-to", true, "Skips to a specific track in the queue via index (0-n)"); // done
        options.addOption("V", "volume", true, "Set the volume (0%-100%) or (-nDb...-1dB, 0DB,1db, 2db, ...nDb)"); // done
        //jellyfin options
        options.addOption("j", "jelly", false, "Test jelly");
        options.addOption("jls", "jellyListSong", true, "Lists JellyFin Songs");
        options.addOption("jss", "jellySearchSong", true, "Searches for a JellyFin Song");
        options.addOption("jas", "jellyAddSong", true, "Adds a JellyFin Song to the queue");
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
                String number = cmd.getOptionValue("r");
                String data = "number="+number;
                Controlls.sendPostRequest("remove", data);

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
            } else if (cmd.hasOption("aPl")) {
                String path = cmd.getOptionValue("aPl");
                System.out.println(path);
                String data = "path="+path;
                Controlls.sendPostRequest("addPlaylist", data);

            } else if (cmd.hasOption("sT")) {
                String index = cmd.getOptionValue("sT");
                System.out.println(index);
                String data = "index="+index;
                Controlls.sendPostRequest("skipTo", data);
            } else if (cmd.hasOption("V")) {
                String vol = cmd.getOptionValue("V");
                System.out.println(vol);
                String data = "vol="+vol;
                Controlls.sendPostRequest("volume", data);
            } else if (cmd.hasOption("I")) {
                String[] remainingArgs = cmd.getArgs();
                if (remainingArgs.length >= 1) {
                    // User provided a command name with -h option
                    String index = remainingArgs[0];
                    String data = "index="+index;
                    Controlls.sendPostRequest("infoOfQueue", data);
                } else {
                    Controlls.sendPostRequest("infoOfQueue", "nothing");

                }
            } else if (cmd.hasOption("i")) {
                String path = cmd.getOptionValue("i");
                System.out.println(path);
                String data = "path="+path;
                Controlls.sendPostRequest("info", data);
            } else if (cmd.hasOption("j")){
                System.out.println("Testing connection to Jellyfin server...");
                Api api = new Api("https://jelly.plskill.me", "", "maxi", "gNtFiFglCNiNejFFRgfGDvJIuTCvENbRdunGnE");

                URI stream = api.getSongStream("7574a8e6417ec19c5afbf28c8295eb43");

                String data = "path="+stream;
                //Controlls.sendPostRequest("add", data);
                System.out.println(stream);
                MusicPlayer.streamPlayer(stream);

                System.out.printf("Finished testing connection");
            }
            else {
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