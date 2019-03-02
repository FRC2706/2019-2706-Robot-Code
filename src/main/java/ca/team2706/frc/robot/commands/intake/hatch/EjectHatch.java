package ca.team2706.frc.robot.commands.intake.hatch;

import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import ca.team2706.frc.robot.subsystems.Lift;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command for ejecting hatches from the mechanism.
 */
public class EjectHatch extends CommandGroup {

    /**
     * Constructs the command to eject hatches.
     */
    public EjectHatch() {
        requires(Intake.getInstance());

        addSequential(new MovePlunger(MovePlunger.DesiredState.DEPLOYED)); // Put plunger out and wait (timed command).
        // Move lift down slightly
        addSequential(new InstantCommand(() -> Lift.getInstance().addToHeight(-Config.SUBTRACT_LIFT_HEIGHT)));
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().retractPlunger(); // Move plunger back in.
    }
}