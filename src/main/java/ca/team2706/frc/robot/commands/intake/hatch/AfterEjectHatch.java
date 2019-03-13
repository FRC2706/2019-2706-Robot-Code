package ca.team2706.frc.robot.commands.intake.hatch;

import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.lift.MoveLiftToPosition;
import edu.wpi.first.wpilibj.command.CommandGroup;

import java.util.function.Supplier;

/**
 * Command designed to run after ejecting a hatch meant to reset the robot to the state it was at
 * before ejecting the hatch.
 */
public class AfterEjectHatch extends CommandGroup {
    /**
     * Constructs an after eject hatch command with the given lift position.
     *
     * @param position The lift position before ejecting the hatch.
     */
    public AfterEjectHatch(Supplier<Double> position) {
        addSequential(new MovePlunger(MovePlunger.DesiredState.STOWED));
        addParallel(new MoveLiftToPosition(0.5, position));
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
