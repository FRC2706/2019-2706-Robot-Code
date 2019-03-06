package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;

import java.util.function.Supplier;

/**
 * Rotates to an absolute heading using the gyro
 */
public class AbsoluteRotateWithGyro extends RotateWithGyro {

    /**
     * Gyro angle in degrees.
     */
    private double gyroAngle;

    /**
     * Constructs a new rotate with gyro command for rotating the robot to an absolute heading.
     *
     * @param speed         The speed of the rotation.
     * @param angle         The new absolute heading to rotate to, in degrees.
     * @param minDoneCycles The minimum number of cycles that the robot should be in the position it wants to be before
     *                      ending the command.
     */
    public AbsoluteRotateWithGyro(double speed, double angle, int minDoneCycles) {
        this(() -> speed, () -> angle, () -> minDoneCycles);
    }

    /**
     * Constructs a new rotate with gyro command for rotating the robot a set number of degrees.
     *
     * @param speed                    The speed supplier for the rotation.
     * @param angle                    The new absolute heading to rotate to, in degrees.
     * @param minCyclesWithinThreshold The supplier for the number of cycles in which the robot should be in
     *                                 the target position.
     */
    public AbsoluteRotateWithGyro(Supplier<Double> speed, Supplier<Double> angle, Supplier<Integer> minCyclesWithinThreshold) {
        super(speed, angle, minCyclesWithinThreshold);
    }

    @Override
    public void initialize() {
        super.initialize();
        DriveBase.getInstance().setRotateMode();

        gyroAngle = deltaAngle(getWrappedAngle(DriveBase.getInstance().getAbsoluteHeading()), mirrored ? 360 - angleSupplier.get() : angleSupplier.get());
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setRotation(speedSupplier.get(), gyroAngle);
    }

    /**
     * Gets the shortest difference between two angles [0, 360]
     *
     * @param angle1 The first angle
     * @param angle2 The second angle
     * @return The shortest angle, and makes it negative if counterclockwise
     */
    private static double deltaAngle(double angle1, double angle2) {
        double delta = angle2 - angle1;

        if (delta > 180) {
            delta -= 360;
        } else if (delta < -180) {
            delta += 360;
        }

        return delta;
    }

    /**
     * Wraps a continuous angle to one between [0, 360)
     *
     * @param unwrappedAngle The angle to unwrap
     * @return The wrapped angle
     */
    private double getWrappedAngle(double unwrappedAngle) {
        double wrappedAngle = unwrappedAngle % 360.0;
        if (wrappedAngle < 0) {
            wrappedAngle += 360;
        }

        return wrappedAngle;
    }
}
