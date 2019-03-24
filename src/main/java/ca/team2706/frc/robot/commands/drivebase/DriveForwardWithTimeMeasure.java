package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.Pair;
import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DriveForwardWithTimeMeasure extends DriveForwardWithTime {

    private Map<Double, Pair<Double>> readings;

    private final Notifier notifier;
    private final boolean useRamp;

    /**
     * Creates a drive forward with time command
     *
     * @param timeout Makes it stop the command after a time in seconds
     * @param speed   The speed for the robot to drive
     */
    public DriveForwardWithTimeMeasure(double timeout, double speed, boolean useRamp) {
        super(timeout, speed);
        this.useRamp = useRamp;
        notifier = new Notifier(this::logReadings);
    }

    @Override
    public void initialize() {
        super.initialize();
        DriveBase.getInstance().enableRamp(useRamp);

        readings = Collections.synchronizedMap(new HashMap<>());
        notifier.startPeriodic(0.001);
    }

    @Override
    public void end() {
        super.end();
        DriveBase.getInstance().enableRamp(false);
        notifier.stop();

        StringBuilder b = new StringBuilder();
        for(Map.Entry<Double, Pair<Double>> entry : readings.entrySet()) {
            b.append(entry.getKey()).append(" ").append(entry.getValue().getFirst()).append(" ").append(entry.getValue().getSecond()).append("\n");
        }

        SmartDashboard.putString("command-data", b.toString());
    }

    private void logReadings() {
        readings.put(Timer.getFPGATimestamp(), DriveBase.getInstance().getVoltageVelocity());
    }
}
