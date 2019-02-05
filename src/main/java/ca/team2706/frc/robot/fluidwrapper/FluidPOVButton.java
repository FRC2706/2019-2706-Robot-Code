package ca.team2706.frc.robot.fluidwrapper;

import ca.team2706.frc.robot.config.Config.XboxValue;
import ca.team2706.frc.robot.config.FluidConstant;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.POVButton;

/**
 * FluidPOVButton
 * <p>
 * Class in order to allow for dynamic changing of the POVButton value in NetworkTables
 */
public class FluidPOVButton extends POVButton {

    private final GenericHID joystick;
    private final FluidConstant<String> angle;
    private final int m_povNumber;

    /**
     * Creates a POV button for triggering commands.
     *
     * @param joystick  The GenericHID object that has the POV
     * @param angle     The desired angle in degrees (e.g. 90, 270)
     * @param povNumber The POV number (see {@link GenericHID#getPOV(int)})
     */
    public FluidPOVButton(GenericHID joystick, FluidConstant<String> angle, int povNumber) {

        super(joystick, getPortValue(angle), povNumber);
        this.joystick = joystick;
        this.angle = angle;
        m_povNumber = povNumber;
    }

    private static int getPortValue(FluidConstant<String> angle) {
        return XboxValue.getXboxValueFromNTKey(angle.value()).getPort();
    }

    @Override
    public boolean get() {
        return joystick.getPOV(m_povNumber) == getPortValue(angle);
    }
}