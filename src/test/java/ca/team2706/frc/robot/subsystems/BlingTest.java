package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.commands.bling.patterns.BlingPattern;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

public class BlingTest {

    @Tested
    Bling bling;

    @Mocked
    NetworkTable table;

    @Mocked
    NetworkTableInstance instance;

    @Injectable
    private NetworkTableEntry waitMSNT, redNT, greenNT, blueNT, repeatNT, brightnessNT, commandNT;

    @Before
    public void setUp() {
        new Expectations() {{
            table.getEntry("wait_ms");
            result = waitMSNT;

            table.getEntry("red");
            result = redNT;

            table.getEntry("green");
            result = greenNT;

            table.getEntry("blue");
            result = blueNT;

            table.getEntry("repeat");
            result = repeatNT;

            table.getEntry("LED_BRIGHTNESS");
            result = brightnessNT;

            table.getEntry("command");
            result = commandNT;
        }};
    }

    /**
     * Tests the method which takes a BlingPattern object as a parameter to display a pattern.
     */
    @Test
    public void testDisplayWithPattern(@Mocked BlingPattern pattern) {
        new Expectations() {{
            pattern.getRGB();
            result = new int[]{1, 1, 1};
        }};

        bling.display(pattern);

        new Verifications() {{
            pattern.runCommand();
            times = 1;
        }};
    }

    /**
     * Tests to ensure that setting a pattern to be displayed using the method that takes all the raw parameters
     * works.
     */
    @Test
    public void testDisplayWithParams() {
        displayAndExpectBack(Bling.MAX_BRIGHTNESS, 50, new int[]{1, 1, 1}, "Hello", 2535);
        displayAndExpectBack(Bling.GOOD_BRIGHTNESS, 0, new int[]{100, 100, 100}, Bling.SOLID, 5000);
        displayAndExpectBack(20, 1, new int[]{80, 3, 4}, Bling.THEATRE_CHASE_RAINBOW, 100000);
        displayAndExpectBack(100, 200, new int[]{12, 2, 9}, Bling.COLOUR_WIPE, 9);
        displayAndExpectBack(90, 30, new int[]{13, 255, 2}, Bling.BLINK, 5);
    }

    /**
     * Utility method to display a pattern and expect that pattern to be displayed.
     *
     * @param brightness  The brightness, an integer between 0 and 255, 255 being full brightness
     * @param waitMS      The amount of milliseconds to delay between each pattern
     * @param rgb         The RGB colour code (red, green, blue) to display
     * @param command     The type of pattern to display. Use one of the Bling class constants for patterns.
     * @param repeatCount The number of times to repeat the pattern
     */
    private void displayAndExpectBack(final int brightness, final int waitMS, final int[] rgb, final String command, final int repeatCount) {
        bling.display(brightness, waitMS, rgb, command, repeatCount);

        new Verifications() {{
            commandNT.setString(command);
            times = 1;

            brightnessNT.setDouble(brightness);
            times = 1;

            repeatNT.setDouble(repeatCount);
            times = 1;

            blueNT.setDouble(rgb[2]);
            times = 1;

            greenNT.setDouble(rgb[1]);
            times = 1;

            redNT.setDouble(rgb[0]);
            times = 1;

            waitMSNT.setDouble(waitMS);
            times = 1;
        }};
    }

    /**
     * Tries to shove invalid parameters into the display and sees if everything still works.
     */
    @Test
    public void testDisplayWithInvalidParams() {
        // One parameter in each of the following is invalid.
        bling.display(Bling.MAX_BRIGHTNESS + 200, 50, new int[]{0, 0, 0}, "", 20); // Brightness too large
        bling.display(Bling.MAX_BRIGHTNESS, 50, new int[]{0, 0, 0}, "", -1); // Repeat count negative
        bling.display(Bling.GOOD_BRIGHTNESS, -1, new int[]{0, 0, 0}, "", 20); // Wait MS negative
        bling.display(Bling.GOOD_BRIGHTNESS, 50, new int[]{0, 0, 256}, "", 20); // Blue value too large
        bling.display(Bling.GOOD_BRIGHTNESS, 50, new int[]{0, 1000, 0}, "", 20); // Green value too large
        bling.display(Bling.GOOD_BRIGHTNESS, 50, new int[]{-200, 0, 0}, "", 20); // Red value too small
        bling.display(-Bling.GOOD_BRIGHTNESS, 50, new int[]{0, 0, 0}, "", 20); // Brightness too small.
        bling.display(Bling.GOOD_BRIGHTNESS, 50, null, "", 20); /// RGB is null
        bling.display(Bling.GOOD_BRIGHTNESS, 50, new int[]{0, 0, 0, 0}, "", 20); // RGB has too many indices.
        bling.display(Bling.GOOD_BRIGHTNESS, 50, new int[]{0, 0}, "", 20); // RGB has too few indices.

        new Verifications() {{
            commandNT.setString(anyString);
            times = 0;

            brightnessNT.setDouble(anyDouble);
            times = 0;

            repeatNT.setDouble(anyDouble);
            times = 0;

            blueNT.setDouble(anyDouble);
            times = 0;

            greenNT.setDouble(anyDouble);
            times = 0;

            redNT.setDouble(anyDouble);
            times = 0;

            waitMSNT.setDouble(anyDouble);
            times = 0;
        }};
    }

    /**
     * Tests to ensure that the bling subsystem will not send the command to display the same pattern twice.
     */
    @Test
    public void testDoesNotDisplayTheSamePatternTwice() {
        // Simply call the display method several times for the same pattern.
        for (int i = 0; i < 4; i++) {
            bling.display(100, 50, new int[]{1, 1, 1}, "Display", 2);
        }

        // Then make sure it's only displayed once.
        new Verifications() {{
            commandNT.setString(anyString);
            times = 1;

            brightnessNT.setDouble(anyDouble);
            times = 1;

            repeatNT.setDouble(anyDouble);
            times = 1;

            blueNT.setDouble(anyDouble);
            times = 1;

            greenNT.setDouble(anyDouble);
            times = 1;

            redNT.setDouble(anyDouble);
            times = 1;

            waitMSNT.setDouble(anyDouble);
            times = 1;
        }};
    }

    /**
     * Tests to ensure that the bling subsystem clears the LED string properly when commanded to.
     */
    @Test
    public void testClearStrip() {
        bling.clearStrip();

        new Verifications() {{
            commandNT.setString(Bling.CLEAR);
            times = 1;

            brightnessNT.setDouble(0);
            times = 1;
        }};
    }
}