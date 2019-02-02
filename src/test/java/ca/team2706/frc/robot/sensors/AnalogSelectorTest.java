package ca.team2706.frc.robot.sensors;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import ca.team2706.frc.robot.sensors.AnalogSelector;
import edu.wpi.first.wpilibj.AnalogInput;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;

import static junit.framework.TestCase.assertEquals;

// d
/**
 * Class to test the AnalogSector on the robot
 */
public class AnalogSelectorTest {
    @Mocked(stubOutClassInitialization = true)
    AnalogInput analogInput;
    @Tested
    private AnalogSelector analogSelector;

    private static double getMid(AnalogSelector.Range range) {
        return ((range.min + range.max) / 2);
    }

    @Before
    public void setup() {
        analogSelector = new AnalogSelector(0);
    }

    @Test
    public void testGetIndex() throws NoSuchFieldException, IllegalAccessException {

        // The voltages from the AnalogSelector Class
        Field field = analogSelector.getClass().getDeclaredField("voltages");
        field.setAccessible(true);
        AnalogSelector.Range[] ranges = (AnalogSelector.Range[]) field.get(null);

        // Sets the middle possible voltage for each index and iterates through them
        new Expectations() {{
            analogInput.getAverageVoltage();
            try {
                result = new double[]{getMid(ranges[0]), getMid(ranges[1]), getMid(ranges[2]), getMid(ranges[3]),
                        getMid(ranges[4]), getMid(ranges[5]), getMid(ranges[6]), getMid(ranges[7]), getMid(ranges[8]), getMid(ranges[9]),
                        getMid(ranges[10]), getMid(ranges[11]), getMid(ranges[12])};
            } catch (NullPointerException e) {
                System.out.println(e.toString());
            }
        }};

        // Checks all the voltages against their expected outputs
        assertEquals(0, analogSelector.getIndex());
        assertEquals(1, analogSelector.getIndex());
        assertEquals(2, analogSelector.getIndex());
        assertEquals(3, analogSelector.getIndex());
        assertEquals(4, analogSelector.getIndex());
        assertEquals(5, analogSelector.getIndex());
        assertEquals(6, analogSelector.getIndex());
        assertEquals(7, analogSelector.getIndex());
        assertEquals(8, analogSelector.getIndex());
        assertEquals(9, analogSelector.getIndex());
        assertEquals(10, analogSelector.getIndex());
        assertEquals(11, analogSelector.getIndex());
        assertEquals(12, analogSelector.getIndex());
    }
}
