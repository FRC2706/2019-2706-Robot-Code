package ca.team2706.frc.robot.commands.bling.patterns;

import ca.team2706.frc.robot.subsystems.Bling;
import edu.wpi.first.wpilibj.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * The template for Bling Patterns. Shouldn't be instantiated.
 *
 * @author eAUE (Kyle Anderson)
 */
public abstract class BlingPattern {
    // The start time of the pattern.
    private double startTime;

    protected int[] rgbColourCode = new int[3];

    protected int repeatCount = 1000000;
    protected int wait_ms = 50;
    protected int LED_BRIGHTNESS = Bling.GOODBRIGHTNESS;
    protected String command = Bling.COLOUR_WIPE;

    protected List<Integer> operationPeriod = new ArrayList<Integer>();

    /**
     * True if the pattern has already been run at least once
     * and it was the last pattern to run.
     */
    protected boolean hasRun = false;


    /**
     * Gets the time passed since this pattern began to run.
     *
     * @return The time passed since the beginning of this pattern.
     */
    public double getTimeSinceStart() {
        return Timer.getFPGATimestamp() - startTime;
    }

    public BlingPattern() {
        // Initialize with a red colour initially. (values 1 and 2 are already initialized).
        rgbColourCode[0] = 255;
    }

    /**
     * Analyzes whether or not the conditions for this pattern are being met,
     * and returns a boolean based on this.
     * This should be overriden for real bling commands, as by default it returns false every time.
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
     * Gets the repeat counnt for the pattern
     *
     * @return The repeat count (number of times to repeat) of the pattern
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * @return The brightness (from 0 to 255) of the LED strip when displaying this pattern
     */
    public int getBrightness() {
        return LED_BRIGHTNESS;
    }

    /**
     * Gets the string command type for the pattern. Should be one of the Bling subsystem's constants
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Get the delay time between pattern segments
     *
     * @return The delay time between pattern segments in miliseconds
     */
    public int getWaitMS() {
        return wait_ms;
    }

    /**
     * Gets the commands operation periods, which declare when the pattern is supposed to operate.
     *
     * @return A list of BlingController periods when this pattern is supposed to run, one of Teleop, Autonomous or Climb or a combination
     */
    public List<Integer> getPeriod() {
        return operationPeriod;
    }

    /**
     * Called by bling every time the command is run, which would be about every 20 miliseconds.
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
}
