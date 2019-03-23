package ca.team2706.frc.robot.commands.climber;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Command for running the climber motor to get the robot to climb.
 */
public class Climb extends CommandGroup {

    /**
     * Constructs a new climb command group for the full climb operation.
     */
    public Climb() {
        addParallel(new RunClimberMotor());
        addSequential(new WaitUntilReadyForPistons());
        addSequential(new PushClimberPistons());
    }
}
