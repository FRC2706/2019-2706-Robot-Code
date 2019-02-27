package ca.team2706.frc.robot.commands.intake;


import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.input.FluidButton;
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
    private int triggerAxis;

    /**
     * Constructs a new InhaleCargo command on the given controller and with the given axis.
     *
     * @param controller The controller to be monitored.
     * @param axisBinding       The axis of the analog stick to be monitored, as a fluid constant.
     */
    public InhaleCargo(final Joystick controller, final FluidConstant<String> axisBinding) {
        requires(Intake.getInstance());
        this.controller = controller;
        this.triggerAxis = Config.XboxValue.getPortFromFluidConstant(axisBinding);

        axisBinding.addChangeListener((oldValue, newValue) -> this.triggerAxis = Config.XboxValue.getPortFromNTString(newValue));
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
