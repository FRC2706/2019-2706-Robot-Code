package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftDownToLowerLevel extends Command {

    public MoveLiftDownToLowerLevel() {
        requires(Lift.getInstance());
    }

    @Override
    public void execute() {
        Lift.getInstance().subtractFromHeightGoal();
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().reachedGoal();
    }

}
