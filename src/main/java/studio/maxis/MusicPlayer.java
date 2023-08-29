package studio.maxis;

//mp3 files
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

//wav files
import javax.sound.sampled.*;

//read files
import java.io.*;
import java.util.Arrays;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;



public class MusicPlayer {
    public Integer volumen = 100; // #TODO: make it so it synced between the diffrent formats :)

    public static void mp3Player(String path) {
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            Player player = new Player(fileInputStream);
            System.out.println("Playing...");
            player.play();
            System.out.println("Done!");
        } catch (FileNotFoundException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public static void wavPlayer(String path) {
        try {
            File file = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();


       } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public static MusicFile getMusicDetails(String path){
        MusicFile musicFile = new MusicFile();
        File file = new File(path);

        musicFile.FileName = file.getName();
        musicFile.Path = file.getAbsolutePath();
        musicFile.Size = file.getTotalSpace();
        String[] part = (musicFile.Path.split("\\."));
        musicFile.Filetype = part[1];

        try (InputStream stream = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();

            ParseContext context = new ParseContext();
            parser.parse(stream, handler, metadata, context);

            // Print extracted metadata
            String[] metadataNames = metadata.names();
            for (String name : metadataNames) {
                System.out.println(name + ": " + metadata.get(name));
            }
            musicFile.Album = metadata.get("xmpDM:album");
            musicFile.ReleaseYear = metadata.get("xmpDM:releaseDate");
            musicFile.Artist = metadata.get("xmpDM:artist");
            musicFile.TrackNumber = metadata.get("xmpDM:trackNumber");
            musicFile.SampleRate = metadata.get("xmpDM:audioSampleRate");
            musicFile.Name = metadata.get("dc:title");
            musicFile.Bpm = metadata.get("bpm");
            musicFile.Genre = metadata.get("xmpDM:genre");
            musicFile.Composer = metadata.get("composer");
            musicFile.Duration = metadata.get("xmpDM:duration");
            musicFile.Bitrate = metadata.get("bits");


            System.out.println("--------");
            System.out.println("Album: " + musicFile.Album);
            System.out.println("Release Year: " + musicFile.ReleaseYear);
            System.out.println("Artist: " + musicFile.Artist);
            System.out.println("Track Number: " + musicFile.TrackNumber);
            System.out.println("Filetype: " + musicFile.Filetype);
            System.out.println("Sample Rate: " + musicFile.SampleRate);
            System.out.println("Name: " + musicFile.Name);
            System.out.println("FileName: " + musicFile.FileName);
            System.out.println("Bpm: " + musicFile.Bpm);
            System.out.println("Genre: " + musicFile.Genre);
            System.out.println("Size: " + musicFile.Size + "b");
            System.out.println("Composer: " + musicFile.Composer);
            System.out.println("Duration: " + musicFile.Duration + " sec");
            System.out.println("File Path: " + musicFile.Path);
            System.out.println("Bitrate: " + musicFile.Bitrate);
            System.out.println("--------");


        } catch (Exception e) {
            e.printStackTrace();
        }



        return musicFile;
    }

}

