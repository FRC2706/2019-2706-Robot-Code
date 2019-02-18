package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PWM;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class SensorExtrasTest {

    @Tested
    private SensorExtras sensorExtras;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private PWM pwm;

    @Mocked
    private AnalogInput analogInput;

    @Mocked
    private DigitalInput dio;

    @Mocked
    private Relay relay;

    @Mocked
    private LiveWindow liveWindow;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
    }

    /**
     * Ensures that the exception for allocating the same object twice is handled
     */
    @Test
    public void handleExceptionTest() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));

        new Expectations() {{
            new PWM(1);
            result = new RuntimeException("Already Allocated");
        }};

        sensorExtras = SensorExtras.getInstance();

        assertEquals("Sensor Extras Warning for PWM 1 (Check Allocation Table):\n\tAlready Allocated",
                outContent.toString().trim());

        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Test that the first PWM gets added to LiveWindow
     */
    @Test
    public void allocationTest() {
        new Verifications() {{
            pwm.setName("SensorExtras", "Unused PWM 1");
            times = 1;
        }};
    }
}