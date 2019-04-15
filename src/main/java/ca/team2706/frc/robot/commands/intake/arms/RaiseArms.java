package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Raises the intake arms to prepare for handling hatches.
 */
public class RaiseArms extends TimedCommand {

    private Pneumatics.IntakeMode previousIntakeState;

    /**
     * Constructs a new command to raise the intake arms in preparation for handling hatches.
     */
    public RaiseArms() {
        super(Config.INTAKE_ARMS_DELAY);
        requires(Pneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        super.initialize();
        previousIntakeState = Pneumatics.getInstance().getMode();
        Pneumatics.getInstance().raiseArms();
    }

    @Override
    protected boolean isFinished() {
        // We're done if the timed command is done (thus the or statement) or if the arms were in hatch mode to start.
        return super.isFinished() || previousIntakeState == Pneumatics.IntakeMode.HATCH;
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().stopArmsPneumatics();
    }
}
