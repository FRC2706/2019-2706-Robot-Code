package ca.team2706.frc.robot.operatorfeedback.rumbler;

public class BasicRumble extends RumblePattern {
    private final int repeatCount;
    private final long timeOff;
    private final long timeOn;

    /**
     * Creates a new basic rumble pattern.
     *
     * @param timeOn             Milliseconds to rumble each cycle.
     * @param timeOff            Milliseconds to not rumble for each cycle.
     * @param repeatCount        The number of cycles to repeat.
     * @param controllerToRumble Which controllers to rumble.
     * @param rumbleIntensity    The intensity of the rumble (between 0 and 1.0)
     */
    public BasicRumble(final long timeOn,
                       final long timeOff,
                       final int repeatCount,
                       final Rumbler.JoystickSelection controllerToRumble,
                       final double rumbleIntensity) {
        joystick = controllerToRumble;
        intensity = rumbleIntensity;
        this.timeOn = timeOn;
        this.timeOff = timeOff;
        this.repeatCount = repeatCount;
    }

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
}
