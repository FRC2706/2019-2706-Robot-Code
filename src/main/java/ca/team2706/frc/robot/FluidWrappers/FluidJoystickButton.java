package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.config.Config.XBOX_VALUE;
import edu.wpi.first.wpilibj.GenericHID;

import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * @author Justin Toft
 * Class to enable dynamic button changes in Network Tables using fluid constants
 */
public class FluidJoystickButton extends JoystickButton {

    private final GenericHID m_joystick;
    private final FluidConstant<String> joystickPort;

    
    /**
     * 
     * @param genericHID The Joystick to be used
     * @param joystickPort The FluidConstant for the button
     */
    public FluidJoystickButton(GenericHID genericHID, FluidConstant<String> joystickPort) {
        super(genericHID, getPortValue(joystickPort));

        m_joystick = genericHID;
        this.joystickPort = joystickPort;
    }

    /**
     * 
     * @param joystickPort FluidConstant for the JoystickButton
     * @return The port of the button on the controller
     */
    private static int getPortValue(FluidConstant<String> joystickPort) {
        // joystickPort.value();
        // XBOX_VALUE staticName = Config.XBOX_VALUE.getConstantName(joystickPort.value()).getPort();
        // staticName.getPort();
        return Config.XBOX_VALUE.getConstantName(joystickPort.value()).getPort();
        //return Config.XBOX_VALUE.staticName.getPort();
    }

    @Override
    public boolean get() {
        return m_joystick.getRawButton(getPortValue(joystickPort));
    }

 }