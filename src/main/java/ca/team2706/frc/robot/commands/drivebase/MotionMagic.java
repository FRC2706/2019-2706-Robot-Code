package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class MotionMagic extends DriveBaseCloseLoop {

    /**
     * The acceptable range in feet for the error between the target and actual position
     */
    public static final double TARGET_RANGE = 0.1;

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed, position, heading;

    private final Supplier<Integer> minDoneCycles;

    private int doneCycles;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     * @param heading       The heading for the robot to follow (in degrees)
     */
    public MotionMagic(double speed, double position, int minDoneCycles, double heading) {
        this(() -> speed, () -> position, () -> minDoneCycles, () -> heading);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     * @param heading       The heading for the robot to follow (in degrees)
     */
    public MotionMagic(Supplier<Double> speed, Supplier<Double> position, Supplier<Integer> minDoneCycles, Supplier<Double> heading) {
        super(minDoneCycles, TARGET_RANGE);
        this.speed = speed;
        this.position = position;
        this.minDoneCycles = minDoneCycles;
        this.heading = heading;
    }

    @Override
    public void initialize() {
        super.initialize();
        DriveBase.getInstance().setMotionMagicWithGyroMode();
        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setMotionMagicPositionGyro(speed.get(), position.get(), heading.get());
    }

    @Override
    public boolean isFinished() {
        if (Math.abs((DriveBase.getInstance().getRightDistance() - position.get())) <= TARGET_RANGE) {
            doneCycles++;
        } else {
            doneCycles = 0;
        }

        return doneCycles >= minDoneCycles.get();
    }
}
