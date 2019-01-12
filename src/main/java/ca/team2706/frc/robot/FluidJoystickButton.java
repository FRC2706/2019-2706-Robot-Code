package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import edu.wpi.first.wpilibj.GenericHID;

import edu.wpi.first.wpilibj.buttons.JoystickButton;


public class FluidJoystickButton extends JoystickButton {

    private final GenericHID m_joystick;
    private final FluidConstant<String> joystickPort;

    // private final String staticName = Config.XBOX_VALUE.getConstantName(Config.testAction.value());
    
    
    public FluidJoystickButton(GenericHID genericHID, FluidConstant<String> joystickPort) {
        super(genericHID, getPortValue(joystickPort));

        m_joystick = genericHID;
        this.joystickPort = joystickPort;
    }

    private static int getPortValue(FluidConstant<String> NTValue) {
        String staticName = Config.XBOX_VALUE.getConstantName(NTValue.value());
        return Config.XBOX_VALUE.valueOf(staticName).getPort();
    }

    @Override
    public boolean get() {
        return m_joystick.getRawButton(getPortValue(joystickPort));
    }

 }