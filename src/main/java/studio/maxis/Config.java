package studio.maxis;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class Config {
    public static void loadConf() throws Exception {
        URL defaultConfigPath = Config.class.getClassLoader().getResource("default.properties");
        System.out.println(defaultConfigPath);
        Properties defaultProps = new Properties();
        defaultProps.load(new FileInputStream(Paths.get(defaultConfigPath.toURI()).toFile()));

        URL appConfigPath = Config.class.getClassLoader().getResource("app.properties");
        Properties appProps = new Properties(defaultProps);
        appProps.load(new FileInputStream(Paths.get(appConfigPath.toURI()).toFile()));


        //(conf.getProperty("key"); <|> key=secret) == secret
        int size = appProps.size();
        String version = appProps.getProperty("version");
        String name = appProps.getProperty("name");
        String github = appProps.getProperty("github");
        String author = appProps.getProperty("author");
        String configRenewal = appProps.getProperty("configRenewal");

        Boolean needNewConfig = size == 0 || Objects.equals(version, "0.1.0") || name.equals("ColdBrew") || github.equals("https://github.com/137-Trimethylxanthin/Cold-Brew") || author.equals("137-Trimethylxanthin") || configRenewal.equalsIgnoreCase("true");
        if (needNewConfig){
            System.out.println("No config file found, creating one...");

            for (String key : defaultProps.stringPropertyNames()) {
                String value = defaultProps.getProperty(key);
                appProps.setProperty(key, value);
            }

            System.out.println("Done!");
            System.out.println(appProps.size());
        }






    }

    public static void storeConf(String key, String value) throws URISyntaxException, IOException {
        URL appConfigPath = Config.class.getClassLoader().getResource("app.properties");
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(Paths.get(appConfigPath.toURI()).toFile()));
        appProps.put(key, value);

    }
}
