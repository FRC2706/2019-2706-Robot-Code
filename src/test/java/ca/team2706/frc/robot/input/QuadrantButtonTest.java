package ca.team2706.frc.robot.input;

import edu.wpi.first.wpilibj.GenericHID;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class QuadrantButtonTest {

    @Tested
    private QuadrantButton quadrantButton;

    @Injectable
    private GenericHID genericHID;

    private static final String UP = "0";
    private static final String RIGHT = "1";
    private static final String DOWN = "2";
    private static final String LEFT = "3";

    @Test
    public void testUp(@Injectable("0") int axis1, @Injectable("1") int axis2, @Injectable(UP) int quadrant, @Injectable("0.8") double deadzone, @Injectable("false") boolean invertY) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 0.0);
            genericHID.getRawAxis(1);
            returns(0.79,0.80, 1.0, 1.0, 0.0, 0.0, -1.0);
        }};

        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
    }

    @Test
    public void testInverted(@Injectable("0") int axis1, @Injectable("1") int axis2, @Injectable(UP) int quadrant, @Injectable("0.8") double deadzone, @Injectable("true") boolean invertY) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 0.0);
            genericHID.getRawAxis(1);
            returns(-0.79,-0.80, -1.0, -1.0, 0.0, 0.0, 1.0);
        }};

        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
    }

    @Test
    public void testDown(@Injectable("0") int axis1, @Injectable("1") int axis2, @Injectable(DOWN) int quadrant, @Injectable("0.8") double deadzone, @Injectable("false") boolean invertY) {
        new Expectations() {{
           genericHID.getRawAxis(0);
           returns(0.0, 0.0, -1.0, 1.0, 1.0, 1.0, 0.0);
           genericHID.getRawAxis(1);
           returns(-0.79,-0.80, -1.0, -1.0, 0.0, 0.0, 1.0);
        }};

        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
    }

    @Test
    public void testRight(@Injectable("0") int axis1, @Injectable("1") int axis2, @Injectable(RIGHT) int quadrant, @Injectable("0.8") double deadzone, @Injectable("false") boolean invertY) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(0.79, 0.80, 1.0, 1.0, 0.0, 0.0, -1.0);
            genericHID.getRawAxis(1);
            returns(0.0,0.0, -1.0, 1.0, 1.0, -1.0, 0.0);
        }};

        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
    }

    @Test
    public void testLeft(@Injectable("0") int axis1, @Injectable("1") int axis2, @Injectable(LEFT) int quadrant, @Injectable("0.8") double deadzone, @Injectable("false") boolean invertY) {
        new Expectations() {{
            genericHID.getRawAxis(0);
            returns(-0.79, -0.80, -1.0, -1.0, 0.0, 0.0, 1.0);
            genericHID.getRawAxis(1);
            returns(0.0,0.0, 1.0, -1.0, -1.0, 1.0, 0.0);
        }};

        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertTrue(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
        assertFalse(quadrantButton.get());
    }



}