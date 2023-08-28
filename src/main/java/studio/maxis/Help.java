package studio.maxis;
public class Help {


    public static void me() {
        System.out.println("Usage: java -jar coldbrew.jar [options]");
        System.out.println("Options:");
        System.out.println("  -h, --help <command> (for more specific help)");
        System.out.println("  -v, --version");
        System.out.println("  -G, --gui");
        System.out.println("  -T, --tui");
        System.out.println("  -s, --status");
        System.out.println("  -d, --daemon");

        System.out.println("Control options:");
        System.out.println("  -p, --play");
        System.out.println("  -s, --stop");
        System.out.println("  -n, --next");
        System.out.println("  -p, --previous");
        System.out.println("  -t, --toggle");
        System.out.println("  -r, --random");
        System.out.println("  -l, --loop");
        System.out.println("  -s, --shuffle");
        System.out.println("  -S, --true-shuffle");
        System.out.println("  -a, --add <path>");
        System.out.println("  -r, --remove <filename>");
        System.out.println("  -c, --clear");
        System.out.println("  -l, --list");
        System.out.println("  -i, --info (<filename>)");
        System.out.println("  -I, --current-info");
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
