package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftToPosition extends Command {

    private final double maxSpeed;
    private double position;
    private final boolean additive;

    /**
     * Constructs a move lift to position command, moving the lift to the given position.
     *
     * @param maxSpeed The maximum lift speed, from 0 to 1.
     * @param position The new lift position, in feet.
     * @param additive True to add the position to the lift's current position, false for an absolute position.
     */
    public MoveLiftToPosition(final double maxSpeed, final double position, boolean additive) {
        requires(Lift.getInstance());
        this.maxSpeed = maxSpeed;
        this.position = position;
        this.additive = additive;
    }

    /**
     * Constructs a move lift to position command, moving the lift to the given absolute position.
     *
     * @param maxSpeed The maximum lift speed, from 0 to 1.
     * @param position The absolute new position, in feeet.
     */
    public MoveLiftToPosition(final double maxSpeed, final double position) {
        this(maxSpeed, position, false);
    }

    @Override
    protected void execute() {
        final double absolutePosition = (additive) ? position + Lift.getInstance().getLiftHeight() : position;

        Lift.getInstance().setPosition(maxSpeed, absolutePosition);
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().hasReachedPosition(this.position);
    }

    @Override
    protected void end() {
        Lift.getInstance().stop();
    }
}
