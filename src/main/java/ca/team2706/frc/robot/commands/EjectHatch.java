package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.subsystems.ElevatorWithPID;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;

public class EjectHatch extends Command {

    public EjectHatch() {
        requires(Intake.getInstance());
        requires(ElevatorWithPID.getInstance());

    }

    @Override
    public void execute() {
        Intake.getInstance().ejectHatch();
    }

    @Override
    public void end() {
        ElevatorWithPID.getInstance().lowertoDeployHatch();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}