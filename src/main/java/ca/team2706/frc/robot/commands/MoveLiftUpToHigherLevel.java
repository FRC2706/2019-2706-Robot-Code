package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.subsystems.ElevatorWithPID;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftUpToHigherLevel extends Command {
    
    public MoveLiftUpToHigherLevel(){
        requires(ElevatorWithPID.getInstance());
    }

    @Override
    public void execute() {
        ElevatorWithPID.getInstance().addToHeightGoal();
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
