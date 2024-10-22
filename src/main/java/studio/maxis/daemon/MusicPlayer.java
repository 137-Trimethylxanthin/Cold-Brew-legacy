package studio.maxis.daemon;

//mp3 files will be to wav
import javax.sound.sampled.*;

//read files
import java.io.*;

import ws.schild.jave.*;




import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import ws.schild.jave.encode.*;



public class MusicPlayer {

    static Clip wavPlayer;

    private static String whereToSaveTempWavs = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +"ColdBrew.wav";
    private static boolean isTemp = false;

    public static void otherPlayer(String path) {
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec("pcm_s16le");  // Use codec for WAV format
        audioAttributes.setBitRate(128000);     // Set the desired bit rate

        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setAudioAttributes(audioAttributes);

        File mp3File = new File(path);
        File wavFile = new File(whereToSaveTempWavs);

        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(mp3File), wavFile, encodingAttributes);
        } catch (IllegalArgumentException | EncoderException e) {
            e.printStackTrace();
        }
        isTemp = true;
        wavPlayer(whereToSaveTempWavs);
    }


    public static void wavPlayer(String path) {
        try {
            File file = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            wavPlayer = AudioSystem.getClip();
            wavPlayer.open(audioIn);
            wavPlayer.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    if (wavPlayer.getMicrosecondLength() == wavPlayer.getMicrosecondPosition()){
                        DaemonLogic.currentPlayingIndex++;
                        if (isTemp){
                            File tempFile = new File(path);
                            tempFile.delete();
                            isTemp = false;
                        }
                    }

                }
            });
            setVolume(DaemonLogic.currentVolumen);
            wavPlayer.start();


       } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }



    public static void stop() {
        if (wavPlayer != null) {
            wavPlayer.stop();
            wavPlayer.close();
            wavPlayer.flush();
        }
    }

    public static void pause() {
        if (wavPlayer != null) {
            wavPlayer.stop();
        }
    }
    public static void resume() {
        if (wavPlayer != null) {
            wavPlayer.start();
        }
    }


    public static Boolean currentlyPlaying(){
        boolean question = false;
        if (wavPlayer != null) {
            question = wavPlayer.isActive();
        }
        return question;
    }
    public static void setVolume( float volume) {
        if (wavPlayer != null) {
            FloatControl vol = (FloatControl) wavPlayer.getControl(FloatControl.Type.MASTER_GAIN); // F*CKING MASTER GAIN IS THE VOLUME AND DIDNT WORK BEFORE BUT AFTER I LOOK IT UP VIA A FUNCTION IT WORKS -_-
            if (vol != null) {
                if (volume > 6.0206f){
                    volume = 6.0206f;
                }
                if (volume <= -65.0f){
                    mute(true);
                }else {
                    mute(false);
                }

                vol.setValue(volume);
            }
        }
    }
    public static void mute(boolean mute) {
        if (wavPlayer != null) {
            BooleanControl muteControl = (BooleanControl) wavPlayer.getControl(BooleanControl.Type.MUTE); //Still hate this sh*t
            if (muteControl != null) {
                muteControl.setValue(mute);
            }
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

