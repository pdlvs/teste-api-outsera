import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();
    private static final String CONFIG_FILE = "src/test/resources/config.properties"; // Caminho do arquivo

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                //return;
            }
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUserId() {
        return properties.getProperty("user.id");
    }

    public static void setUserId(String userId) {
        properties.setProperty("user.id", userId);
        saveProperties();
    }

    private static void saveProperties() {
        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
