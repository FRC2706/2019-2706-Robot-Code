package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.ClimberMotor;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command that only finished when the climber motors are ready for the pistons to be pushed out.
 */
public class WaitUntilReadyForPistons extends Command {

    @Override
    protected boolean isFinished() {
        return ClimberMotor.getInstance().isClimberReadyForPistons();
    }
}
