package ca.team2706.frc.robot.commands.climber.actions;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.ClimberMotor;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Command for running the climber motor forward.
 */
public class RunClimberMotor extends Command {

    private final Supplier<Double> speed;

    /**
     * Runs the climber motor at the given speed.
     *
     * @param speed The speed, from -1 to 1, at which the motor should run.
     */
    public RunClimberMotor(Supplier<Double> speed) {
        requires(ClimberMotor.getInstance());
        this.speed = speed;
    }

    /**
     * Constructs a new command to run the climber motors using a boolean speed,
     * which uses the correct constant for moving the motor.
     *
     * @param forward Supplier of the speed, true for forward, false for backward.
     * @return The resultant command.
     */
    public static RunClimberMotor createWithBoolean(Supplier<Boolean> forward) {
        return new RunClimberMotor((() -> (forward.get()) ? Config.CLIMBER_FORWARD_SPEED.value() : -Config.CLIMBER_REVERSE_SPEED.value()));
    }

    @Override
    protected void initialize() {
        super.initialize();
        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Brake);
    }

    @Override
    protected void execute() {
        ClimberMotor.getInstance().runMotor(speed.get());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        super.end();
        ClimberMotor.getInstance().stopMotor();
//        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Coast); // TODO re-enable later
    }
}
