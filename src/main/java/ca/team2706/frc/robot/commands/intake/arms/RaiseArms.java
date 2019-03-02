package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Raises the intake arms to prepare for handling hatches.
 */
public class RaiseArms extends TimedCommand {

    public RaiseArms() {
        super(1.0); // TODO make constant.
    }

    @Override
    protected void execute() {
        super.execute();
        Intake.getInstance().raiseIntake();
    }

    @Override
    protected void end() {
        super.end();
        Intake.getInstance().stopArmsPneumatics();
    }
}
