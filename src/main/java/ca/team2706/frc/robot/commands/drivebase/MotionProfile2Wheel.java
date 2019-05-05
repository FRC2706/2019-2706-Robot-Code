package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.commands.mirrorable.MirroredCommand;
import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

import static ca.team2706.frc.robot.subsystems.DriveBase.negateDoubleArray;

/**
 * Follows two motion profiles for each side of the robot
 */
public class MotionProfile2Wheel extends MirroredCommand {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Boolean> direction;
    private final Supplier<Integer> minDoneCycles;

    private final double[] posLeft;
    private final double[] velLeft;
    private final double[] posRight;
    private final double[] velRight;
    private final double[] heading;
    private final int[] time;
    private final int size;

    private int doneCycles;

    /**
     * Creates a motion profile using two wheels
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles The cycles to hold after the motion profile has ended
     * @param posLeft       The left wheel positions in feet at each point
     * @param velLeft       The left wheel velocities in feet per second at each point
     * @param posRight      The right wheel positions in feet at each point
     * @param velRight      The right wheel velocities in feet per second at each point
     * @param heading       The heading at each point
     * @param time          The milliseconds to run each segment
     * @param size          The amount of segments in the trajectory
     */
    public MotionProfile2Wheel(boolean direction, int minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
        this(() -> direction, () -> minDoneCycles, posLeft, velLeft, posRight, velRight, heading, time, size);
    }

    /**
     * Creates a motion profile using two wheels
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles The supplier to the cycles to hold after the motion profile has ended
     * @param posLeft       The left wheel positions in feet at each point
     * @param velLeft       The left wheel velocities in feet per second at each point
     * @param posRight      The right wheel positions in feet at each point
     * @param velRight      The right wheel velocities in feet per second at each point
     * @param heading       The heading at each point
     * @param time          The milliseconds to run each segment
     * @param size          The amount of segments in the trajectory
     */
    public MotionProfile2Wheel(Supplier<Boolean> direction, Supplier<Integer> minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.direction = direction;
        this.posLeft = posLeft;
        this.velLeft = velLeft;
        this.posRight = posRight;
        this.velRight = velRight;
        this.heading = heading;
        this.time = time;
        this.size = size;
        this.minDoneCycles = minDoneCycles;
    }

    /**
     * Creates a motion profile using the sum of two encoders
     *
     * @param direction True to drive forwards, false to drive backwards
     * @param minDoneCycles       The cycles to hold after the motion profile has ended
     * @param dualTalonTrajectory An object with all trajectory data for both wheels
     */
    MotionProfile2Wheel(Supplier<Boolean> direction, Supplier<Integer> minDoneCycles, DualTalonTrajectory dualTalonTrajectory) {
        this(direction, minDoneCycles, dualTalonTrajectory.posLeft, dualTalonTrajectory.velLeft, dualTalonTrajectory.posRight, dualTalonTrajectory.velRight, dualTalonTrajectory.heading, dualTalonTrajectory.time, dualTalonTrajectory.size);
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        if (isMirrored()) {
            DriveBase.getInstance().pushMotionProfile2Wheel(direction.get(), posRight, velRight, negateDoubleArray(heading), time, size, posLeft, velLeft);
        } else {
            DriveBase.getInstance().pushMotionProfile2Wheel(direction.get(), posLeft, velLeft, heading, time, size, posRight, velRight);
        }
        DriveBase.getInstance().reset();

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().runMotionProfile2Wheel();
    }

    @Override
    protected boolean isFinished() {
        if (DriveBase.getInstance().isFinishedMotionProfile2Wheel()) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }

    /**
     * Class with all trajectory data for both wheels
     */
    static class DualTalonTrajectory {

        final double[] posLeft;
        final double[] velLeft;
        final double[] posRight;
        final double[] velRight;
        final double[] heading;
        final int[] time;
        final int size;

        /**
         * Applies the motion profile for 2 wheels
         *
         * @param posLeft  The position of the robot at a trajectory point for the left wheel
         * @param velLeft  The velocity of the robot at a trajectory point for the left wheel
         * @param heading  The heading of the robot at a trajectory point
         * @param time     The time for each trajectory point
         * @param size     How many trajectory points there are
         * @param posRight The position of the robot at a trajectory point for the right wheel
         * @param velRight The velocity of the robot at a trajectory point for the right wheel
         */
        DualTalonTrajectory(double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
            this.posLeft = posLeft;
            this.velLeft = velLeft;
            this.posRight = posRight;
            this.velRight = velRight;
            this.heading = heading;
            this.time = time;
            this.size = size;
        }
    }
}

