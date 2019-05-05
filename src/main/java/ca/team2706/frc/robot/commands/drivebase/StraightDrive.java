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
    private final Supplier<Double> position;

    /**
     * Creates a straight drive command with constant values
     *
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public StraightDrive(double position, int minDoneCycles) {
        this(() -> position, () -> minDoneCycles);
    }

    /**
     * Creates a straight drive command with references to values
     *
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public StraightDrive(Supplier<Double> position, Supplier<Integer> minDoneCycles) {
        super(minDoneCycles, TARGET_RANGE);
        this.position = position;
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setPositionNoGyro(position.get());
    }
}
