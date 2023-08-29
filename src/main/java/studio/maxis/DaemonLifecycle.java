package studio.maxis;

import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
public class DaemonLifecycle {
    public static void init() {
        System.out.println("DaemonLifecycle-init: Daemon initializing.\n");
        starting();
    }

    public static void starting() {
        if (isRunning()) {
            System.out.println("DaemonLifecycle-starting: Daemon already running.");
        } else {
            System.out.println("DaemonLifecycle-starting: Daemon starting.");
            String jarPath = System.getProperty("java.class.path");
            try{
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", jarPath, "studio.maxis.DaemonLogic");
                System.out.println("DaemonLifecycle-starting: the daemon has awakend..");

                File logFile = new File("daemon.log");
                pb.redirectErrorStream(true);
                pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));

                Process p = pb.start();
                System.out.println("DaemonLifecycle-starting: the daemon has started.");

                String pid = String.valueOf(p.pid());
                System.out.println("DaemonLifecycle-starting: the daemon has PID: " + pid);
                String tempFilePath = getFilePath("cold-brew-daemon.pid");
                try {
                    // Erstellen Sie die temporäre Datei und schreiben Sie die PID hinein
                    FileWriter writer = new FileWriter(tempFilePath);
                    writer.write(pid);
                    writer.close();

                    System.out.println("DaemonLifecycle-starting: Temporäre PID-Datei erstellt: " + tempFilePath + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void stop() {
        System.out.println("DaemonLifecycle-stop: Daemon stopping.\n");
        if (!isRunning()) {
            System.out.println("DaemonLifecycle-stop: ...but Daemon was never alive to begin with :( .");
            return;
        }
        try {
            Controlls.sendGetRequest("stop");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        destroy();
    }

    public static void destroy() {
        System.out.println("DaemonLifecycle-destroy: Daemon destroying.");
        String tempFilePath = getFilePath("cold-brew-daemon.pid");

        File tempFile = new File(tempFilePath);
        if (tempFile.exists() && tempFile.isFile()) {
            // Delete the temporary file
            if (tempFile.delete()) {
                System.out.println("DaemonLifecycle-destroy: emporäre Datei gelöscht: " + tempFilePath);
            } else {
                System.out.println("DaemonLifecycle-destroy: Temporäre Datei konnte nicht gelöscht werden: " + tempFilePath);
            }
        } else {
            System.out.println("DaemonLifecycle-destroy: Temporäre Datei existiert nicht oder ist keine Datei: " + tempFilePath);
        }
        System.out.println("DaemonLifecycle-destroy: Daemon destroyed.\n");
    }


    public static String getFilePath(String fileName){
        String tempFilePath;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            System.out.println("DaemonLifecycle-filepath: Windows detected.");
            tempFilePath = System.getProperty("java.io.tmpdir") + "\\" + fileName;
        } else {
            System.out.println("DaemonLifecycle-filepath: Linux detected.");
            tempFilePath = System.getProperty("java.io.tmpdir") + "/" + fileName;
        }

        return tempFilePath;
    }

    private static boolean isRunning() {
        System.out.println("DaemonLifecycle: Checking if daemon is still alive.");
        String tempFilePath = getFilePath("cold-brew-daemon.pid");
        File tempFile = new File(tempFilePath);
        if (tempFile.exists() && tempFile.isFile()) {
            // Read the file for the pid
            try (BufferedReader reader = new BufferedReader(new FileReader(tempFilePath))) {
                String pidString = reader.readLine();
                System.out.println("DaemonLifecycle-running?: PID read from file: " + pidString);
                System.out.println("DaemonLifecycle-running?: poking the daemon wit a stick ;)");

                int pid = Integer.parseInt(pidString);

                // Check if the PID is still running
                if (isProcessRunning(pid)) {
                    System.out.println("-- DaemonLifecycle-running?: Daemon is still alive.");
                    return true;
                } else {
                    System.out.println("-- DaemonLifecycle-running?: Daemon is ded.");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }
        return false;
    }
    private static boolean isProcessRunning(int pid) {
        if (pid <= 0) {
            return false;
        }

        try {
            // The exact command to check if a process is running can vary based on the operating system.
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                // For Windows
                System.out.println("DaemonLifecycle-runningProc?: Windows detected.");
                Process process = Runtime.getRuntime().exec("tasklist");
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(" " + pid + " ")) {
                            return true;
                        }
                    }
                }
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
                // For Linux, Unix, or MacOS
                System.out.println("DaemonLifecycle-runningProc?: Linux detected.");
                Process process = Runtime.getRuntime().exec("ps -p " + pid);
                process.waitFor();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    int lineNumber = 0;
                    while ((line = reader.readLine()) != null) {
                        if (lineNumber++ >= 1) {  // Skip the first line which is the header
                            String[] columns = line.trim().split("\\s+");
                            System.out.println("DaemonLifecycle-runningProc?: " + columns[0]);
                            if (columns.length > 0 && Integer.parseInt(columns[0]) == pid) {
                                return true;
                            }
                        }
                    }
                }
            } else {
                System.out.println("DaemonLifecycle-runningProc?: Unknown OS detected.");
                return false;
            }

        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


}
