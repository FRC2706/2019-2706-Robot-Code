package ca.team2706.frc.robot.operatorFeedback;

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
     * @return
     */
    public Rumbler.JoystickSelection getJoystick() {
        return joystick;
    }

    protected Rumbler.JoystickSelection joystick;

    /**
     * Determines if this pattern should be rumbling given the amount of time that the pattern has been on.
     * @param millisecondsOn The amount of time that the pattern has been on.
     * @return True if the pattern should currently be rumbling, false otherwise.
     */
    abstract boolean shouldRumble(long millisecondsOn);

    /**
     * Determines if this rumbler pattern is over, given the amount of time that the pattern has been running.
     * @param millisecondsOn The number of milliseconds that this pattern has been running.
     * @return True if the pattern is over and false otherwise.
     */
    abstract boolean isOver(long millisecondsOn);


    /**
     * Helper method for creating a basic rumbler pattern with time on - time off pairs.
     *
     * @param timeOn             How long to rumble, in milliseconds.
     * @param timeOff            How long to pause between rumbles, in milliseconds.
     * @param repeatCount        How many times to repeat the pattern.
     * @param controllerToRumble Which controller (one of DRIVER_JOYSTICK, OPERATOR_JOYSTICK or
     *                           BOTH_JOYSTICKS)
     * @param rumbleIntensity    The rumble intensity setting.  to rumble.
     * @return The created RumblePattern.
     */
    public static RumblePattern createBasic(final long timeOn,
                                            final long timeOff,
                                            final int repeatCount,
                                            final Rumbler.JoystickSelection controllerToRumble,
                                            final double rumbleIntensity) {
        // Create and return the rumble pattern.
        return new RumblePattern() {
            private Rumbler.JoystickSelection joystickSelection = controllerToRumble;
            private double intensity = rumbleIntensity;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                long currentCycleTime = millisecondsOn % (timeOn + timeOff);
                return currentCycleTime < timeOn;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                long completedCycles = millisecondsOn / (timeOn + timeOff); // Determine how many runs we've done.
                return completedCycles >= repeatCount;
            }
        };
    }
}
