package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.subsystems.Climber;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for running the climber motor to get the robot to climb.
 */
public class Climb extends Command {
    @Override
    protected void execute() {
        Climber.getInstance().runClimberUp();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
