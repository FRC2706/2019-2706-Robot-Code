package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.ClimberMotor;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for running the climber motors backward.
 */
public class RetractClimberMotors extends Command {

    /**
     * Constructs a new command for running the climber motors backward.
     */
    public RetractClimberMotors() {
        requires(ClimberMotor.getInstance());
    }

    @Override
    protected void execute() {
        ClimberMotor.getInstance().runMotorBackward();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
