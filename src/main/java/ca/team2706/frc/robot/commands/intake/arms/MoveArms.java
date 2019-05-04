package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.commands.pneumatics.PneumaticController;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.subsystems.Pneumatics;

import java.util.function.Supplier;

/**
 * Function for manipulating the intake arms pneumatics.
 */
public class MoveArms extends PneumaticController {

    /**
     * Constructs a command for moving the intake arms.
     *
     * @param desiredStateProvider The provider of the desired state of the pneumatics.
     *                             The expected return is the desired position for the arms.
     */
    public MoveArms(final Supplier<PneumaticState> desiredStateProvider) {
        super(Pneumatics.getInstance()::moveArms,
                pneumaticState -> desiredStateProvider.get(), Pneumatics.getInstance()::getArmsState,
                Config.INTAKE_ARMS_DELAY,
                Pneumatics.getInstance()::stopArmsPneumatics);
        requires(Pneumatics.getInstance());
    }
}
