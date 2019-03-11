package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.commands.intake.cargo.AutoEjectCargo;
import ca.team2706.frc.robot.commands.intake.hatch.EjectHatch;
import ca.team2706.frc.robot.commands.lift.LiftPosition;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

/**
 * Command for running either the eject cargo or eject hatch command,
 * depending on the state of the intake subsystem.
 */
public class EjectConditional extends ConditionalCommand {

    /**
     * Constructs the eject conditional command.
     *
     * @param elevatorPosition The elevator position object to which the current position of the elevator should be saved.
     */
    public EjectConditional(LiftPosition elevatorPosition) {
        super(new AutoEjectCargo(), new EjectHatch(elevatorPosition));
    }

    @Override
    protected boolean condition() {
        return Pneumatics.getInstance().getMode() == Pneumatics.IntakeMode.CARGO;
    }
}
