package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MotionMagicVisionTest {

    @Tested
    private MotionMagicVision motionMagicVision;

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

    @Mocked
    private NetworkTable networkTable;

    @Injectable
    private NetworkTableEntry driver, tapeDetected, tapeYaw;

    @Mocked
    private Relay relay;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
        }};

        new Expectations(NetworkTableEntry.class) {{
            networkTable.getEntry(withMatch("^(Driver|tapeYaw|tapeDetected)"));
            result = new Delegate<NetworkTable>() {
                public NetworkTableEntry getEntry(String entry) {
                    switch (entry) {
                        case "Driver":
                            return driver;
                        case "tapeDetected":
                            return tapeDetected;
                        case "tapeYaw":
                            return tapeYaw;
                        default:
                            return null;
                    }
                }
            };
        }};
    }

    /**
     * Tests whether network tables and the ring light are set up correctly
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testNetworkTablesSetup(@Injectable("0.0") double speed, @Injectable("0") double position, @Injectable("3") int minDoneCycles) {
        assertEquals(driver, NetworkTableInstance.getDefault().getTable("ChickenVision").getEntry("Driver"));

        motionMagicVision.initialize();
        motionMagicVision.end();

        new Verifications() {{
            driver.setBoolean(false);
            times = 1;

            driver.setBoolean(true);
            times = 1;

            relay.set(Relay.Value.kForward);
            times = 1;

            relay.set(Relay.Value.kReverse);
            times = 1;
        }};
    }

    /**
     * Tests if the auxiliary output is set correctly
     *
     * @param speed         The speed to inject
     * @param position      The position to inject
     * @param minDoneCycles The min cycles to inject
     */
    @Test
    public void testAuxSetting(@Injectable("0.0") double speed, @Injectable("0") double position, @Injectable("3") int minDoneCycles) {
        new Expectations() {{
            tapeDetected.getBoolean(false);
            returns(false, true, true, true);

            tapeYaw.getDouble(0.0);
            returns(5.0, -4.2, 0.0);
        }};

        motionMagicVision.initialize();

        motionMagicVision.execute();
        motionMagicVision.execute();
        motionMagicVision.execute();
        motionMagicVision.execute();

        motionMagicVision.end();

        new Verifications() {{
            List<Double> vals = new ArrayList<>(4);
            talon.set(ControlMode.MotionMagic, anyDouble, DemandType.AuxPID, withCapture(vals));
            assertEquals(List.of(0.0, -5.0 / Config.PIGEON_DPP, 4.2 / Config.PIGEON_DPP, -0.0), vals);
        }};
    }
}
