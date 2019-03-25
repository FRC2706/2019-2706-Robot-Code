package ca.team2706.frc.robot.commands.climber.actions;

import ca.team2706.frc.robot.subsystems.ClimberMotor;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for running the climber motor forward.
 */
public class RunClimberMotor extends Command {

    /**
     * Constructs a new command to run the climber motors.
     */
    public RunClimberMotor() {
        requires(ClimberMotor.getInstance());
    }

    @Override
    protected void execute() {
        ClimberMotor.getInstance().runMotorForward();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
