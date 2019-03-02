package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.IntakePneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Raises the intake arms to prepare for handling hatches.
 */
public class RaiseArms extends TimedCommand {

    public RaiseArms() {
        super(Config.INTAKE_ARMS_DELAY);
        requires(IntakePneumatics.getInstance());
    }

    @Override
    protected void execute() {
        super.execute();
        IntakePneumatics.getInstance().raiseArms();
    }

    @Override
    protected void end() {
        super.end();
        IntakePneumatics.getInstance().stopArmsPneumatics();
    }
}
