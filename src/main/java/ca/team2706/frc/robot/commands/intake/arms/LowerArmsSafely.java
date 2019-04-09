package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Lowers the arms, checking if the plunger is stowed beforehand and lowering it safely if not.
 */
public class LowerArmsSafely extends CommandGroup {

    /**
     * Constructs a new lower arms safely command.
     */
    public LowerArmsSafely() {
        addSequential(new MovePlunger(PneumaticState.STOWED)); // Stow plunger. This finished immediately if already stowed.
        addSequential(new LowerArms()); // Then lower arms.
    }
}
