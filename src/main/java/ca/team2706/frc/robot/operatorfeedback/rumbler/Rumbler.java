package ca.team2706.frc.robot.operatorfeedback.rumbler;

import ca.team2706.frc.robot.OI;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Class for running rumble patterns on the robot.
 */
public class Rumbler extends Command {

    public enum JoystickSelection {
        DRIVER_JOYSTICK, OPERATOR_JOYSTICK, BOTH_JOYSTICKS
    }

    private boolean isFinished;

    // The last time in seconds of an event.
    private long startTime;

    /**
     * True if currently rumbling, false otherwise.
     */
    private boolean isRumbling;

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
        this(new BasicRumble(timeOn, timeOff, repeatCount, controllerToRumble, intensity));

        // Add this command to the scheduler.
        start();
    }

    /**
     * Constructs a new rumbler with the give patterns.
     *
     * @param pattern The pattern to be played on the controller(s).
     */
    public Rumbler(RumblePattern pattern) {
        this.currentPattern = pattern;
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
     * Determines if the controllers are currently being rumbled or not.
     *
     * @return True if currently rumbling, false otherwise.
     */
    public boolean isRumbling() {
        return isRumbling;
    }

    /**
     * Function that toggles the state of the rumble
     *
     * @param on True to turn on rumble, false otherwise.
     */
    private void rumble(boolean on) {
        // If we're already doing the thing that we're supposed to be, don't spam.
        if (on != isRumbling()) {
            isRumbling = on;

            // If rumble is on, full power. Otherwise, no power.
            double rumbleIntensity = (on ? currentPattern.getRumbleIntensity() : 0.0);

            // Rumble the appropriate joysticks
            if (currentPattern.getJoystick() == JoystickSelection.DRIVER_JOYSTICK || currentPattern.getJoystick() == JoystickSelection.BOTH_JOYSTICKS) {
                final Joystick driver = OI.getInstance().getDriverStick();
                driver.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
                driver.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
            }

            if (currentPattern.getJoystick() == JoystickSelection.OPERATOR_JOYSTICK || currentPattern.getJoystick() == JoystickSelection.BOTH_JOYSTICKS) {
                final Joystick operator = OI.getInstance().getControlStick();
                operator.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
                operator.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
            }
        }
    }

}
