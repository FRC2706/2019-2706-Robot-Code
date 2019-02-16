package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

public class StraightDriveGyro extends DriveBaseCloseLoop {

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed, position;

    /**
     * The minimum number of cycles for the robot to be within the target zone before the command ends
     */
    private final Supplier<Integer> minDoneCycles;

    /**
     * The acceptable range in feet for the error between the target and actual position
     */
    private static final double TARGET_RANGE = 0.3;

    /**
     * Creates a straight drive command with constant values
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public StraightDriveGyro(double speed, double position, int minDoneCycles) {
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
    public StraightDriveGyro(Supplier<Double> speed, Supplier<Double> position, Supplier<Integer> minDoneCycles) {
        super(DriveBase.getInstance(), minDoneCycles, TARGET_RANGE);
        this.speed = speed;
        this.position = position;
        this.minDoneCycles = minDoneCycles;
        requires(DriveBase.getInstance());
    }

    @Override
    public void initialize() {
        super.initialize();
        DriveBase.getInstance().setPositionGyroMode();
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setPositionGyro(speed.get(), position.get(), 0);
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}
