package ca.team2706.frc.robot.commands.drivebase;

import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

public class ArcadeDriveTest {
    @Tested
    private ArcadeDrive arcadeDrive;

    @Mocked
    private Supplier<Double> forwardVal;

    @Mocked
    private Supplier<Double> rotateVal;

    @Mocked
    private boolean squareInputs;

    @Mocked
    private boolean initBrake;

    @Before
    public void setUp() {
        arcadeDrive = new ArcadeDrive(forwardVal, rotateVal, squareInputs, initBrake) {
            @Override
            public boolean isFinished() {
                return false;
            }
        };
    }

    /**
     * Tests starting the robot in teleop and having the bling detect the period properly.
     */
    @Test
    public void testBrakeMode() {
        new Expectations() {{
            forwardVal.get();
            result = 0.0;

            rotateVal.get();
            result = 0.0;

            squareInputs;
            result = false;

        }};

    }
}
