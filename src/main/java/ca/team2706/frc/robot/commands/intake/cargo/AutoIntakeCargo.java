package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for automatically intaking cargo using the IR sensor to stop the motors.
 */
public class AutoIntakeCargo extends Command {

    /**
     * Constructs a new AutoIntakeCargo command with default parameters.
     */
    public AutoIntakeCargo() {
        requires(Intake.getInstance());
    }

    @Override
    protected void execute() {
        Intake.getInstance().runIntakeForward(Config.AUTO_INTAKE_CARGO_SPEED);
    }

    @Override
    protected boolean isFinished() {
        return Intake.getInstance().isCargoPositionedWell();
    }

    @Override
    protected void end() {
        Intake.getInstance().stop();
    }
}
