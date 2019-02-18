package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
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
    private final FluidConstant<String> joystickPort;

    /**
     * Constructs a FluidButton with the given GenericHID interface and action binding.
     *
     * @param genericHID    The GenericHID interface object.
     * @param actionBinding The action (such as "run motor") to which the joystick is bound.
     */
    public FluidButton(GenericHID genericHID, FluidConstant<String> actionBinding) {
        m_joystick = genericHID;
        this.joystickPort = actionBinding;
    }

    /**
     * Gets the port value currently set to the action's binding.
     *
     * @param fluidConstant The fluid constant action of which to find the port value.
     * @return The port value for the binding.
     */
    private static Config.XboxValue getPort(FluidConstant<String> fluidConstant) {
        return Config.XboxValue.getXboxValueFromNTKey(fluidConstant.value());
    }

    @Override
    public boolean get() {
        Config.XboxValue port = Config.XboxValue.getXboxValueFromNTKey(joystickPort.value());

        boolean value = false;

        switch (port.getInputType()) {
            case Axis:
                value = Math.abs(m_joystick.getRawAxis(port.getPort())) >= MIN_AXIS_ACTIVATION;
                break;
            case Button:
                value = m_joystick.getRawButton(port.getPort());
                break;
            case POV:
                value = m_joystick.getPOV(POV_NUMBER) == port.getPort();
                break;
        }

        return value;
    }
}
