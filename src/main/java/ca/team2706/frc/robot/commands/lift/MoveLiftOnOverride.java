package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Moves the lift up or down on override.
 */
public class MoveLiftOnOverride extends Command {


    private final boolean moveUp;

    /**
     * Constructs a new command to move the lift up or down using override.
     *
     * @param moveUp True to move up, false to move down.
     */
    public MoveLiftOnOverride(final boolean moveUp) {
        requires(Lift.getInstance());
        this.moveUp = moveUp;
    }

    @Override
    protected void execute() {
        if (moveUp) {
            Lift.getInstance().overrideUp();
        } else {
            Lift.getInstance().overrideDown();
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
