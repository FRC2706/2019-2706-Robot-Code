package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;

public class RunIntakeAtSpeed extends Command {
    private final double speed;

    public RunIntakeAtSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    protected void execute() {
        Intake.getInstance().runIntakeForward(speed);
    }

    @Override
    protected void end() {
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
