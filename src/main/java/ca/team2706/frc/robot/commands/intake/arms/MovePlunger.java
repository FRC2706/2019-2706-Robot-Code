package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Pneumatics;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Arrays;

/**
 * Command for moving the plunger, either retracting it or expanding it.
 * Timeout is to ensure that the command ends when the plunger is in the right position.
 */
public class MovePlunger extends TimedCommand {
    private DesiredState oldState;

    public enum DesiredState {
        STOWED, DEPLOYED, TOGGLE;
    }

    private DesiredState newState;

    /**
     * @param newState True to retract the plunger, false to shoot it out.
     */
    public MovePlunger(final DesiredState newState) {
        super(Config.PLUNGER_TIMEOUT);
        requires(Pneumatics.getInstance());
        this.newState = newState;
    }

    /**
     * Constructs a new plunger mover that toggles the plunger.
     */
    public MovePlunger() {
        this(DesiredState.TOGGLE);
    }

    @Override
    protected void initialize() {
        System.out.println("Init on move plunger."); // TODO remove

        super.initialize();
        oldState = (Pneumatics.getInstance().isPlungerStowed()) ? DesiredState.STOWED : DesiredState.DEPLOYED;

        DesiredState goodNewState = newState;
        if (goodNewState == DesiredState.TOGGLE) {
            if (Pneumatics.getInstance().isPlungerStowed()) {
                goodNewState = DesiredState.DEPLOYED;
            } else {
                goodNewState = DesiredState.STOWED;
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

    // TODO remove overridden method.
    @Override
    protected void interrupted() {
        super.interrupted();
        SmartDashboard.putString("Error", Arrays.toString(new Exception().getStackTrace())); // TODO Remove
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
