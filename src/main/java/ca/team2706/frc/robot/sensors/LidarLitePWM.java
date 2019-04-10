// Based on package from FRC Team 3504 (Girls of Steel Robotics)
// https://github.com/GirlsOfSteelRobotics/Docs/wiki/LIDAR-Lite-Distance-Sensor

package ca.team2706.frc.robot.sensors;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.Log;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class LidarLitePWM extends SendableBase {
	/**
	 * Counter to measure rising and falling edges of PWM signal
	 */
	private Counter counter;
	
	/**
	 * Number of attempts to wait for a count before printing warning message
	 */
    private int printedWarningCount = 5;

    /**
     * Create an object for a LIDAR-Lite attached to some digital input on the roboRIO
     */
    public LidarLitePWM () {
	    DigitalSource ds = new DigitalInput(0);
	    counter = new Counter(ds);
        counter.setMaxPeriod(1.0);
        // Configure for measuring rising to falling pulses
	    counter.setSemiPeriodMode(true);
	    // After trying out different samplestoaverages we decided upon 35
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
	    /* 
	     * If we haven't seen the first rising to falling pulse, then we have no measurement.
	     * This happens when there is no LIDAR-Lite plugged in.
	     */
	    if (counter.get() < 1) {
		    if (printedWarningCount-- > 0) {
			    Log.d("LidarLitePWM: waiting for distance measurement");
		    }
		    return 0;
	    }
	    /** 
	     * getPeriod returns time in seconds. The hardware resolution is microseconds.
	     * The LIDAR-Lite unit sends a high signal for 10 microseconds per cm of distance.
	     */
	    cm = (counter.getPeriod() * 1000000.0 / 10.0) + Config.LASER_CALIBRATION_OFFSET_CM.value();
	    return cm;
    }
}