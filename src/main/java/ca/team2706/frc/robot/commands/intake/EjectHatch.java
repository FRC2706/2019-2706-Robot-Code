package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class EjectHatch extends InstantCommand {

    public EjectHatch() {
        requires(Intake.getInstance());
    }

    @Override
    protected void execute() {
        if (Intake.getInstance().hatchEjectorSolenoid.get() == DoubleSolenoid.Value.kReverse){
            Intake.getInstance().ejectHatch();
        } else {
            Intake.getInstance().retractPlunger();
        }
    }
}