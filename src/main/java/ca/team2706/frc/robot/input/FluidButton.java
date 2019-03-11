package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.Robot;
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
    private int[] joystickPorts;
    private XboxValue.XboxInputType[] inputTypes;

    /**
     * Constructs a FluidButton with the given GenericHID interface and action binding.
     *
     * @param genericHID    The GenericHID interface object.
     * @param actionBinding The action (such as "run motor") to which the joystick is bound.
     */
    public FluidButton(GenericHID genericHID, FluidConstant<String> actionBinding) {
        this(genericHID, DEFAULT_MIN_AXIS_ACTIVATION, actionBinding);
    }

    /**
     * Constructs a fluid button with the given arguments.
     *
     * @param genericHID    The joystick on which the button is bound.
     * @param minActivation The minimum axis activation, if this is an axis.
     *                      This equates to the minimum value (between 0 and 1)
     *                      at which the axis will count as being pressed.
     * @param actionBinding The binding(s) for the button. If multiple arguments are provided, only one of the buttons
     *                      needs to be pressed to activate the button.
     */
    @SafeVarargs
    public FluidButton(GenericHID genericHID, final double minActivation, FluidConstant<String>... actionBinding) {
        if (actionBinding == null || actionBinding.length == 0) {
            throw new IllegalArgumentException("actionBinding needs to contain at least one fluid constant.");
        }

        m_joystick = genericHID;
        this.minAxisActivation = minActivation;

        joystickPorts = new int[actionBinding.length];
        inputTypes = new XboxValue.XboxInputType[actionBinding.length];

        for (int i = 0; i < actionBinding.length; i++) {
            final FluidConstant<String> fluidConstant = actionBinding[i];
            final int index = i;
            updatePortAndInputType(XboxValue.getXboxValueFromFluidConstant(fluidConstant), index);

            fluidConstant.addChangeListener((oldValue, newValue) -> {
                updatePortAndInputType(XboxValue.getXboxValueFromNTKey(newValue), index);
            });
        }
    }

    /**
     * Updates the port and input type for this button.
     *
     * @param value The XboxValue button binding.
     */
    private void updatePortAndInputType(XboxValue value, final int index) {
        this.joystickPorts[index] = value.getPort();
        this.inputTypes[index] = value.getInputType();
    }

    @Override
    public boolean get() {
        boolean pressed = false;

        // Iterate through all the buttons to check if they are pressed.
        for (int i = 0; i < inputTypes.length; i++) {
            switch (inputTypes[i]) {
                case Axis:
                    pressed = Math.abs(m_joystick.getRawAxis(joystickPorts[i])) >= minAxisActivation;
                    break;
                case Button:
                    pressed = m_joystick.getRawButton(joystickPorts[i]);
                    break;
                case POV:
                    pressed = m_joystick.getPOV(POV_NUMBER) == joystickPorts[i];
                    break;
            }

            // If one of the buttons is pressed, break the loop since we don't need to check anymore.
            if (pressed) {
                break;
            }
        }

        // Interrupt the current command on any button press
        if (pressed) {
            Robot.interruptCurrentCommand();
        }

        return pressed;
    }
}
