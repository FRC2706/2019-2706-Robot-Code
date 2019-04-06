package ca.team2706.frc.robot.commands.climber.actions;

import ca.team2706.frc.robot.subsystems.ClimberMotor;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Command for running the climber motor forward.
 */
public class RunClimberMotorVelocity extends Command {

    private final Supplier<Double> speed;

    /**
     * Runs the climber motor at the given speed.
     *
     * @param speed The speed, from -1 to 1, at which the motor should run.
     */
    public RunClimberMotorVelocity(Supplier<Double> speed) {
        requires(ClimberMotor.getInstance());
        this.speed = speed;
    }

    @Override
    protected void initialize() {
        super.initialize();
        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Brake);
        ClimberMotor.getInstance().startMotionProfile();
    }

    @Override
    protected void execute() {
        ClimberMotor.getInstance().motionProfilePeriodic(speed.get());
    }

    @Override
    protected boolean isFinished() {
        return ClimberMotor.getInstance().motionProfileDone();
    }

    @Override
    protected void end() {
        super.end();
        ClimberMotor.getInstance().stopMotor();
        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Coast);
    }
}
