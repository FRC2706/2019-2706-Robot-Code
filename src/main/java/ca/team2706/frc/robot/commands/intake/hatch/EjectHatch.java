package ca.team2706.frc.robot.commands.intake.hatch;

import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.lift.LiftPosition;
import ca.team2706.frc.robot.commands.lift.MoveLiftToPosition;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command for ejecting hatches from the mechanism.
 */
public class EjectHatch extends CommandGroup {

    private MoveLiftToPosition liftMover;

    /**
     * Constructs the command to eject hatches.
     *
     * @param elevatorPosition The elevator position object to add the elevator's current position to.
     */
    public EjectHatch(LiftPosition elevatorPosition) {
        requires(Intake.getInstance());

        addSequential(new InstantCommand(() -> elevatorPosition.setPosition(Lift.getInstance().getLiftHeight())));
        addSequential(new MovePlunger(MovePlunger.DesiredState.DEPLOYED)); // Put plunger out and wait (timed command).
        // Move lift down slightly
        liftMover = new MoveLiftToPosition(0.5, () -> Lift.getInstance().getLiftHeight() + Config.SUBTRACT_LIFT_HEIGHT);
        addSequential(liftMover);
    }

    /**
     * Gets the height of the lift before the eject hatch command started;
     *
     * @return The original height of the lift, in feet.
     */
    public double getOriginalLiftHeight() {
        return liftMover.getOriginalLiftHeight();
    }
}