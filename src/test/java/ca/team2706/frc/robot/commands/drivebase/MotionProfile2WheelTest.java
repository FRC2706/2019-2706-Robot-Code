package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import static org.junit.Assert.*;

public class MotionProfile2WheelTest {

    @Tested
    private MotionProfile2Wheel motionProfile2Wheel;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked
    private SmartDashboard smartDashboard;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Injectable
    private SensorCollection sensorCollection;

    @Injectable
    private double[] posLeft = {1.0, 4.5, 2.4, -24};

    @Injectable
    private double[] velLeft = {0.2, 2.1, -4.3, -2.1};


    @Injectable
    private double[] posRight = {1.0, 4.5, 2.4, -24};

    @Injectable
    private double[] velRight = {0.2, 2.1, -4.3, -2.1};

    @Injectable
    private double[] heading = {42, 21, 21, -54};

    @Injectable
    private int[] time = {5, 5, 5, 5};

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};
    }

    /**
     * Tests that the command puts the drivetrain into the correct state
     *
     * @param speed         The speed to create the command with
     * @param minDoneCycles The minimum cycles to use
     * @param size          The number of trajectory points
     */
    @Test
    public void testCorrectState(@Injectable("0.0") double speed, @Injectable("1") int minDoneCycles, @Injectable("4") int size) {
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
        motionProfile2Wheel.initialize();
        assertEquals(DriveBase.DriveMode.MotionProfile2Wheel, DriveBase.getInstance().getDriveMode());
        assertTrue(DriveBase.getInstance().isBrakeMode());

        motionProfile2Wheel.end();
        assertEquals(DriveBase.DriveMode.Disabled, DriveBase.getInstance().getDriveMode());
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param minDoneCycles The min cycles to inject
     * @param size          The number of trajectory points
     */
    @Test
    public void testSetting(@Injectable("0.0") double speed, @Injectable("1") int minDoneCycles, @Injectable("4") int size) {
        motionProfile2Wheel.initialize();

        for (int i = 0; i < 3; i++) {
            motionProfile2Wheel.execute();
        }

        motionProfile2Wheel.end();

        new Verifications() {{
            talon.startMotionProfile((BufferedTrajectoryPointStream) any, 20, ControlMode.MotionProfileArc);
            times = 2;
            talon.feed();
            times = 6;
            talon.configClosedLoopPeakOutput(0, speed);
            times = 6;
            talon.configClosedLoopPeakOutput(1, speed);
            times = 6;
        }};
    }

    /**
     * Tests that the command finishes in the right conditions
     *
     * @param speed         The speed to inject
     * @param minDoneCycles The min cycles to inject
     * @param size          The number of trajectory points
     */
    @Test
    public void testFinished(@Injectable("0.0") double speed, @Injectable("3") int minDoneCycles, @Injectable("4") int size) {
        new Expectations() {{
            talon.isMotionProfileFinished();
            returns(false, false, false, false, false, true, true, true, true, true, true, true, true, true);
        }};


        Scheduler.getInstance().disable();

        motionProfile2Wheel.initialize();

        assertFalse(motionProfile2Wheel.isFinished());
        assertFalse(motionProfile2Wheel.isFinished());
        assertFalse(motionProfile2Wheel.isFinished());
        assertFalse(motionProfile2Wheel.isFinished());
        assertTrue(motionProfile2Wheel.isFinished());

        motionProfile2Wheel.end();

        motionProfile2Wheel.initialize();

        assertFalse(motionProfile2Wheel.isFinished());
        assertFalse(motionProfile2Wheel.isFinished());

        motionProfile2Wheel.end();
    }
}
