package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.RingLight;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.util.function.Supplier;

public class MotionMagicVision extends MotionMagic{

    private static final NetworkTable table = NetworkTableInstance.getDefault().getTable("ChickenVision");
    public MotionMagicVision(double speed, double position, int minDoneCycles) {
        this(() -> speed, () -> position, () -> minDoneCycles);
    }

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

    private static double getTargetHeading() {
        if(table.getEntry("tapeDetected").getBoolean(false)) {
            return -table.getEntry("tapeYaw").getDouble(0.0);
        }
        else {
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
