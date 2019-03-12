package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Command for running the intake motors at the given speed.
 */
public class RunIntakeAtSpeed extends Command {
    private final Supplier<Double> speed;

    /**
     * Constructs a new command for running the intake at a given speed for cargo.
     *
     * @param speed The intake motor speed, from 0 to 1.
     */
    public RunIntakeAtSpeed(Supplier<Double> speed) {
        this.speed = speed;
    }

    @Override
    protected void execute() {
        Intake.getInstance().runIntakeForward(speed.get());
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
