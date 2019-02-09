package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.commands.bling.BlingController;
import ca.team2706.frc.robot.commands.bling.patterns.BlingPattern;
import ca.team2706.frc.robot.logging.Log;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Arrays;

/**
 * Subsystem for controlling bling operations.
 */
public class Bling extends Subsystem {

    private static Bling currentInstance;

    /**
     * Gets the current Bling subsystem object instance.
     *
     * @return The current Bling instance.
     */
    public static Bling getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new bling object.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new Bling();
        }
    }

    // All of the pattern numbers
    public static final String COLOUR_WIPE = "colorWipe", THEATRE_CHASE = "theaterChase", SOLID = "solid", BLINK = "blink",
            RAINBOW = "rainbow", THEATRE_CHASE_RAINBOW = "theaterChaseRainbow", RAINBOW_CYCLE = "rainbowCycle",
            CLEAR = "clear";

    // COLOUR PRESETS BELOW
    /**
     * A nice purple merge RGB colour.
     */
    public static final int[] MERGERGB = {102, 51, 153}, WHITE = {255, 255, 255}, BLACK = {0, 0, 0},
            ORANGE = {255, 165, 0}, SKYBLUE = {125, 206, 235}, TURQUOISE = {64, 224, 208},
            GREEN = {0, 255, 0}, BLUE = {0, 0, 255}, RED = {255, 0, 0}, PURPLE = {128, 0, 128},
            YELLOW = {255, 255, 0};

    public static final int GOOD_BRIGHTNESS = 128, MAX_BRIGHTNESS = 255;

    /**
     * The networktables key for the bling table.
     */
    private static final String NTKEY = "blingTable";


    // Networktables entries for bling
    private NetworkTableEntry waitMSNT, redNT, greenNT, blueNT, repeatNT, brightnessNT, commandNT;


    private BlingController blingController;

    // Used to make sure we don't run the same command twice in a row.
    private int[] lastRGBArray = new int[3];
    private int lastRepeat = -1;
    private int lastWaitMs = -1;
    private int lastLEDBrightness = -1;
    private String lastCommand = "";

    /**
     * Class used as the basic part of handling bling commands.
     */
    private Bling() {
        final NetworkTable blingTable = NetworkTableInstance.getDefault().getTable(NTKEY);

        // Declare all the necessary variables to work with for networktables setting
        waitMSNT = blingTable.getEntry("wait_ms");
        redNT = blingTable.getEntry("red");
        greenNT = blingTable.getEntry("green");
        blueNT = blingTable.getEntry("blue");
        repeatNT = blingTable.getEntry("repeat");
        brightnessNT = blingTable.getEntry("LED_BRIGHTNESS");
        commandNT = blingTable.getEntry("command");
    }

    @Override
    public BlingController getDefaultCommand() {
        if (blingController == null) {
            blingController = new BlingController();
        }
        return blingController;
    }

    @Override
    protected void initDefaultCommand() {
        setDefaultCommand(getDefaultCommand());
    }

    /**
     * The method used to display bling patterns.
     *
     * @param patternToShow The bling pattern object whose pattern to show.
     */
    public void display(BlingPattern patternToShow) {
        // Run the command
        patternToShow.runCommand();

        display(patternToShow.getBrightness(), patternToShow.getWaitMS(), patternToShow.getRGB(), patternToShow.getCommand(), patternToShow.getRepeatCount());
    }

    /**
     * Displays the given type of LED pattern on the LED strip.
     *
     * @param brightness  The brightness, an integer between 0 and 255, 255 being full brightness
     * @param waitMS      The amount of milliseconds to delay between each pattern
     * @param rgb         The RGB colour code (red, green, blue) to display
     * @param command     The type of pattern to display. Use one of the Bling class constants for patterns.
     * @param repeatCount The number of times to repeat the pattern
     */
    public void display(final int brightness, final int waitMS, final int[] rgb, final String command, final int repeatCount) {
        if (isValidCommand(brightness, waitMS, rgb, command, repeatCount)) {
            // Don't spam the coprocessor with the same command, so determine if this is the same as the last command
            if (!isSameAsLastCommandRun(brightness, waitMS, rgb, command, repeatCount)) {
                lastRepeat = repeatCount;
                lastLEDBrightness = brightness;
                lastWaitMs = waitMS;
                lastCommand = command;
                lastRGBArray = rgb;

                sendPattern(brightness, waitMS, rgb, command, repeatCount);
            }
        }
    }

    private boolean isValidCommand(final int brightness, final int waitMS, final int[] rgb, final String command, final int repeatCount) {
        boolean rgbIsGood = rgb != null && rgb.length == 3;

        if (rgbIsGood) {
            for (int colour : rgb) {
                rgbIsGood = 0 <= colour && colour <= 255;
                // As soon as the rgb is found not to be good, break.
                if (!rgbIsGood) {
                    break;
                }
            }
        }

        return 0 <= brightness && brightness <= Bling.MAX_BRIGHTNESS &&
                waitMS >= 0 &&
                rgbIsGood &&
                repeatCount >= 0;
    }

    /**
     * Determines if the command described by these parameters is the same as the last command that was run.
     *
     * @param brightness  The brightness, an integer between 0 and 255, 255 being full brightness
     * @param waitMS      The amount of milliseconds to delay between each pattern
     * @param rgb         The RGB colour code (red, green, blue) to display
     * @param command     The type of pattern to display. Use one of the Bling class constants for patterns.
     * @param repeatCount The number of times to repeat the pattern
     * @return True if this pattern and the last are the same, false otherwise.
     */
    private boolean isSameAsLastCommandRun(final int brightness, final int waitMS, final int[] rgb, final String command, final int repeatCount) {
        return brightness == lastLEDBrightness &&
                repeatCount == lastRepeat &&
                command.equals(lastCommand) &&
                Arrays.equals(rgb, lastRGBArray) &&
                waitMS == lastWaitMs;
    }

    /**
     * Sends the given pattern to the coprocessor to dislay the pattern.
     * <b>No verification or checking is done.</b>
     *
     * @param brightness  The brightness, an integer between 0 and 255, 255 being full brightness
     * @param waitMS      The amount of milliseconds to delay between each pattern
     * @param rgb         The RGB colour code (red, green, blue) to display
     * @param command     The type of pattern to display. Use one of the Bling class constants for patterns.
     * @param repeatCount The number of times to repeat the pattern
     */
    private void sendPattern(int brightness, int waitMS, int[] rgb, String command, int repeatCount) {
        // Verify that the length of the rgb array is proper.
        if (rgb.length >= 3) {
            // Send pattern parameters to Networktables. The command one must be sent last because that's the cue for the pi to display the pattern
            redNT.setDouble(rgb[0]);
            greenNT.setDouble(rgb[1]);
            blueNT.setDouble(rgb[2]);
            repeatNT.setDouble(repeatCount);
            waitMSNT.setDouble(waitMS);
            brightnessNT.setDouble(brightness);
            commandNT.setString(command);
            Log.i(command);
        }
    }

    /**
     * Clears the LED strip
     */
    public void clearStrip() {
        int[] colour = new int[]{0, 0, 0};
        sendPattern(0, 0, colour, CLEAR, 0);
    }
}
