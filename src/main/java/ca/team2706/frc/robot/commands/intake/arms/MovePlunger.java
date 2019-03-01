package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Command for moving the plunger, either retracting it or expanding it.
 * Timeout is to ensure that the command ends when the plunger is in the right position.
 */
public class MovePlunger extends TimedCommand {
    private boolean wasPlungerStowed = false;
    private final boolean stow;

    /**
      * @param stow True to retract the plunger, false to shoot it out.
     */
    public MovePlunger(final boolean stow) {
        super(Config.PLUNGER_TIMEOUT);
        this.stow = stow;
    }

    @Override
    protected void initialize() {
        super.initialize();
        wasPlungerStowed = Intake.getInstance().isPlungerStowed();
    }

    @Override
    protected void execute() {
        super.execute();

        if (stow) {
            Intake.getInstance().retractPlunger();
        } else {
            Intake.getInstance().deployPlunger();
        }
    }

    @Override
    protected boolean isFinished() {
        // We're done already if the plunger is already stowed.
        return super.isFinished() || wasPlungerStowed == stow;
    }
}
