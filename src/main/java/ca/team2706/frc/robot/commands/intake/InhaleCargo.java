package ca.team2706.frc.robot.commands.intake;


import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for inhaling cargo using the intake subsystems.
 */
public class InhaleCargo extends Command {

    /**
     * Joystick which is going to be used to determine speed.
     */
    private Joystick controller;
    /**
     * Trigger axis id to be looked at.
     */
    private final int triggerAxis;

    /**
     * Constructs a new InhaleCargo command on the given controller and with the given axis.
     *
     * @param controller The controller to be monitored.
     * @param axis       The axis of the analog stick to be monitored.
     */
    public InhaleCargo(final Joystick controller, final int axis) {
        requires(Intake.getInstance());
        this.controller = controller;
        this.triggerAxis = axis;
    }

    @Override
    public void initialize() {
        Intake.getInstance().retractPlunger();
        Intake.getInstance().lowerIntake();
    }

    @Override
    public void execute() {
        double speed = controller.getRawAxis(triggerAxis);
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
