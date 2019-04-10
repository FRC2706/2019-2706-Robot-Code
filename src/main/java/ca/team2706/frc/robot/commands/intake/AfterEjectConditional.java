package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.commands.intake.arms.MovePlunger;
import ca.team2706.frc.robot.commands.intake.hatch.AfterEjectHatch;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

import java.util.function.Supplier;

/**
 * Conditional command for running certain actions after either ejecting a hatch or cargo.
 */
public class AfterEjectConditional extends ConditionalCommand {

    public AfterEjectConditional(Supplier<Double> elevatorPosition) {
        super(new MovePlunger(pneumaticState -> PneumaticState.STOWED), new AfterEjectHatch(elevatorPosition));
    }

    @Override
    protected boolean condition() {
        return Pneumatics.getInstance().getMode() == Pneumatics.IntakeMode.CARGO;
    }
}
