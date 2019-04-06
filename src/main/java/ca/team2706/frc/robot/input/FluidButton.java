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
    private final double minAxisActivation;

    private final GenericHID m_joystick;
    private int joystickPort;
    private XboxValue.XboxInputType inputType;

    private boolean first = true;

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
        this.minAxisActivation = minActivation;

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

    @Override
    public boolean get() {
        final boolean pressed = determineIfActivated(m_joystick, joystickPort, inputType, minAxisActivation);

        /*
         * The first call happens when the button is initialized in OI.init().
         * If it is pressed, the button will try to interrupt the current command.
         * When interrupting the current command,
         * OI must be initialized to ensure that the command isn't a default command.
         * Since the initialization is not complete, the singleton is still null, and OI will be initialized again.
         * As a result, the loop will continue indefinitely and create a stack overflow
         */
        if (!first && pressed) {
            // Interrupt the current command on any button press
            Robot.interruptCurrentCommand();
        }

        first = false;

        return pressed;
    }
}
