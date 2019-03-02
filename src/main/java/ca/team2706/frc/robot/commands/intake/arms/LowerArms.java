package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.IntakePneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Lowers the intake arms in preparation for dealing with cargo
 */
public class LowerArms extends TimedCommand {

    public LowerArms() {
        super(Config.INTAKE_ARMS_DELAY);
        requires(IntakePneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        IntakePneumatics.getInstance().lowerArms();
    }

    @Override
    protected void end() {
        super.end();
        IntakePneumatics.getInstance().stopArmsPneumatics();
    }
}
