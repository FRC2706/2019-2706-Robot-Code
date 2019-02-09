package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.subsystems.ElevatorWithPID;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftDownToLowerLevel extends Command {

    public MoveLiftDownToLowerLevel() {
        requires(ElevatorWithPID.getInstance());
    }

    @Override
    public void execute() {
        ElevatorWithPID.getInstance().subtractFromHeightGoal();
    }

    @Override
    public void end() {
        ElevatorWithPID.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return ElevatorWithPID.getInstance().reachedGoal();
    }

}
