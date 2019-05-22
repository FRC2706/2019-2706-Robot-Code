package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.pneumatics.PneumaticState;

/**
 * Command for lowering the intake arms.
 */
public class LowerArms extends MoveArms {

    /**
     * Constructs the command to lower the intake arms.
     */
    public LowerArms() {
        super(() -> PneumaticState.DEPLOYED);
    }
}
