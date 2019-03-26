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

    private Supplier<Boolean> forwardSpeed;

    /**
     * Constructs a new command to run the climber motors.
     *
     * @param forward Supplier of the speed, true for forward, false for backward.
     */
    public RunClimberMotor(Supplier<Boolean> forward) {
        requires(ClimberMotor.getInstance());
        this.forwardSpeed = forward;
    }

    @Override
    protected void initialize() {
        super.initialize();
        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Brake);
    }

    @Override
    protected void execute() {
        ClimberMotor.getInstance().runMotor((forwardSpeed.get()) ? Config.CLIMBER_FORWARD_SPEED.value() : -Config.CLIMBER_REVERSE_SPEED.value());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        super.end();
        ClimberMotor.getInstance().stopMotor();
        ClimberMotor.getInstance().setNeutralMode(NeutralMode.Coast);
    }
}
