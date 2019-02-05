package ca.team2706.frc.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

/**
 * Class to test the AnalogSector on the robot
 */
public class AnalogSelectorTest {
    @Mocked
    AnalogInput analogInput;

    @Tested
    private AnalogSelector analogSelector;

    private static double getMid(AnalogSelector.Range range) {
        return ((range.min + range.max) / 2);
    }

    /**
     * Tests to ensure that the autonomous selector determines the right selected index given the voltage.
     *
     * @param channel The channel with which the
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void testGetIndex(@Injectable("0") int channel) throws NoSuchFieldException, IllegalAccessException {

        // The voltages from the AnalogSelector Class
        Field field = AnalogSelector.class.getDeclaredField("VOLTAGE_RANGES");
        field.setAccessible(true);
        AnalogSelector.Range[] ranges = (AnalogSelector.Range[]) field.get(analogSelector);

        // Sets the middle possible voltage for each index and iterates through them
        new Expectations() {{
            analogInput.getAverageVoltage();
            result = new double[]{getMid(ranges[0]), getMid(ranges[1]), getMid(ranges[2]), getMid(ranges[3]),
                    getMid(ranges[4]), getMid(ranges[5]), getMid(ranges[6]), getMid(ranges[7]), getMid(ranges[8]), getMid(ranges[9]),
                    getMid(ranges[10]), getMid(ranges[11]), getMid(ranges[12]), ranges[1].min, ranges[2].max};
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
        assertEquals(1, analogSelector.getIndex());
        assertEquals(3, analogSelector.getIndex());
    }
}
