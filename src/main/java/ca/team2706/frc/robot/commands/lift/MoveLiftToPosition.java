package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.logging.Log;
import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Command for moving the lift to a certain position, without motion magic.
 */
public class MoveLiftToPosition extends Command {

    private final double maxSpeed;
    private final Supplier<Double> position;
    private double currentTarget;

    /**
     * Constructs a move lift to position command, moving the lift to the given position.
     *
     * @param maxSpeed The maximum lift speed, from 0 to 1.
     * @param position The new lift position, in feet.
     */
    public MoveLiftToPosition(final double maxSpeed, final Supplier<Double> position) {
        requires(Lift.getInstance());
        this.maxSpeed = maxSpeed;
        this.position = position;
    }

    @Override
    protected void initialize() {
        currentTarget = position.get();
        Log.d("Moving lift to " + currentTarget + " ticks");
    }

    @Override
    protected void execute() {
        Lift.getInstance().setPosition(maxSpeed, currentTarget);
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().hasReachedPosition(currentTarget);
    }

    @Override
    protected void end() {
        Log.d("Ended lift moving. Position: " + Lift.getInstance().getLiftHeight());
        Lift.getInstance().stop();
    }

    @Override
    protected void interrupted() {
        Log.d("Cancelled moving lift after " + this.timeSinceInitialized() + " seconds");
        super.interrupted();
    }
}
