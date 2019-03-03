package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

/**
 * Command for automatically ejecting cargo from the mechanism.
 */
public class AutoEjectCargo extends CommandGroup {

    /**
     * Constructs a new auto eject cargo command.
     */
    public AutoEjectCargo() {
        requires(Intake.getInstance());

        addParallel(new RunIntakeAtSpeed(1.0));
        addSequential(new WaitCommand(Config.EXHALE_CARGO_WAIT_UNTIL_PLUNGER));
        addSequential(new MovePlunger(MovePlunger.DesiredState.DEPLOYED));
    }
}
