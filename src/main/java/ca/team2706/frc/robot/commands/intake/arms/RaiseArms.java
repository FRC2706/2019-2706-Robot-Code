package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Raises the intake arms to prepare for handling hatches.
 */
public class RaiseArms extends InstantCommand {
    @Override
    protected void execute() {
        super.execute();
        Intake.getInstance().raiseIntake();
    }
}
