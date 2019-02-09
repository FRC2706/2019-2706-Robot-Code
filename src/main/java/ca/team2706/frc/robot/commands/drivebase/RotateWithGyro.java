package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

public class RotateWithGyro extends Command {

    /**
     * How close to the target angle we should be, in degrees.
     */
    public static final double TARGET_ANGLE_RANGE = 5D;

    private final Supplier<Double> speedSupplier, angleSupplier;

    /**
     * The minimum number of cycles that the robot has to be in range of the target position
     * before the command ends.
     */
    private final Supplier<Integer> minCyclesWithinThreshold;

    /**
     * Number of cycles in which the robot has been at its target angle.
     */
    private int doneCycles;

    /**
     * Constructs a new rotate with gyro command for rotating the robot a set number of degrees.
     * @param speed The speed of the rotation.
     * @param angle The new heading to rotate to.
     * @param minDoneCycles The minimum number of cycles that the robot should be in the position it wants to be before
     *                      ending the cmmand.
     */
    public RotateWithGyro(double speed, double angle, int minDoneCycles) {
        this(() -> speed, () -> angle, () -> minDoneCycles);
    }

    /**
     * Constructs a new rotate with gyro command for rotating the robot a set number of degrees.
     * @param speed The speed supplier for the rotation.
     * @param angle The angle supplier for the amount of rotation.
     * @param minCyclesWithinThreshold The supplier for the number of cycles in which the robot should be in
     *                                 the target position.
     */
    public RotateWithGyro(Supplier<Double> speed, Supplier<Double> angle, Supplier<Integer> minCyclesWithinThreshold) {
        this.speedSupplier = speed;
        this.angleSupplier = angle;
        this.minCyclesWithinThreshold = minCyclesWithinThreshold;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setRotateMode();

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setRotation(speedSupplier.get(), angleSupplier.get());
    }

    @Override
    protected boolean isFinished() {
        if (Math.abs(DriveBase.getInstance().getRightError()) <= TARGET_ANGLE_RANGE) {
            doneCycles++;
        } else {
            doneCycles = 0;
        }

        return doneCycles >= minCyclesWithinThreshold.get();
    }

    @Override
    public void end() {
        DriveBase.getInstance().setDisabledMode();
    }
}
