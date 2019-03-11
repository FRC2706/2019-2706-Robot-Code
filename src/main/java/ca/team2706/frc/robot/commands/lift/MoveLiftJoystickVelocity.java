package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift on joystick input using velocity.
 */
public class MoveLiftJoystickVelocity extends Command {

    public static final double ALPHA = 0.1;

    private final Joystick controller;
    private int axisPort;

    private double last;

    /**
     * Moves the lift on joystick input using velocity.
     *
     * @param joystick    The joystick to be looking at for percent speed.
     * @param portBinding The port binding at which to look at.
     */
    public MoveLiftJoystickVelocity(Joystick joystick, final FluidConstant<String> portBinding) {
        requires(Lift.getInstance());
        controller = joystick;

        this.axisPort = XboxValue.getPortFromFluidConstant(portBinding);

        portBinding.addChangeListener((oldValue, newValue) -> this.axisPort = XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void initialize() {
        last = 0;
    }

    @Override
    public void execute() {
        final double percentSpeed = -controller.getRawAxis(axisPort) * ALPHA + (1 - ALPHA) * last;
        Lift.getInstance().setVelocity((int) (percentSpeed * Config.LIFT_MAX_SPEED.value()));

        last = percentSpeed;
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