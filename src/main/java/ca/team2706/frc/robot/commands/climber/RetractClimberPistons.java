package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.ClimberPneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Command for retracting the climber pistons.
 */
public class RetractClimberPistons extends TimedCommand {

    /**
     * Constructs a new RetractClimberPistons command for automatically retracting the climber pistons.
     */
    public RetractClimberPistons() {
        super(1.0);
        requires(ClimberPneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        ClimberPneumatics.getInstance().retractPushers();
    }

    @Override
    protected void end() {
        ClimberPneumatics.getInstance().stopPneumatics();
    }
}
