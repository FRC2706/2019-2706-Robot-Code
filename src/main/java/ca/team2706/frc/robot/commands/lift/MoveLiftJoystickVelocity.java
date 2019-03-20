package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift on joystick input using velocity.
 */
public class MoveLiftJoystickVelocity extends Command {

    public static final double ALPHA = 0.1;

    private final GenericHID controller;
    private int axisPort;
    private int overridePort;

    private double last;

    /**
     * Moves the lift on joystick input using velocity.
     *  @param joystick    The joystick to be looking at for percent speed.
     * @param portBinding The port binding at which to look at.
     * @param overrideLiftBinding The binding to the override button used to put the lift in override mode.
     */
    public MoveLiftJoystickVelocity(GenericHID joystick, final FluidConstant<String> portBinding, final FluidConstant<String> overrideLiftBinding) {
        requires(Lift.getInstance());
        this.controller = joystick;

        this.axisPort = XboxValue.getPortFromFluidConstant(portBinding);
        this.overridePort = XboxValue.getPortFromFluidConstant(overrideLiftBinding);

        portBinding.addChangeListener((oldValue, newValue) -> this.axisPort = XboxValue.getPortFromNTString(newValue));
        overrideLiftBinding.addChangeListener((oldValue, newValue) -> this.overridePort = XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void initialize() {
        last = 0;
    }

    @Override
    public void execute() {
        final double percentSpeed = -controller.getRawAxis(axisPort) * ALPHA + (1 - ALPHA) * last;
        if (!shouldUseOverride()) {
            Lift.getInstance().setVelocity((int) (percentSpeed * Config.LIFT_MAX_SPEED.value()));
            last = percentSpeed;
        } else if (percentSpeed < 0) {
            Lift.getInstance().overrideDown();
        } else if (percentSpeed > 0) {
            Lift.getInstance().overrideUp();
        }
    }

    /**
     * Determines if the operator is attempting to use override lift, false otherwise.
     * @return True to override, false otherwise.
     */
    private boolean shouldUseOverride() {
        return controller.getRawButton(overridePort);
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