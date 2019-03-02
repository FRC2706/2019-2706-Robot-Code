package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Lowers the intake arms in preparation for dealing with cargo
 */
public class LowerArms extends TimedCommand {

    public LowerArms() {
        super(1.0); // TODO add constant.
    }

    @Override
    protected void initialize() {
        System.out.println("Lowering intake arms."); // TODO remove
        Intake.getInstance().lowerIntake();
    }

    @Override
    protected void end() {
        super.end();
        Intake.getInstance().stopArmsPneumatics();
    }
}
