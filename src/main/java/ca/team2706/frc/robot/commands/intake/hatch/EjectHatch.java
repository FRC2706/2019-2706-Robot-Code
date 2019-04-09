package ca.team2706.frc.robot.commands.intake.hatch;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.lift.LiftPosition;
import ca.team2706.frc.robot.commands.lift.MoveLiftToPosition;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command for ejecting hatches from the mechanism.
 */
public class EjectHatch extends CommandGroup {
    /**
     * Constructs the command to eject hatches.
     *
     * @param elevatorPosition The elevator position object to add the elevator's current position to.
     */
    public EjectHatch(LiftPosition elevatorPosition) {
        addSequential(new InstantCommand(() -> elevatorPosition.setPosition(Lift.getInstance().getLiftHeight())));
        // Make sure lift is high enough. Timeout after 0.75 seconds in case it's blocked.
        addSequential(new ConditionalCommand(new MoveLiftToPosition(0.7, () -> 0.05 - Config.SUBTRACT_LIFT_HEIGHT)) {
            @Override
            protected boolean condition() {
                // Make sure that we're above the minimum hatch eject height before going.
                return Lift.getInstance().getLiftHeight() < -Config.SUBTRACT_LIFT_HEIGHT + 0.05;
            }
        }, 0.75);
        addSequential(new MovePlunger(PneumaticState.DEPLOYED)); // Put plunger out and wait (timed command).
        // Move lift down slightly
        addSequential(new MoveLiftToPosition(0.5, () -> Lift.getInstance().getLiftHeight() + Config.SUBTRACT_LIFT_HEIGHT), 0.75);
    }
}