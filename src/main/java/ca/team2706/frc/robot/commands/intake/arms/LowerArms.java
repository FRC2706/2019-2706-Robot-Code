package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Lowers the intake arms in preparation for dealing with cargo
 */
public class LowerArms extends InstantCommand {

    @Override
    protected void initialize() {
        Intake.getInstance().lowerIntake();
    }
}
