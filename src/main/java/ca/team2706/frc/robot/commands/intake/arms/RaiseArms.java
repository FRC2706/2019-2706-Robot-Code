package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.pneumatics.PneumaticState;

/**
 * Command for raising the intake arms using pneumatics.
 */
public class RaiseArms extends MoveArms {

    /**
     * Constructs a command for raising the arm pneumatics.
     */
    public RaiseArms() {
        super(() -> PneumaticState.STOWED);
    }
}
