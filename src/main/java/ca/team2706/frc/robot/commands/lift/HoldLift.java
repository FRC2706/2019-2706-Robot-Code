package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for holding the position of the lift when there are no inputs being made to it.
 */
public class HoldLift extends Command {

    /**
     * Height to hold in feet.
     */
    private double heightToHold;

    public HoldLift() {
        requires(Lift.getInstance());
    }

    @Override
    protected void initialize() {
        super.initialize();
        heightToHold = Lift.getInstance().getLiftHeight();
    }

    @Override
    protected void execute() {
        super.execute();
        Lift.getInstance().setPosition(0.7, heightToHold);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
