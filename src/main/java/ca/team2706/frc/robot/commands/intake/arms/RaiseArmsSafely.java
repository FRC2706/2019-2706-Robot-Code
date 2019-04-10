package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Command group for safely raising the arms by checking and retracting the pneumatic piston.
 */
public class RaiseArmsSafely extends CommandGroup {

    /**
     * Constructs a new command for raising the intake arms to manipulate hatches, checking if the plunger
     * has been stowed already.
     */
    public RaiseArmsSafely() {
        addSequential(new MovePlunger(pneumaticState -> PneumaticState.STOWED));
        addSequential(new RaiseArms());
    }
}
