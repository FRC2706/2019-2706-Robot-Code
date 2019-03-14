package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Lowers the intake arms in preparation for dealing with cargo
 */
public class LowerArms extends TimedCommand {

    /**
     * Constructs a new command to lower the intake arms in preparation for handling cargo.
     */
    public LowerArms() {
        super(Config.INTAKE_ARMS_DELAY);
        requires(Pneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        Pneumatics.getInstance().lowerArms();
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().stopArmsPneumatics();
    }
}
