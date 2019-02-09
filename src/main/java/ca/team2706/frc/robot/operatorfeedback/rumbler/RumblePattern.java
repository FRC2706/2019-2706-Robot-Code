package ca.team2706.frc.robot.operatorfeedback.rumbler;

/**
 * Rumble pattern class for controlling and creating custom rumbler patterns as well as basic ones.
 *
 * @author Kyle Anderson
 */
public abstract class RumblePattern {
    /**
     * Gets the rumble intensity for this rumble pattern.
     *
     * @return The rumble intensity.
     */
    public double getRumbleIntensity() {
        return intensity;
    }

    protected double intensity;

    /**
     * Gets the joystick selection for which joystick should be rumbling.
     *
     * @return The joystick which should be rumbled.
     */
    public Rumbler.JoystickSelection getJoystick() {
        return joystick;
    }

    protected Rumbler.JoystickSelection joystick;

    /**
     * Determines if this pattern should be rumbling given the amount of time that the pattern has been on.
     *
     * @param millisecondsOn The amount of time that the pattern has been on.
     * @return True if the pattern should currently be rumbling, false otherwise.
     */
    abstract boolean shouldRumble(long millisecondsOn);

    /**
     * Determines if this rumbler pattern is over, given the amount of time that the pattern has been running.
     *
     * @param millisecondsOn The number of milliseconds that this pattern has been running.
     * @return True if the pattern is over and false otherwise.
     */
    abstract boolean isOver(long millisecondsOn);
}
