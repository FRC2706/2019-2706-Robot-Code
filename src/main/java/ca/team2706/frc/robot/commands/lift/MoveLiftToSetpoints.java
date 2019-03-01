package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift to a given setpoint.
 */
public class MoveLiftToSetpoints extends Command {

    private final int setpoint;


    /**
     * Moves the lift to the currently set setpoint, depending on the Intake mode (either cargo setpoints
     * or hatch setpoints).
     *
     * @param setpoint The setpoint number.
     */
    public MoveLiftToSetpoints(final int setpoint) {
        requires(Lift.getInstance());
        this.setpoint = setpoint;
    }

    @Override
    protected void execute() {
        Lift.getInstance().moveToSetpoint(1.0, setpoint);
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().hasReachedSetpoint(setpoint);
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }
}
