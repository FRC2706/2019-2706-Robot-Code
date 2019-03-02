package ca.team2706.frc.robot.commands.intake.arms;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Command group for safely raising the arms by checking and retracting the pneumatic piston.
 */
public class RaiseArmsSafely extends CommandGroup {
    public RaiseArmsSafely() {
        addSequential(new MovePlunger(MovePlunger.DesiredState.STOWED));
        addSequential(new RaiseArms());
    }
}
