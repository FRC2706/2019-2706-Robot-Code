package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftUpToHigherLevel extends Command {

    public MoveLiftUpToHigherLevel() {
        requires(Lift.getInstance());
    }

    @Override
    public void execute() {
        //Lift.getInstance().addToHeightGoal();
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;//Lift.getInstance().reachedGoal();
    }

}
