package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

public class MoveLiftToPosition extends Command {

    private final double maxSpeed;
    private final Supplier<Double> position;
    private double startLiftHeight;

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
        startLiftHeight = Lift.getInstance().getLiftHeight();
    }

    @Override
    protected void execute() {
        Lift.getInstance().setPosition(maxSpeed, position.get());
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().hasReachedPosition(this.position.get());
    }

    @Override
    protected void end() {
        Lift.getInstance().stop();
    }

    /**
     * Gets the lift height before the movement began.
     *
     * @return The start lift height.
     */
    public double getOriginalLiftHeight() {
        return startLiftHeight;
    }
}