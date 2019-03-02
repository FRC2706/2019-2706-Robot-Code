package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.commands.mirrorable.MirroredCommand;
import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class MotionProfile2Wheel extends MirroredCommand {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed;
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
     * Creates a straight drive command with constant values
     *
     * @param speed The maximum speed of the robot
     */
    public MotionProfile2Wheel(double speed, int minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
        this(() -> speed, () -> minDoneCycles, posLeft, velLeft, posRight, velRight, heading, time, size);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed The maximum speed of the robot
     */
    public MotionProfile2Wheel(Supplier<Double> speed, Supplier<Integer> minDoneCycles, double[] posLeft, double[] velLeft, double[] posRight, double[] velRight, double[] heading, int[] time, int size) {
        requires(DriveBase.getInstance());
        this.speed = speed;
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
     * Motion profile for 2 wheel
     *
     * @param speed               The speed of the robot
     * @param minDoneCycles       The number of cycles to complete after finishing the motion profile
     * @param dualTalonTrajectory The trajectory
     */
    MotionProfile2Wheel(Supplier<Double> speed, Supplier<Integer> minDoneCycles, DualTalonTrajectory dualTalonTrajectory) {
        this(speed, minDoneCycles, dualTalonTrajectory.posLeft, dualTalonTrajectory.velLeft, dualTalonTrajectory.posRight, dualTalonTrajectory.velRight, dualTalonTrajectory.heading, dualTalonTrajectory.time, dualTalonTrajectory.size);
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        if (isMirrored()) {
            DriveBase.getInstance().pushMotionProfile2Wheel(speed.get() >= 0, posRight, velRight, negateDoubleArray(heading), time, size, posLeft, velLeft);
        } else {
            DriveBase.getInstance().pushMotionProfile2Wheel(speed.get() >= 0, posLeft, velLeft, heading, time, size, posRight, velRight);
        }
        DriveBase.getInstance().setMotionProfile2Wheel();

        doneCycles = 0;
    }

    /**
     * Makes all the elements in the double array negative
     *
     * @param array The array that is negated
     * @return The array
     */
    private static double[] negateDoubleArray(double[] array) {
        double[] newArray = new double[array.length];

        for (int i = 0; i < array.length; i++) {
            newArray[i] = -array[i];
        }

        return newArray;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().runMotionProfile2Wheel(Math.abs(speed.get()));
    }

    @Override
    protected boolean isFinished() {
        if (DriveBase.getInstance().isFinishedMotionProfile2Wheel()) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }

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

