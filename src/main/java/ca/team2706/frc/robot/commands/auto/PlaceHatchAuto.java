package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.intake.hatch.AfterEjectHatch;
import ca.team2706.frc.robot.commands.intake.hatch.EjectHatch;
import ca.team2706.frc.robot.commands.lift.LiftPosition;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Places a hatch
 */
public class PlaceHatchAuto extends CommandGroup {

    /**
     * Creates command to place a hatch
     */
    public PlaceHatchAuto() {
        addSequential(new ApproachHatchPlacement());
        LiftPosition position = new LiftPosition();
        addSequential(new EjectHatch(position));
        addSequential(new AfterEjectHatch(position::getPosition));
    }
}
