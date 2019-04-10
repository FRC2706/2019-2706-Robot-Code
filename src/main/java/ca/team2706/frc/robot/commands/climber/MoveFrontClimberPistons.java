package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.ClimberPneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

import java.util.function.Function;

/**
 * Command for moving the climber pistons, either in or out.
 */
public class MoveFrontClimberPistons extends TimedCommand {

    private Function<PneumaticState, PneumaticState> desiredState;

    /**
     * True if the pneumatics are already in position, false otherwise.
     */
    private boolean isAlreadyInPosition;

    /**
     * Constructs the command for moving climber pistons.
     * @param desiredState The desired state of the back climber pistons. Argument provided is their current state,
     *                     expected return is the desired state.
     */
    public MoveFrontClimberPistons(Function<PneumaticState, PneumaticState> desiredState) {
        super(Config.CLIMBER_PNEUMATICS_ON_TIME);
        requires(ClimberPneumatics.getInstance());

        this.desiredState = desiredState;
    }

    @Override
    protected void initialize() {
        final PneumaticState oldState = ClimberPneumatics.getInstance().getBackState();
        final PneumaticState newState = desiredState.apply(oldState);

        isAlreadyInPosition = newState == oldState;

        if (!isAlreadyInPosition) {
            ClimberPneumatics.getInstance().moveFrontPiston(newState);
        }
    }

    @Override
    protected boolean isFinished() {
        // Complete when the timed command thinks we're done or if the pistons are already where we want them.
        return super.isFinished() || isAlreadyInPosition;
    }

    @Override
    protected void end() {
        ClimberPneumatics.getInstance().stopPneumatics();
        isAlreadyInPosition = false;
    }
}
