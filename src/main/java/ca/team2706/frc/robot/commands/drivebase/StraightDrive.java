package ca.team2706.frc.robot.commands.drivebase;

import java.util.function.Supplier;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Drives in a straight line to a position
 */
public class StraightDrive extends Command {

    /**
     * The acceptable range in feet for the error between the target and actual position
     */
    public static final double TARGET_RANGE = 0.3;

    /**
     * References to the speed and position that the robot should be travelling at
     */
    private final Supplier<Double> speed, position;

    /**
     * The minimum number of cycles for the robot to be within the target zone before the command ends
     */
    private final Supplier<Integer> minDoneCycles;

    /**
     * The number of cycles that the robot is in the target range for
     */
    private int doneCycles = 0;

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
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.position = position;
        this.minDoneCycles = minDoneCycles;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setPositionNoGyro(speed.get(), position.get());
    }

    @Override
    public boolean isFinished() {
        if (Math.abs(DriveBase.getInstance().getRightError()) <= TARGET_RANGE) {
            doneCycles++;
        } else {
            doneCycles = 0;
        }

        return doneCycles >= minDoneCycles.get();
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}
