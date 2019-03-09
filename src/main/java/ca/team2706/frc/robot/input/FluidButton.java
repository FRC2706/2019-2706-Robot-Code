package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.XboxValue;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * JoystickButton set up for using a FluidConstant binding.
 */
public class FluidButton extends EButton {

    /**
     * The minimum absolute input for the raw axis to be active
     */
    public static final double MIN_AXIS_ACTIVATION = 0.8;

    /**
     * The POV of the POV button
     */
    public static final int POV_NUMBER = 0;

    private final GenericHID m_joystick;
    private int joystickPort;
    private XboxValue.XboxInputType inputType;

    /**
     * Constructs a FluidButton with the given GenericHID interface and action binding.
     *
     * @param genericHID    The GenericHID interface object.
     * @param actionBinding The action (such as "run motor") to which the joystick is bound.
     */
    public FluidButton(GenericHID genericHID, FluidConstant<String> actionBinding) {
        m_joystick = genericHID;
        updatePortAndInputType(XboxValue.getXboxValueFromFluidConstant(actionBinding));

        actionBinding.addChangeListener((oldValue, newValue) -> {
            updatePortAndInputType(XboxValue.getXboxValueFromNTKey(newValue));
        });
    }

    /**
     * Updates the port and input type for this button.
     *
     * @param value The XboxValue button binding.
     */
    private void updatePortAndInputType(XboxValue value) {
        this.joystickPort = value.getPort();
        this.inputType = value.getInputType();
    }

    /**
     * Gets the port value currently set to the action's binding.
     *
     * @param fluidConstant The fluid constant action of which to find the port value.
     * @return The port value for the binding.
     */
    private static XboxValue getPort(FluidConstant<String> fluidConstant) {
        return XboxValue.getXboxValueFromNTKey(fluidConstant.value());
    }

    @Override
    public boolean get() {
        final boolean value;

        switch (inputType) {
            case Axis:
                value = Math.abs(m_joystick.getRawAxis(joystickPort)) >= MIN_AXIS_ACTIVATION;
                break;
            case Button:
                value = m_joystick.getRawButton(joystickPort);
                break;
            case POV:
                value = m_joystick.getPOV(POV_NUMBER) == joystickPort;
                break;
            default:
                value = false;
                break;
        }

        // Interrupt the current command on any button press
        if (value) {
            Robot.interruptCurrentCommand();
        }

        return value;
    }
}
