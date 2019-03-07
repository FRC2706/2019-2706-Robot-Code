package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motion.BuffTrajPointStreamJNI;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class FluidConstantTest {
    // We'll just use a string fluid constant to test.
    @Tested
    private FluidConstant<String> constantToTest = new FluidConstant<>("Test Constant", "");

    @Injectable
    private NetworkTableEntry ntEntry;

    @Injectable
    private NetworkTable constantsTable;

    @Mocked
    private NetworkTableInstance ntInstance;

    @Mocked(stubOutClassInitialization = true)
    private DriverStation driverStation;

    // LOOK AWAY! The mocks for Robot
    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked(stubOutClassInitialization = true)
    private PigeonIMU pigeon;

    private DifferentialDrive differentialDrive;

    @Mocked(stubOutClassInitialization = true)
    private CTREJNIWrapper jni;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Mocked
    private Notifier notifier;

    @Mocked(stubOutClassInitialization = true)
    private BuffTrajPointStreamJNI jni2;

    @Mocked
    private CameraServer cameraServer;

    @Mocked
    private LiveWindow liveWindow;

    @Injectable
    private SensorCollection sensorCollection;

    @Mocked
    private DoubleSolenoid solenoid;

    @Mocked
    private DigitalInput input;

    // Whether or not tests have been initialized.
    private static boolean isInitialized = false;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException, IOException {
        if (!isInitialized) {
            isInitialized = true;

            new Expectations() {{
                ntInstance.getTable("Fluid Constants");
                result = constantsTable;
                minTimes = 0;

                constantsTable.getEntry(constantToTest.getName());
                result = ntEntry;
                minTimes = 0;

                talon.getSensorCollection();
                result = sensorCollection;
                minTimes = 0;
            }};

            new Expectations(Pathfinder.class) {{
                Pathfinder.readFromCSV((File) any);
                result = new Trajectory(0);
            }};

            Util.resetSubsystems();
            Robot robot = new Robot();
            robot.robotInit();
        }
    }

    @Test
    public void testFluidConstantsCanBeUpdatedWhileDisabled() {
        new Expectations() {{
            driverStation.isDisabled();
            result = true;
        }};

        constantToTest.setValue("Test");

        new Verifications() {{
            ntEntry.setValue("Test");
            times = 1;
        }};
    }

    @Test
    public void testFluidConstantCannotBeUpdatedWhileEnabled() {
        new Expectations() {{
            driverStation.isDisabled();
            result = false;
        }};

        constantToTest.setValue("Test");

        new Verifications() {{
            ntEntry.setValue("Test");
            times = 0;
        }};
    }

    /**
     * Ensures that when changed, fluid constants call their listener.
     */
    @Test
    public void testConstantCallsListenerWhenChanged() {
        new Expectations() {{
            driverStation.isDisabled();
            returns(true, false, false, true);
        }};

        final int[] callCount = {0};
        constantToTest.addChangeListener((oldValue, newValue) -> callCount[0]++);

        for (int i = 0; i < 4; i++) {
            constantToTest.setValue(String.valueOf(i));
        }

        assertEquals(2, callCount[0]);
    }
}