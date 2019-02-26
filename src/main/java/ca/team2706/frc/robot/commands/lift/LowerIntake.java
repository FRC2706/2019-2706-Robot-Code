package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;

public class LowerIntake extends Command {

    public LowerIntake() {
        requires(Intake.getInstance());
    }

    @Override
    public void execute() {
        Intake.getInstance().lowerIntake();
    }

    @Override
    public void end() {
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}