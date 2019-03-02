package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * JoystickButton set up for using a FluidConstant binding.
 */
public class FluidButton extends EButton {
    public static final double DEFAULT_MIN_AXIS_ACTIVATION = 0.8;

    /**
     * The minimum absolute input for the raw axis to be active
     */
    private final double minAxisActivation;

    /**
     * The POV of the POV button
     */
    public static final int POV_NUMBER = 0;

    private final GenericHID m_joystick;
    private final FluidConstant<String> joystickPort;

    /**
     * Constructs a FluidButton with the given GenericHID interface and action binding.
     *
     * @param genericHID    The GenericHID interface object.
     * @param actionBinding The action (such as "run motor") to which the joystick is bound.
     */
    public FluidButton(GenericHID genericHID, FluidConstant<String> actionBinding) {
        this(genericHID, actionBinding, DEFAULT_MIN_AXIS_ACTIVATION);
    }

    public FluidButton(GenericHID genericHID, FluidConstant<String> actionBinding, final double minActivation) {
        m_joystick = genericHID;
        this.joystickPort = actionBinding;
        this.minAxisActivation = minActivation;

    }

    @Override
    public boolean get() {
        return determineIfPressed(m_joystick, XboxValue.getXboxValueFromFluidConstant(joystickPort), minAxisActivation);
    }

    /**
     * Determines if the given controller's port is being pressed.
     * @param controller The controller to test.
     * @param port The port of the button which is being tested.
     * @return True if the button at the given port on the controller is being pressed, false otherwise.
     */
    static boolean determineIfPressed(GenericHID controller, final XboxValue port, final double minAxisActivation) {
        final boolean pressed;

        switch (port.getInputType()) {
            case Axis:
                pressed = Math.abs(controller.getRawAxis(port.getPort())) >= minAxisActivation;
                break;
            case Button:
                pressed = controller.getRawButton(port.getPort());
                break;
            case POV:
                pressed = controller.getPOV(POV_NUMBER) == port.getPort();
                break;
            default:
                pressed = false;
                break;
        }

        return pressed;
    }

    /**
     * Determines if the given button on the given controller is being pressed.
     * @param controller The controller.
     * @param port The button binding.
     * @return True if it's being pressed, false otherwise.
     */
    static boolean determineIfPressed(GenericHID controller, final XboxValue port) {
        return determineIfPressed(controller, port, DEFAULT_MIN_AXIS_ACTIVATION);
    }
}
