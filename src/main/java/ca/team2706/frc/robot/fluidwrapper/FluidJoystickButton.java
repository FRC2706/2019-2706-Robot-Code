package ca.team2706.frc.robot.fluidwrappers;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * JoystickButton set up for using a FluidConstant binding.
 */
public class FluidJoystickButton extends JoystickButton {

    private final GenericHID m_joystick;
    private final FluidConstant<String> joystickPort;

    /**
     * Constructs a FluidJoystickButton with the given GenericHID interface and action binding.
     *
     * @param genericHID   The GenericHID interface object.
     * @param actionBindig The action (such as "run motor") to which the joystick is bound.
     */
    public FluidJoystickButton(GenericHID genericHID, FluidConstant<String> actionBindig) {
        super(genericHID, getPortValue(actionBindig));

        m_joystick = genericHID;
        this.joystickPort = actionBindig;
    }

    /**
     * Gets the port value currently set to the action's binding.
     *
     * @param fluidConstant The fluid constant action of which to find the port value.
     * @return The port value for the binding.
     */
    private static int getPortValue(FluidConstant<String> fluidConstant) {
        return Config.XboxValue.getXboxValueFromNTKey(fluidConstant.value()).getPort();
    }

    @Override
    public boolean get() {
        return m_joystick.getRawButton(getPortValue(joystickPort));
    }
}
