package ca.team2706.frc.robot.commands.climber;

import ca.team2706.frc.robot.commands.pneumatics.PneumaticController;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.subsystems.ClimberPneumatics;

import java.util.function.Function;

/**
 * Command for moving the climber pistons, either in or out.
 */
public class MoveFrontClimberPistons extends PneumaticController {

    /**
     * Constructs the command for moving climber pistons.
     *
     * @param desiredState The desired state of the back climber pistons. Argument provided is their current state,
     *                     expected return is the desired state.
     */
    public MoveFrontClimberPistons(Function<PneumaticState, PneumaticState> desiredState) {
        super(ClimberPneumatics.getInstance()::moveFrontPiston,
                desiredState,
                ClimberPneumatics.getInstance()::getFrontState,
                Config.CLIMBER_FRONT_PISTONS_ON_TIME,
                ClimberPneumatics.getInstance()::stopPneumatics);
        requires(ClimberPneumatics.getInstance());
    }
}
