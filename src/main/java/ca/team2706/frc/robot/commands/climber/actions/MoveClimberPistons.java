package ca.team2706.frc.robot.commands.climber.actions;

import ca.team2706.frc.robot.commands.PneumaticState;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.ClimberPneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

import java.util.function.Supplier;

/**
 * Command for moving the climber pistons, either in or out.
 */
public class MoveClimberPistons extends TimedCommand {

    private Supplier<PneumaticState> desiredState;

    private PneumaticState oldState;

    /**
     * Constructs the command for moving climber pistons.
     */
    public MoveClimberPistons(Supplier<PneumaticState> desiredState) {
        super(Config.CLIMBER_PNEUMATICS_ON_TIME);
        requires(ClimberPneumatics.getInstance());

        this.desiredState = desiredState;
    }

    @Override
    protected void initialize() {
        oldState = (ClimberPneumatics.getInstance().arePistonsExtended())
                ? PneumaticState.DEPLOYED : PneumaticState.STOWED;

        PneumaticState actualState = desiredState.get();
        if (actualState == PneumaticState.TOGGLE) {
            actualState = (oldState == PneumaticState.STOWED) ? PneumaticState.DEPLOYED : PneumaticState.STOWED;
        }

        switch (actualState) {
            case STOWED:
                ClimberPneumatics.getInstance().retractPushers();
                break;
            case DEPLOYED:
                ClimberPneumatics.getInstance().pushRobot();
                break;
        }
    }

    @Override
    protected boolean isFinished() {
        // Complete when the timed command thinks we're done or if the pistons are already where we want them.
        return super.isFinished() || desiredState.get() == oldState;
    }

    @Override
    protected void end() {
        ClimberPneumatics.getInstance().stopPneumatics();
    }
}
