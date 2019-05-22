package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.commands.pneumatics.PneumaticController;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.subsystems.Pneumatics;

import java.util.function.Function;

/**
 * Command for moving the plunger, either retracting it or expanding it.
 * Timeout is to ensure that the command ends when the plunger is in the right position.
 */
public class MovePlunger extends PneumaticController {

    /**
     * @param newState Function for determining the desired new state of the plunger based on its current state.
     */
    public MovePlunger(final Function<PneumaticState, PneumaticState> newState) {
        super(Pneumatics.getInstance()::movePlunger, newState, Pneumatics.getInstance()::getPlungerState,
                Config.PLUNGER_TIMEOUT, Pneumatics.getInstance()::stopPlunger);
        requires(Pneumatics.getInstance());
    }

    /**
     * Constructs a move plunger command which will toggle the plunger's current position.
     */
    public MovePlunger() {
        this(PneumaticState::getOpposite);
    }
}
