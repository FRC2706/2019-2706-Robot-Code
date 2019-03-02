package ca.team2706.frc.robot.commands.intake.arms;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class RaiseArmsSafely extends CommandGroup {
    public RaiseArmsSafely() {
        addSequential(new MovePlunger(MovePlunger.DesiredState.STOWED));
        addSequential(new RaiseArms());
    }
}
