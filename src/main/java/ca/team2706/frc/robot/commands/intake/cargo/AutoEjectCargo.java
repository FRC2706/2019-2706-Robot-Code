package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Command for automatically ejecting cargo from the mechanism.
 */
public class AutoEjectCargo extends CommandGroup {
    public AutoEjectCargo() {
        requires(Intake.getInstance());

        addSequential(new TimedCommand(Config.EXHALE_CARGO_WAIT_UNTIL_PLUNGER));
        addSequential(new InstantCommand(Intake.getInstance()::deployPlunger));
    }

    @Override
    protected void execute() {
        super.execute();
        Intake.getInstance().runIntakeForward(Config.AUTO_EJECT_CARGO_INTAKE_SPEED);
    }

    @Override
    protected void end() {
        super.end();
        Intake.getInstance().retractPlunger();
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        // We're not done until the cargo is gone.
        return !Intake.getInstance().isCargoInMechanism();
    }
}
