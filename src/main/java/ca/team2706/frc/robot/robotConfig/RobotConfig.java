package ca.team2706.frc.robot.robotConfig;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Demo robot configuration manager for managing this robot's configuration.
 * Uses the idea of "global" configurations which are the same for all of our different robots
 * and "local" configurations which vary for each robot.
 * @author Kyle Anderson (KyleRAnderson)
 * ICS4UR
 * 2018-11-17
 */
public class RobotConfig  {

    private static final JsonObject GLOBAL_CONSTANTS, LOCAL_CONSTANTS;

    static {
        JsonObject readObject = readConfigurationFile();

        JsonObject localConstants, globalConstants;
        if (readObject != null) {
            globalConstants = getGlobalConstants(readObject);
            localConstants = getLocalConstants(readObject);
        }
        else {
            // TODO if we set all these to null we have an issue since the robot needs its constants.
            localConstants = null;
            globalConstants = null;
        }
        GLOBAL_CONSTANTS = globalConstants;
        LOCAL_CONSTANTS = localConstants;

        // Begin initialization of all constants.
        SELECTOR_ID = getSelectorId();
        CAMERA_IP = getCameraIp();
    }

    // Location for local and global data.
    private static final String LOCAL_LOC = "local", GLOBAL_LOC = "global";

    // JSON locations for all of the data we're going to be using.
    private static final String CAMERA_IP_LOC = "camera_ip", SELECTOR_ID_LOC = "selector_id";

    // Actual constants that will be used.
    public static final String CAMERA_IP;
    public static final int SELECTOR_ID;

    /**
     * Robot ID for the different robots which may have local configurations.
     */
    public enum RobotID {
        CompetitionBot("competition_bot"), PracticeBot("practice_bot");

        // The JSON key at which this bot's local settings will be found.
        final String jsonKey;
        RobotID(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        public String getJsonKey() {
            return jsonKey;
        }
    }

    /**
     * Gets the json object containing all the global constants.
     * @param object The root json object.
     * @return The JsonObject wrapping all the global constants.
     */
    private static JsonObject getGlobalConstants(JsonObject object) {
        return object.get(GLOBAL_LOC).getAsJsonObject();
    }

    /**
     * Gets the json object containing all local constants.
     * @param object The root json object.
     * @return The JsonObject wrapping all local constants.
     */
    private static JsonObject getLocalConstants(JsonObject object) {
        return object.get(LOCAL_LOC).getAsJsonObject().get(getRobotId().getJsonKey()).getAsJsonObject();
    }

    /**
     * Reads the config file and generated the appropriate JSON object from the read data.
     * @return The read json object from the file.
     */
    private static JsonObject readConfigurationFile() {
        JsonObject object;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(RobotConfig.class.getResource("config.json").toString()));
            ArrayList<String> lines = new ArrayList<>();

            // Read all lines of the file.
            String line;
            do {
                line = reader.readLine();
                lines.add(line);
            }
            while (line != null);

            StringBuilder totalFile = new StringBuilder();
            lines.forEach(totalFile::append);

            JsonParser parser = new JsonParser();
            object = parser.parse(totalFile.toString()).getAsJsonObject();
        }
        catch (IOException e) {
            object = null;
        }

        return object;
    }

    /**
     * Gets this robot's ID.
     * @return This Robot's ID as an instance of the RobotID enum.
     */
    private static RobotID getRobotId() {
        // TODO read file on RoboRIO to get robot ID.
        return RobotID.CompetitionBot;
    }

    private static int getSelectorId() {
        return getProperty(SELECTOR_ID_LOC).getAsInt();
    }

    private static String getCameraIp() {
        return getProperty(CAMERA_IP_LOC).getAsString();
    }

    private static JsonElement getProperty(final String location) {
        return (LOCAL_CONSTANTS.get(location) != null) ? LOCAL_CONSTANTS.get(location) : GLOBAL_CONSTANTS.get(location);
    }
}
