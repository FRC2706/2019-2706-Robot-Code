package ca.team2706.frc.robot.commands.lift;

/**
 * Class for keeping track of lift positions.
 */
public class LiftPosition {
    private double position;

    /**
     * Sets the lift position.
     *
     * @param position The lift position.
     */
    public void setPosition(final double position) {
        this.position = position;
    }

    /**
     * Gets the position.
     *
     * @return The position.
     */
    public double getPosition() {
        return position;
    }
}
