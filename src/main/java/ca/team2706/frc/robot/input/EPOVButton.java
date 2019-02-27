package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.GenericHID;

/**
 * A {@link EButton} that gets its state from a POV on a {@link GenericHID}.
 */
public class EPOVButton extends EButton {
    private final GenericHID m_joystick;
    private final int m_angle;
    private final int m_povNumber;

    /**
     * Creates a POV button for triggering commands.
     *
     * @param joystick  The GenericHID object that has the POV
     * @param angle     The desired angle in degrees (e.g. 90, 270)
     * @param povNumber The POV number (see {@link GenericHID#getPOV(int)})
     */
    public EPOVButton(GenericHID joystick, int angle, int povNumber) {
        m_joystick = joystick;
        m_angle = angle;
        m_povNumber = povNumber;
    }

    /**
     * Creates a POV button for triggering commands.
     * By default, acts on POV 0
     *
     * @param joystick The GenericHID object that has the POV
     * @param angle    The desired angle (e.g. 90, 270)
     */
    public EPOVButton(GenericHID joystick, int angle) {
        this(joystick, angle, 0);
    }

    /**
     * Checks whether the current value of the POV is the target angle.
     *
     * @return Whether the value of the POV matches the target angle
     */
    @Override
    public boolean get() {
        return m_joystick.getPOV(m_povNumber) == m_angle;
    }
}
