package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

/**
 * Follows a motion profile using the sum of encoders
 */
public class MotionProfile extends Command {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed;
    private final Supplier<Integer> minDoneCycles;

    private final double[] pos;
    private final double[] vel;
    private final double[] heading;
    private final int[] time;
    private final int size;

    private int doneCycles;

    /**
     * Creates a motion profile using the sum of two encoders
     *
     * @param speed         The maximum speed of the robot
     * @param minDoneCycles The cycles to hold after the motion profile has ended
     * @param pos           The positions in feet at each point
     * @param vel           The velocities in feet per second at each point
     * @param heading       The heading at each point
     * @param time          The milliseconds to run each segment
     * @param size          The amount of segments in the trajectory
     */
    public MotionProfile(double speed, int minDoneCycles, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        this(() -> speed, () -> minDoneCycles, pos, vel, heading, time, size);
    }

    /**
     * Creates a motion profile using the sum of two encoders
     *
     * @param speed         The suppler to the maximum speed of the robot
     * @param minDoneCycles The supplier to the cycles to hold after the motion profile has ended
     * @param pos           The positions in feet at each point
     * @param vel           The velocities in feet per second at each point
     * @param heading       The heading at each point
     * @param time          The milliseconds to run each segment
     * @param size          The amount of segments in the trajectory
     */
    public MotionProfile(Supplier<Double> speed, Supplier<Integer> minDoneCycles, double[] pos, double[] vel, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.pos = pos;
        this.vel = vel;
        this.heading = heading;
        this.time = time;
        this.size = size;

        this.minDoneCycles = minDoneCycles;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().pushMotionProfile1Wheel(speed.get() >= 0, pos, vel, heading, time, size);

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().runMotionProfile(Math.abs(speed.get()));
    }

    @Override
    protected boolean isFinished() {
        if (DriveBase.getInstance().isFinishedMotionProfile()) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }
}

