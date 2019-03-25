package ca.team2706.frc.robot.commands.climber.sequences;

import ca.team2706.frc.robot.commands.climber.WaitUntilReadyForPistons;
import ca.team2706.frc.robot.commands.climber.actions.PushClimberPistons;
import ca.team2706.frc.robot.commands.climber.actions.RunClimberMotor;
import ca.team2706.frc.robot.commands.intake.arms.RaiseArmsSafely;
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
        addParallel(new RaiseArmsSafely());
        addSequential(new WaitUntilReadyForPistons());
        addSequential(new PushClimberPistons());
    }
}
