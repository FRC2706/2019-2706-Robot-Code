package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift up and down with percent output based on controller input.
 */
public class MoveLiftOnJoystick extends Command {

    private GenericHID controller;

    private int axisPort;

    /**
     * Moves the lift down using PID.
     *
     * @param joystick    The joystick at which to look at to determine speed.
     * @param portBinding The axis port at which to look at for the speed.
     */
    public MoveLiftOnJoystick(GenericHID joystick, final FluidConstant<String> portBinding) {
        requires(Lift.getInstance());
        controller = joystick;

        this.axisPort = XboxValue.getPortFromFluidConstant(portBinding);

        portBinding.addChangeListener((oldValue, newValue) -> this.axisPort = XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void execute() {
        final double percentSpeed = -controller.getRawAxis(axisPort);
        Lift.getInstance().setPercentOutput(percentSpeed);
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}