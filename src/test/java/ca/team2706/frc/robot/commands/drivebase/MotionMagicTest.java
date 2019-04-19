package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.*;
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

public class MotionMagicTest {

    @Tested
    private MotionMagic motionMagic;

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

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            minTimes = 0;
        }};
    }

    /**
     * Tests that the setpoint commands are called and speed is limited each tick
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     * @param heading       The heading to use
     * @param heading       The heading to use
     */
    @Test
    public void testSetting(@Injectable("0.0") double speed, @Injectable("0.5") double position, @Injectable("1") int minDoneCycles, @Injectable("0.0") double heading) {
        motionMagic.initialize();

        for (int i = 0; i < 3; i++) {
            motionMagic.execute();
        }

        new Verifications() {{
            talon.set(ControlMode.MotionMagic, position / Config.DRIVE_ENCODER_DPP, DemandType.AuxPID, 0);
            times = 3;
            talon.feed();
            times = 0;
            talon.follow((IMotorController) any, FollowerType.AuxOutput1);
            times = 3;
        }};
    }

    /**
     * Tests that the command finishes in the right conditions
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     * @param heading       The heading to use
     * @param heading       The heading to use
     */
    @Test
    public void testFinished(@Injectable("0.0") double speed, @Injectable("0") double position, @Injectable("3") int minDoneCycles, @Injectable("0.0") double heading) {
        new Expectations() {{
            sensorCollection.getQuadraturePosition();
            returns(intFeetToTicks(5), intFeetToTicks(4), intFeetToTicks(1), intFeetToTicks(0.25), intFeetToTicks(1), intFeetToTicks(0.25),
                    intFeetToTicks(-0.4), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0), intFeetToTicks(0));
        }};


        Scheduler.getInstance().disable();

        motionMagic.initialize();

        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertFalse(motionMagic.isFinished());
        assertTrue(motionMagic.isFinished());

        motionMagic.initialize();

        assertFalse(motionMagic.isFinished());
    }

    /**
     * Converts feet to encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks
     */
    private static double feetToTicks(double feet) {
        return feet / Config.DRIVE_ENCODER_DPP;
    }

    /**
     * Converts feet to integer encoder ticks
     *
     * @param feet The distance in feet
     * @return The amount of ticks as integer
     */
    private static int intFeetToTicks(double feet) {
        return (int) (feetToTicks(feet));
    }
}
