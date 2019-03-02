package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.commands.intake.cargo.AutoEjectCargo;
import ca.team2706.frc.robot.commands.intake.hatch.EjectHatch;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

/**
 * Command for running either the eject cargo or eject hatch command,
 * depending on the state of the intake subsystem.
 */
public class EjectConditional extends ConditionalCommand {

    /**
     * Constructs the eject conditional command.
     */
    public EjectConditional() {
        super(new AutoEjectCargo(), new EjectHatch());
    }

    @Override
    protected boolean condition() {
        return Pneumatics.getInstance().getMode() == Pneumatics.IntakeMode.CARGO;
    }
}
