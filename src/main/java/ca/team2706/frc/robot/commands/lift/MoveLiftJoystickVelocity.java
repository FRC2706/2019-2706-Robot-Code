package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftJoystickVelocity extends Command {

    private final Joystick controller;
    private int axisPort;

    public MoveLiftJoystickVelocity(Joystick joystick, final FluidConstant<String> portBinding) {
        requires(Lift.getInstance());
        controller = joystick;

        this.axisPort = XboxValue.getPortFromFluidConstant(portBinding);

        portBinding.addChangeListener((oldValue, newValue) -> this.axisPort = XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void execute() {
        final double percentSpeed = -controller.getRawAxis(axisPort);
        Lift.getInstance().setVelocity(Config.LIFT_MAX_SPEED.value());
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void end() {
        Lift.getInstance().stop();
    }
}
