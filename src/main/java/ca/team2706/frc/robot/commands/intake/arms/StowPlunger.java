package ca.team2706.frc.robot.commands.intake.arms;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.command.TimedCommand;

public class StowPlunger extends TimedCommand {
    private boolean wasPlungerAlreadyStowed = false;

    public StowPlunger() {
        super(Config.PLUNGER_TIMEOUT);
    }

    @Override
    protected void initialize() {
        super.initialize();
        wasPlungerAlreadyStowed = Intake.getInstance().isPlungerStowed();
    }

    @Override
    protected void execute() {
        super.execute();

        if (!wasPlungerAlreadyStowed) {
            Intake.getInstance().retractPlunger();
        }
    }

    @Override
    protected boolean isFinished() {
        // We're done already if the plunger is already stowed.
        return super.isFinished() || wasPlungerAlreadyStowed;
    }
}
