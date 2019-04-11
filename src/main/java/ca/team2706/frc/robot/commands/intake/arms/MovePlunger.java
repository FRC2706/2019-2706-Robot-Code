package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.pneumatics.PneumaticState;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

import java.util.function.Function;

/**
 * Command for moving the plunger, either retracting it or expanding it.
 * Timeout is to ensure that the command ends when the plunger is in the right position.
 */
public class MovePlunger extends TimedCommand {
    private boolean isAlreadyInPosition = false;


    private Function<PneumaticState, PneumaticState> newState;

    /**
     * @param newState Function for determining the desired new state of the plunger based on its current state.
     */
    public MovePlunger(final Function<PneumaticState, PneumaticState> newState) {
        super(Config.PLUNGER_TIMEOUT);
        requires(Pneumatics.getInstance());
        this.newState = newState;
    }

    /**
     * Constructs a new plunger mover that toggles the plunger.
     */
    public MovePlunger() {
        this(PneumaticState::getOpposite);
    }

    @Override
    protected void initialize() {
        super.initialize();
        final PneumaticState oldState = Pneumatics.getInstance().getPlungerState();
        final PneumaticState newState = this.newState.apply(oldState);

        isAlreadyInPosition = oldState == newState;
        Pneumatics.getInstance().movePlunger(newState);
    }

    @Override
    protected boolean isFinished() {
        // We're done already if the plunger is already in the desired position.
        return super.isFinished() || isAlreadyInPosition;
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().stopPlunger();
        isAlreadyInPosition = false;
    }
}
