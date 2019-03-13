package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.RingLight;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.function.Supplier;

/**
 * Uses vision to align while driving with motion magic
 */
public class MotionMagicVision extends MotionMagic {

    private static final NetworkTable table = NetworkTableInstance.getDefault().getTable("ChickenVision");

    /**
     * Uses motion magic using vision for heading
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public MotionMagicVision(double speed, double position, int minDoneCycles) {
        this(() -> speed, () -> position, () -> minDoneCycles);
    }

    /**
     * Uses motion magic using vision for heading
     *
     * @param speed         The maximum speed of the robot
     * @param position      The position to go to in feet
     * @param minDoneCycles The minimum number of cycles for the robot to be within
     *                      the target zone before the command ends
     */
    public MotionMagicVision(Supplier<Double> speed, Supplier<Double> position, Supplier<Integer> minDoneCycles) {
        super(speed, position, minDoneCycles, MotionMagicVision::getTargetHeading);
        requires(RingLight.getInstance());
    }

    @Override
    public void initialize() {
        super.initialize();
        RingLight.getInstance().enableLight();
        table.getEntry("Driver").setBoolean(false);
    }

    /**
     * Gets the heading that the robot should point to make it point at the target
     *
     * @return The heading in degrees
     */
    private static double getTargetHeading() {
        if (table.getEntry("tapeDetected").getBoolean(false)) {
            return -table.getEntry("tapeYaw").getDouble(0.0);
        } else {
            return 0;
        }
    }

    @Override
    public void end() {
        super.end();
        RingLight.getInstance().disableLight();
        table.getEntry("Driver").setBoolean(true);
    }
}
