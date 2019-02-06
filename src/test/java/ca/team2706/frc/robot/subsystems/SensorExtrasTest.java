package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

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
    Subsystem subsystem;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field field1 = SensorExtras.class.getDeclaredField("currentInstance");
        field1.setAccessible(true);
        field1.set(null, null);
    }

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

    @Test
    public void allocationTest() {
        new Verifications() {{
            subsystem.addChild("Unused PWM 1", (Sendable) any);
            times = 1;
        }};
    }
}