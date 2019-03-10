package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.intake.AfterEjectConditional;
import ca.team2706.frc.robot.commands.intake.EjectConditional;
import ca.team2706.frc.robot.commands.lift.LiftPosition;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class PlaceHatchAuto extends CommandGroup {
    public PlaceHatchAuto() {
        addSequential(new ApproachHatchPlacement());
        LiftPosition position = new LiftPosition();
        addSequential(new EjectConditional(position));
        addSequential(new WaitCommand(1));
        addSequential(new AfterEjectConditional(position::getPosition));
    }
}
