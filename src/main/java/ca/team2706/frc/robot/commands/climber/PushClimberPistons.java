package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.ClimberPneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Constructs a new PushClimberPistons command to push out the climber pistons and finish when they should be fully out.
 */
public class PushClimberPistons extends TimedCommand {

    /**
     * Constructs a new command to push out the climber pneumatics.
     */
    public PushClimberPistons() {
        super(1.0);
        requires(ClimberPneumatics.getInstance());
    }

    @Override
    protected void initialize() {
        ClimberPneumatics.getInstance().pushRobot();
    }

    @Override
    protected void end() {
        ClimberPneumatics.getInstance().stopPneumatics();
    }
}
