package ca.team2706.frc.robot.operatorFeedback;

import ca.team2706.frc.robot.OI;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Kyle Anderson
 */
public class Rumbler extends Command {

    public enum JoystickSelection {
        DRIVER_JOYSTICK, OPERATOR_JOYSTICK, BOTH_JOYSTICKS
    }

    // The joysticks that will be rumbled
    private final Joystick driver;
    private final Joystick operator;

    private boolean isFinished;

    // The last time in seconds of an event.
    private long startTime;

    // States of the pattern
    private static final int RUMBLE = 0;
    private static final int BREAK = 1;

    // Indicate what part of the pattern we're in, RUMBLE or BREAK
    private int state;

    private RumblePattern currentPattern;


    /**
     * Class to rumble the controllers of the robot with the purpose of giving haptic feedback to
     * the drivers for important events.
     *
     * @param timeOn             How long to rumble in milliseconds.
     * @param timeOff            How long to pause between rumbles in milliseconds.
     * @param repeatCount        How long to repeat the pattern. Enter a negative number for infinite, but
     *                           <b>DON'T FORGET TO CALL THE END() FUNCTION.</b>
     * @param controllerToRumble Which controller (one of DRIVER_JOYSTICK, OPERATOR_JOYSTICK or
     *                           BOTH_JOYSTICKS)
     * @param intensity          The rumble intensity setting.  to rumble.
     */
    public Rumbler(long timeOn, long timeOff, int repeatCount, JoystickSelection controllerToRumble,
                   double intensity) {
        this(RumblePattern.createBasic(timeOn, timeOff, repeatCount, controllerToRumble, intensity));

        // Add this command to the scheduler.
        start();
    }

    /**
     * Constructs a new rumbler with the give patterns.
     * @param pattern The pattern to be played on the controller(s).
     */
    public Rumbler(RumblePattern pattern) {
        this.currentPattern = pattern;
        driver = OI.getInstance().getDriverJoystick();
        operator = OI.getInstance().getOperatorJoystick();
        start();
    }

    @Override
    public void start() {
        super.start();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void initialize() {
        super.initialize();
        // Begin rumbling
        rumble(true);
    }

    @Override
    protected void execute() {
        // Get the time passed since last time point
        long timeSinceStart = System.currentTimeMillis() - startTime;

        boolean shouldRumble = currentPattern.shouldRumble(timeSinceStart);
        rumble(shouldRumble);
    }

    @Override
    protected boolean isFinished() {
        return currentPattern.isOver(System.currentTimeMillis() - startTime) || isFinished;
    }

    /**
     * Ends the command nicely, removing it from the command group.
     */
    @Override
    public void end() {
        isFinished = true;
        rumble(false);
    }

    /**
     * Function that toggles the state of the rumble
     *
     * @param on True to turn on rumble, false otherwise.
     */
    private void rumble(boolean on) {
        // Set the state
        state = on ? RUMBLE : BREAK;

        // If rumble is on, full power. Otherwise, no power.
        double rumbleIntensity = (on ? currentPattern.getRumbleIntensity() : 0.0);

        // Rumble the appropriate joysticks
        if (currentPattern.getJoystick() == JoystickSelection.DRIVER_JOYSTICK || currentPattern.getJoystick() == JoystickSelection.BOTH_JOYSTICKS) {
            driver.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
            driver.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
        }

        if (currentPattern.getJoystick() == JoystickSelection.OPERATOR_JOYSTICK || currentPattern.getJoystick() == JoystickSelection.BOTH_JOYSTICKS) {
            operator.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
            operator.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
        }
    }

}
