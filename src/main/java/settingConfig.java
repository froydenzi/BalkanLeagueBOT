import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class settingConfig {
    InputStream inputStream;

    public void getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            Main.FACEITTOKEN = prop.getProperty("faceittoken");
            Main.DISCORDTOKEN = prop.getProperty("discordtoken");
            faceitMessageListener.setPrefix = prop.getProperty("prefix");
            databaseAdapter.DB_PASSWORD = prop.getProperty("databasePassword");
            databaseAdapter.DB_ADRES = prop.getProperty("server");
            databaseAdapter.DB_NAME = prop.getProperty("databaseUser");
            databaseAdapter.DB_PORT = Integer.parseInt(prop.getProperty("port"));
            databaseAdapter.DB_USER = prop.getProperty("databaseName");
            faceitMessageListener.moveme = prop.getProperty("movechannel");
            faceitMessageListener.movetocha = prop.getProperty("movetolobby");
            faceitMessageListener.auth = prop.getProperty("authchannel");
            faceitMessageListener.webhookname = prop.getProperty("webhookname");
            faceitMessageListener.hubcategory = prop.getProperty("hubcategory");
            faceitMessageListener.hubrole = prop.getProperty("hubrole");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}