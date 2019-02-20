package ca.team2706.frc.robot.commands.intake;


import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for inhaling cargo using the intake subsystems.
 */
public class InhaleCargo extends Command {

    private Joystick joystick;
    private final int triggerAxis;

    public InhaleCargo(final Joystick joystick, final int axis) {
        requires(Intake.getInstance());
        this.joystick = joystick;
        this.triggerAxis = axis;
    }

    @Override
    public void initialize() {
        Intake.getInstance().retractPlunger();
        Intake.getInstance().lowerIntake();
    }

    @Override
    public void execute() {
        double speed = joystick.getRawAxis(triggerAxis);
        Intake.getInstance().inhaleCargo(speed);
    }

    @Override
    public void end() {
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
