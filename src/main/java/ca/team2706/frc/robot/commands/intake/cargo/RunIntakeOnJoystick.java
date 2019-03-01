package ca.team2706.frc.robot.commands.intake.cargo;


import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for inhaling cargo using the intake subsystems.
 */
public class RunIntakeOnJoystick extends Command {

    /**
     * Joystick which is going to be used to determine speed.
     */
    private Joystick controller;
    /**
     * Trigger axis id to be looked at.
     */
    private int triggerAxis;

    /**
     * Which direction the motors should be moved, either true for forward or false for backward.
     */
    private final boolean forward;

    /**
     * Constructs a new RunIntakeOnJoystick command on the given controller and with the given axis.
     *
     * @param controller The controller to be monitored.
     * @param axisBinding       The axis of the analog stick to be monitored, as a fluid constant.
     * @param forward True to move the motors forward (for inhaling and ejecting cargo), false to go backward.
     */
    public RunIntakeOnJoystick(final Joystick controller, final FluidConstant<String> axisBinding, final boolean forward) {
        requires(Intake.getInstance());
        this.controller = controller;
        this.triggerAxis = XboxValue.getPortFromFluidConstant(axisBinding);
        this.forward = forward;

        axisBinding.addChangeListener((oldValue, newValue) -> this.triggerAxis = XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void execute() {
        double speed = controller.getRawAxis(triggerAxis);
        if (forward) {
            Intake.getInstance().runIntakeForward(speed);
        } else {
            Intake.getInstance().runIntakeBackward(speed);
        }
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
