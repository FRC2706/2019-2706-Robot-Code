// Based on package from org.usfirst.frc.team3504.robot;
package ca.team2706.frc.robot.sensors;
import ca.team2706.frc.robot.logging.Log;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class LidarLitePWM extends SendableBase {
/*
 * Adjust the Calibration Offset to compensate for differences in each unit.
 * We've found this is a reasonably constant value for readings in the 25 cm to 600 cm range.
 * You can also use the offset to zero out the distance between the sensor and edge of the robot.
 */
private static final double CALIBRATION_OFFSET = 1.22;

private Counter counter;
private int printedWarningCount = 5;

/**
 * Create an object for a LIDAR-Lite attached to some digital input on the roboRIO
 * 
 * @param source The DigitalInput or DigitalSource where the LIDAR-Lite is attached (ex: new DigitalInput(9))
 */
public LidarLitePWM () {
	DigitalSource ds = new DigitalInput(0);
	counter = new Counter(ds);
    counter.setMaxPeriod(1.0);
    // Configure for measuring rising to falling pulses
	counter.setSemiPeriodMode(true);
	/*after trying out different 
	samplestoaverages we decided upon 35*/
	counter.setSamplesToAverage(35);
	counter.reset();
}

@Override
public void initSendable(SendableBuilder builder) {
	builder.addDoubleProperty("DistanceCm", this::getDistanceCm, null);
}

/**
 * Take a measurement and return the distance in cm
 * 
 * @return Distance in cm
 */
public double getDistanceCm() {
	double cm;
	/* If we haven't seen the first rising to falling pulse, then we have no measurement.
	 * This happens when there is no LIDAR-Lite plugged in, btw.
	 */
	//System.out.println("counter: " +  counter.get());
	if (counter.get() < 1) {
		if (printedWarningCount-- > 0) {
			Log.d("LidarLitePWM: waiting for distance measurement");
		}
		return 0;
	}
	/* getPeriod returns time in seconds. The hardware resolution is microseconds.
	 * The LIDAR-Lite unit sends a high signal for 10 microseconds per cm of distance.
	 */
	cm = (counter.getPeriod() * 1000000.0 / 10.0) + CALIBRATION_OFFSET;
	return cm;
}
}