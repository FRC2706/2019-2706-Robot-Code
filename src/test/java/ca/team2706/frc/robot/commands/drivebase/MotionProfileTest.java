package ca.team2706.frc.robot.commands.drivebase;

import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.IMotorController;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MotionProfileTest {

    @Tested
    private MotionProfile motionProfile;

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
    private double[] pos = {1.0, 4.5, 2.4, -24};

    @Injectable
    private double[] vel = {0.2, 2.1, -4.3, -2.1};

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
            minTimes = 0;
        }};

        new Expectations(ErrorCode.class) {{
            ErrorCode.worstOne((ErrorCode) any, (ErrorCode) any);
            result = ErrorCode.OK;
            minTimes = 0;
        }};
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param minDoneCycles The min cycles to inject
     * @param size          The number of trajectory points
     */
    @Test
    public void testSetting(@Injectable("true") boolean direction, @Injectable("1") int minDoneCycles, @Injectable("4") int size) {
        motionProfile.initialize();

        for (int i = 0; i < 3; i++) {
            motionProfile.execute();
        }

        new Verifications() {{
            talon.startMotionProfile((BufferedTrajectoryPointStream) any, 20, ControlMode.MotionProfileArc);
            times = 1;
            talon.feed();
            times = 3;
            talon.follow((IMotorController) any, FollowerType.AuxOutput1);
            times = 3;
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
    public void testFinished(@Injectable("true") boolean direction, @Injectable("3") int minDoneCycles, @Injectable("4") int size) {
        new Expectations() {{
            talon.isMotionProfileFinished();
            returns(false, false, true, true, true, true, true);
        }};


        Scheduler.getInstance().disable();

        motionProfile.initialize();

        assertFalse(motionProfile.isFinished());
        assertFalse(motionProfile.isFinished());
        assertFalse(motionProfile.isFinished());
        assertFalse(motionProfile.isFinished());
        assertTrue(motionProfile.isFinished());
    }
}
