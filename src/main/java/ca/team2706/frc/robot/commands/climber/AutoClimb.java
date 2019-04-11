package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.commands.drivebase.DriveForwardWithTime;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

/**
 * Command for automatically climbing to the second level hab platform.
 */
public class AutoClimb extends CommandGroup {

    /**
     * Constructs a new automatic command to climb to the habitation platform second level.
     */
    public AutoClimb() {
        addSequential(new DriveForwardWithTime(0.075, -0.2));
        addSequential(new WaitCommand(0.2));
        addSequential(new MoveFrontClimberPistons(pneumaticState -> PneumaticState.DEPLOYED));
        addSequential(new DriveForwardWithTime(2, 0.2)); // Needs to be accurate to avoid turning
        addSequential(new MoveFrontClimberPistons(pneumaticState -> PneumaticState.STOWED));
        addSequential(new DriveForwardWithTime(0.25, 0.25));
        addParallel(new DriveForwardWithTime(Double.POSITIVE_INFINITY, 0.25));
        addSequential(new MoveBackClimberPistons(pneumaticState -> PneumaticState.DEPLOYED));
        addSequential(new WaitCommand("Climb Last Stage Delay", 1.0));
        addSequential(new MoveBackClimberPistons(pneumaticState -> PneumaticState.STOWED));
    }
}
