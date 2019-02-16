package ca.team2706.frc.robot.commands.bling.patterns;

import java.util.ArrayList;
import java.util.List;

import ca.team2706.frc.robot.commands.bling.BlingController;
import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.Timer;

/**
 * The template for Bling Patterns. Shouldn't be instantiated.
 *
 * @author Kyle Anderson
 */
public abstract class BlingPattern {
    protected int[] rgbColourCode = new int[3];
    protected int repeatCount = 1000000;
    protected int waitMs = 50;
    protected int ledBrightness = Bling.GOOD_BRIGHTNESS;
    protected String command = Bling.COLOUR_WIPE;
    protected List<BlingController.Period> operationPeriod = new ArrayList<>();
    /**
     * True if the pattern has already been run at least once
     * and it was the last pattern to run.
     */
    protected boolean hasRun = false;
    // The start time of the pattern.
    private double startTime;


    public BlingPattern() {
        // Initialize with a red colour initially. (values 1 and 2 are already initialized).
        rgbColourCode[0] = 255;
    }

    /**
     * Gets the time passed since this pattern began to run.
     *
     * @return The time passed since the beginning of this pattern.
     */
    public double getTimeSinceStart() {
        return Timer.getFPGATimestamp() - startTime;
    }

    /**
     * Analyzes whether or not the conditions for this pattern are being met,
     * and returns a boolean based on this.
     * This should be overridden for real bling commands, as by default it returns false every time.
     *
     * @return True if this bling pattern's operating conditions are met, false
     * otherwise.
     */
    public abstract boolean conditionsMet();

    /**
     * Gets an integer array representation of the RGB colour code for the pattern
     *
     * @return The RGB array representation of the pattern's colour
     */
    public int[] getRGB() {
        return rgbColourCode;
    }

    /**
     * Gets the repeat count for the pattern
     *
     * @return The repeat count (number of times to repeat) of the pattern
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    protected void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    /**
     * @return The brightness (from 0 to 255) of the LED strip when displaying this pattern
     */
    public int getBrightness() {
        return ledBrightness;
    }

    /**
     * Gets the string command type for the pattern. Should be one of the Bling subsystem's constants
     *
     * @return The string command type for this pattern.
     */
    public String getCommand() {
        return command;
    }

    protected void setCommand(String command) {
        this.command = command;
    }

    /**
     * Get the delay time between pattern segments
     *
     * @return The delay time between pattern segments in milliseconds
     */
    public int getWaitMS() {
        return waitMs;
    }

    /**
     * Gets the commands operation periods, which declare when the pattern is supposed to operate.
     *
     * @return A list of BlingController periods when this pattern is supposed to run, one of Teleop, Autonomous or Climb or a combination
     */
    public List<BlingController.Period> getPeriod() {
        return operationPeriod;
    }

    /**
     * Called by bling every time the command is run, which would be about every 20 milliseconds.
     * If the command would like to run something while it is being displayed,
     * it should be run here.
     */
    public void runCommand() {
    }

    /**
     * Should be run at the beginning of the command to help initialize it.
     */
    public void initialize() {
        hasRun = true;
        startTime = Timer.getFPGATimestamp();
    }

    /**
     * Should be called when the pattern is ended.
     * Resets the pattern so it can be run again.
     */
    public void end() {
        hasRun = false;
    }

    /**
     * Sets the rgb colour to be used.
     *
     * @param red   Amount of red (0 to 255)
     * @param green Amount of green (0 to 255)
     * @param blue  Amount of blue (0 to 255)
     */
    protected void setRgbColourCode(final int red, final int green, final int blue) {
        rgbColourCode[0] = red;
        rgbColourCode[1] = green;
        rgbColourCode[2] = blue;
    }

    protected void setWaitMs(int waitMs) {
        this.waitMs = waitMs;
    }

    protected void setLedBrightness(int ledBrightness) {
        this.ledBrightness = ledBrightness;
    }
}
