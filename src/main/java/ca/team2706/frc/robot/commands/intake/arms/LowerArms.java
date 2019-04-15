package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Lowers the intake arms in preparation for dealing with cargo
 */
public class LowerArms extends TimedCommand {

    private Pneumatics.IntakeMode previousIntakeState;

    /**
     * Constructs a new command to lower the intake arms in preparation for handling cargo.
     */
    public LowerArms() {
        super(Config.INTAKE_ARMS_DELAY);
        requires(Pneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        super.initialize();
        previousIntakeState = Pneumatics.getInstance().getMode();
        Pneumatics.getInstance().lowerArms();
    }

    @Override
    protected boolean isFinished() {
        // We're done if time command is done (thus the or statement) or if the arms were in a good position to start.
        return super.isFinished() || previousIntakeState == Pneumatics.IntakeMode.CARGO;
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().stopArmsPneumatics();
    }
}
