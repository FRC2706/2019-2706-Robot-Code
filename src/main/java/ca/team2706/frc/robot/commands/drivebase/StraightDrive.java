package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

/**
 * Drives in a straight line to a position
 */
public class StraightDrive extends DriveBaseCloseLoop {

    /**
     * The acceptable range in feet for the error between the target and actual position
     */
    public static final double TARGET_RANGE = 0.3;

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed, position;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public StraightDrive(double speed, double position, int minDoneCycles) {
        this(() -> speed, () -> position, () -> minDoneCycles);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public StraightDrive(Supplier<Double> speed, Supplier<Double> position, Supplier<Integer> minDoneCycles) {
        super(minDoneCycles, TARGET_RANGE);
        this.speed = speed;
        this.position = position;
    }

    @Override
    public void initialize() {
        super.initialize();
        DriveBase.getInstance().setPositionNoGyroMode();
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setPositionNoGyro(speed.get(), position.get());
    }
}
