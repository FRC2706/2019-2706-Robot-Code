package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ToggleIntake extends InstantCommand {

    public ToggleIntake() {
        requires(Intake.getInstance());
    }

    @Override
    protected void execute() {
        if (Intake.getInstance().intakeLiftSolenoid.get() == DoubleSolenoid.Value.kReverse){
            Intake.getInstance().lowerIntake();
        } else {
            Intake.getInstance().raiseIntake();
        }
    }
}