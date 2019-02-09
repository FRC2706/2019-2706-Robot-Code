package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import com.ctre.phoenix.CTREJNIWrapper;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import mockit.*;
import org.junit.Before;
import org.junit.Test;

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
    private CameraServer cameraServer;

    @Mocked
    private LiveWindow liveWindow;

    @Injectable
    private SensorCollection sensorCollection;

    // Whether or not tests have been initialized.
    private static boolean isInitialized = false;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
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
}