package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.GenericHID;

public class QuadrantButton extends EButton {

    /**
     * The quadrants on the analog stick
     */
    public static final int UP = 0, RIGHT = 1, DOWN = 2, LEFT = 3;

    private final GenericHID joystick;
    private final int axis1;
    private final int axis2;
    private final int quadrant;
    private final double deadzone;
    private final boolean invertY;

    /**
     * Creates a QuadrantButton
     *
     * @param joystick The joystick the analog stick is on
     * @param axis1    The first axis of the analog stick
     * @param axis2    The second axis of the analog stick
     * @param quadrant The quadrant to activate in
     * @param deadzone The deadzone before activating
     */
    public QuadrantButton(GenericHID joystick, int axis1, int axis2, int quadrant,
                          double deadzone, boolean invertY) {
        super();

        if (quadrant < 0 || quadrant > 3) {
            throw new IllegalArgumentException("Quadrant range is 0-3");
        }

        this.joystick = joystick;
        this.axis1 = axis1;
        this.axis2 = axis2;
        this.quadrant = quadrant;
        this.deadzone = deadzone;
        this.invertY = invertY;
    }

    @Override
    public boolean get() {
        // Get the x and y values of the analog stick
        double x = joystick.getRawAxis(axis1);
        double y = (invertY ? -1 : 1) * joystick.getRawAxis(axis2);

        // Only continue if the coordinates are outside of the deadzone
        if (Math.abs(x) < deadzone && Math.abs(y) < deadzone) {
            return false;
        }

        // Rotate coordinates so the quadrant range is always in the same location
        double a, b;
        if (quadrant == UP) {
            a = x;
            b = y;
        } else if (quadrant == DOWN) {
            a = -x;
            b = -y;
        } else if (quadrant == RIGHT) {
            a = -y;
            b = x;
        } else {
            a = y;
            b = -x;
        }

        // Ensure that there are never two quadrants active at once
        // Check if the coordinates are inside the quadrant
        if (a > 0) {
            return b > Math.abs(a);
        } else {
            return b >= Math.abs(a);
        }
    }
}
