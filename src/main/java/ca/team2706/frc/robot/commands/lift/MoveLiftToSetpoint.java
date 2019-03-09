package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift to a given setpoint.
 */
public class MoveLiftToSetpoint extends Command {

    private final int setpoint;

    /**
     * Number of cycles in which the lift is at the correct position.
     */
    private int doneCycles = 0;

    private final int minDoneCycles;

    /**
     * Moves the lift to the currently set setpoint, depending on the Intake mode (either cargo setpoints
     * or hatch setpoints).
     *
     * @param setpoint      The setpoint number.
     * @param minDoneCycles Minimum number of cycles in which the lift has to be in position.
     */
    public MoveLiftToSetpoint(final int setpoint, final int minDoneCycles) {
        requires(Lift.getInstance());
        this.setpoint = setpoint;
        this.minDoneCycles = minDoneCycles;
    }

    @Override
    protected void initialize() {
        Lift.getInstance().moveToSetpoint(1.0, setpoint);
    }

    @Override
    protected boolean isFinished() {
        if (Lift.getInstance().hasReachedSetpoint(setpoint)) {
            doneCycles++;
        } else {
            doneCycles = 0;
        }

        return doneCycles >= minDoneCycles;
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }
}
