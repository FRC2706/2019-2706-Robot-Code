package ca.team2706.frc.robot.commands.climber.sequences;

import ca.team2706.frc.robot.commands.climber.actions.RunClimberMotor;
import ca.team2706.frc.robot.subsystems.ClimberMotor;

/**
 * Command for running the climber motor until the robot is pivoted high enough.
 */
public class RunClimberMotorUntilHighEnough extends RunClimberMotor {
    @Override
    protected boolean isFinished() {
        return super.isFinished() || ClimberMotor.getInstance().isClimberReadyForPistons();
    }
}
