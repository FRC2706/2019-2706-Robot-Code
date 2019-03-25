package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.commands.PneumaticState;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Command for moving the plunger, either retracting it or expanding it.
 * Timeout is to ensure that the command ends when the plunger is in the right position.
 */
public class MovePlunger extends TimedCommand {
    private PneumaticState oldState;


    private PneumaticState newState;

    /**
     * @param newState True to retract the plunger, false to shoot it out.
     */
    public MovePlunger(final PneumaticState newState) {
        super(Config.PLUNGER_TIMEOUT);
        requires(Pneumatics.getInstance());
        this.newState = newState;
    }

    /**
     * Constructs a new plunger mover that toggles the plunger.
     */
    public MovePlunger() {
        this(PneumaticState.TOGGLE);
    }

    @Override
    protected void initialize() {
        super.initialize();
        oldState = (Pneumatics.getInstance().isPlungerStowed()) ? PneumaticState.STOWED : PneumaticState.DEPLOYED;

        PneumaticState goodNewState = newState;
        if (goodNewState == PneumaticState.TOGGLE) {
            if (Pneumatics.getInstance().isPlungerStowed()) {
                goodNewState = PneumaticState.DEPLOYED;
            } else {
                goodNewState = PneumaticState.STOWED;
            }
        }

        switch (goodNewState) {
            case DEPLOYED:
                Pneumatics.getInstance().deployPlunger();
                break;
            case STOWED:
                Pneumatics.getInstance().retractPlunger();
                break;
        }
    }

    @Override
    protected void end() {
        super.end();
        Pneumatics.getInstance().stopPlunger();
    }

    @Override
    protected boolean isFinished() {
        // We're done already if the plunger is already in the desired position.
        return super.isFinished() || oldState == newState;
    }
}
