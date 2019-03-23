package ca.team2706.frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Command for retracting the climber mechanism.
 */
public class RetractClimber extends CommandGroup {
    public RetractClimber() {
        addParallel(new RetractClimberMotors());
        addSequential(new RetractClimberPistons());
    }
}
