package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.Climber;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for retracting the climber mechanism.
 */
public class RetractClimber extends Command {
    @Override
    protected void execute() {
        Climber.getInstance().retractClimber();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
