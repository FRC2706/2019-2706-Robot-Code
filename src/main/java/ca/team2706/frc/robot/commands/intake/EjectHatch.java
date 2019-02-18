package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class EjectHatch extends InstantCommand {

    public EjectHatch() {
        requires(Intake.getInstance());
    }

    @Override
    public void execute() {
        Intake.getInstance().ejectHatch();
    }

}