package ca.team2706.frc.robot.operatorFeedback;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 * @author Kyle Anderson
 */
public class Rumbler extends Command {

    public static enum JoystickSelection {
        DRIVER_JOYSTICK, OPERATOR_JOYSTICK, BOTH_JOYSTICKS
    }

    private boolean isFinished = false;

    /*
     * An integer which is one DRIVER_JOYSTICK, OPERATOR_JOYSTICK or BOTH_JOYSTICKS values.
     * Represents which controller to rumble
     */
    JoystickSelection controllerToRumble;

    // How long to rumble
    private double timeOn;

    // How much break between rumbles
    private double timeOff;

    // How many times to repeat the pattern
    private int repeatCount;

    // The intensity setting for the rumble
    private double intensity;

    // The joysticks that will be rumbled
    private Joystick driver;
    private Joystick operator;

    // The last time in seconds of an event.
    private double timePoint;

    // States of the pattern
    private static final int RUMBLE = 0;
    private static final int BREAK = 1;

    // Indicate what part of the pattern we're in, RUMBLE or BREAK
    int state;


    /**
     * Class to rumble the controllers of the robot with the purpose of giving haptic feedback to
     * the drivers for important events.
     */
    public Rumbler() {
        // Just a default rumble of 1 second 1 time on both joysticks.
        this(1.0, 0, 1, JoystickSelection.BOTH_JOYSTICKS, 1);
    }

    /**
     * Class to rumble the controllers of the robot with the purpose of giving haptic feedback to
     * the drivers for important events.
     *
     * @param timeOn How long to rumble
     * @param timeOff How long to pause between rumbles
     * @param repeatCount How long to repeat the pattern. Enter a negative number for infinite, but
     *        DON'T FORGET TO CALL THE END() FUNCTION.
     * @param controllerToRumble Which controller (one of DRIVER_JOYSTICK, OPERATOR_JOYSTICK or
     *        BOTH_JOYSTICKS) to rumble.
     */
    public Rumbler(double timeOn, double timeOff, int repeatCount, JoystickSelection controllerToRumble) {
        this(timeOn, timeOff, repeatCount, controllerToRumble, 1);
    }


    /**
     * Class to rumble the controllers of the robot with the purpose of giving haptic feedback to
     * the drivers for important events.
     *
     * @param timeOn How long to rumble
     * @param timeOff How long to pause between rumbles
     * @param repeatCount How long to repeat the pattern. Enter a negative number for infinite, but
     *        DON'T FORGET TO CALL THE END() FUNCTION.
     * @param controllerToRumble Which controller (one of DRIVER_JOYSTICK, OPERATOR_JOYSTICK or
     *        BOTH_JOYSTICKS)
     * @param intensity The rumble intensity setting.  to rumble.
     */
    public Rumbler(double timeOn, double timeOff, int repeatCount, JoystickSelection controllerToRumble,
                   double intensity) {
        this.timeOn = timeOn;
        this.timeOff = timeOff;
        this.repeatCount = repeatCount;
        this.intensity = intensity;

        driver = Robot.oi.getDriverJoystick();
        operator = Robot.oi.getOperatorJoystick();

        this.controllerToRumble = controllerToRumble;

        // Add this command to the scheduler.
        start();
    }

    @Override
    public void initialize() {
        // Begin rumbling
        rumble(true);
    }

    @Override
    protected void execute() {
        // Get the time passed since last time point
        double timePassed = Timer.getFPGATimestamp() - timePoint;

        boolean rumbleOver = (state == RUMBLE && timePassed > timeOn);
        boolean breakOver = (state == BREAK && timePassed > timeOff);

        if (rumbleOver) {
            rumble(false);
            if (repeatCount >= 0)
                repeatCount--;
        } else if (breakOver)
            rumble(true);
    }

    @Override
    protected boolean isFinished() {
        return repeatCount == 0 || isFinished;
    }

    @Override
    /**
     * Ends the command nicely, removing it from the command group.
     */
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
        // Set the time point, so we know when to start or stop rumbling
        timePoint = Timer.getFPGATimestamp();

        // Set the state
        state = on ? RUMBLE : BREAK;

        // If rumble is on, full power. Otherwise, no power.
        double rumbleIntensity = (on ? intensity : 0.0);

        // Rumble the appropriate joysticks
        if (controllerToRumble == JoystickSelection.DRIVER_JOYSTICK || controllerToRumble == JoystickSelection.BOTH_JOYSTICKS) {
            driver.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
            driver.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
        }

        if (controllerToRumble == JoystickSelection.OPERATOR_JOYSTICK || controllerToRumble == JoystickSelection.BOTH_JOYSTICKS) {
            operator.setRumble(GenericHID.RumbleType.kRightRumble, rumbleIntensity);
            operator.setRumble(GenericHID.RumbleType.kLeftRumble, rumbleIntensity);
        }
    }

}
