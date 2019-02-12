package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.GenericHID;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AxisButtonTest {

    @Tested
    private AxisButton axisButton;

    @Injectable
    private GenericHID genericHID;

    private static final String Positive = "0";
    private static final String Negative = "1";
    private static final String Both = "2";


    @Test
    public void testPositive(@Injectable("0") int axis, @Injectable("0.8") double minTrigger, @Injectable(Positive) int triggerType) {
        new Expectations() {{
           genericHID.getRawAxis(0);
           returns(0.3, 0.8, -0.8, -0.9, 1.0);
        }};

        assertFalse(axisButton.get());
        assertTrue(axisButton.get());
        assertFalse(axisButton.get());
        assertFalse(axisButton.get());
        assertTrue(axisButton.get());
    }

    @Test
    public void testNegative(@Injectable("0") int axis, @Injectable("0.8") double minTrigger, @Injectable(Negative) int triggerType) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(0.3, 0.8, -0.8, -0.9, 1.0);
        }};

        assertFalse(axisButton.get());
        assertFalse(axisButton.get());
        assertTrue(axisButton.get());
        assertTrue(axisButton.get());
        assertFalse(axisButton.get());
    }

    @Test
    public void testBoth(@Injectable("0") int axis, @Injectable("0.8") double minTrigger, @Injectable(Both) int triggerType) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(0.3, 0.8, -0.8, -0.9, 1.0);
        }};

        assertFalse(axisButton.get());
        assertTrue(axisButton.get());
        assertTrue(axisButton.get());
        assertTrue(axisButton.get());
        assertTrue(axisButton.get());
    }
}
