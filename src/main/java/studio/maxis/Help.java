package studio.maxis;

import org.apache.commons.cli.*;


public class Help {


    public static void displayHelpForCommand(String commandName) {
        if ("d".equals(commandName)) {
            System.out.println("Help for daemon: Usage instructions");
            System.out.println("java -jar coldbrew.jar daemon <action>");
            System.out.println("Actions:");
            System.out.println("start - Start the daemon");
            System.out.println("stop - Stop the daemon");
            System.out.println("restart - restart the daemon");

        } else if ("command2".equals(commandName)) {
            System.out.println("Help for command2: Usage instructions...");

        } else {
            System.out.println("No help available for the specified command: " + commandName);
            System.out.println("Use the -h option to display general help.");
            System.out.println("(Or it hasent been implemented yet)");
        }
    }

    public static void generalHelp(Options options){
        System.out.println("General help message: Usage instructions...");
        HelpFormatter formatter = new HelpFormatter();
        String header = "\nColdBrew Music player :)\n\n";
        String footer = "\nFor more information, visit https://maxis.studio";

        formatter.printHelp("java -jar coldbrew.jar [options]", header, options, footer);
    }

    public static void version() {
        String version = "0.1.0-SNAPSHOT";
        String Jversion = System.getProperty("java.version");
        System.out.println("Coldbrew version " + version + " | Java: " + Jversion);
    }
    
    public static void gui() {
        System.out.println("With this option, Coldbrew will run in GUI mode.");
        System.out.println("This option is not yet implemented.");
    }

    public static void tui() {
        System.out.println("With this option, Coldbrew will run in TUI mode.");
        System.out.println("This option is not yet implemented.");
    }

    public static void status() {
        System.out.println("here it prints the status of the current running instance");
        System.out.println("If the Player is running in daemon mode, this is the only way to interact with it.");
        System.out.println("If no options are given, it will print all the status.");
        System.out.println("you can use multiple options at once.");
        System.out.println("-s (--status) <options>");
        System.out.println("Options:");
        System.out.println("--is-playing");
        System.out.println("--is-paused");
        System.out.println("--is-stopped");
        System.out.println("--is-looping");
        System.out.println("--is-shuffling");
        System.out.println("--is-random");
        System.out.println("--is-daemon");
        System.out.println("--is-gui");
        System.out.println("--is-tui");
        System.out.println("--cpu-usage");
        System.out.println("--memory-usage");
        System.out.println("--uptime");

        System.out.println("Example:");
        System.out.println("java -jar coldbrew.jar -s --is-playing --is-paused --is-stopped");
        System.out.println("This will print the status of the player, if it is playing, paused or stopped.");

        System.out.println("If you want to know the current song info use the -ci (--current-info) option.");
    }

    public static void daemon() {
        System.out.println("With this option, Coldbrew will run in daemon mode.");
        System.out.println("This option is not yet implemented.");
    }

}
