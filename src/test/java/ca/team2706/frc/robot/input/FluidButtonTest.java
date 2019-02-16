package ca.team2706.frc.robot.input;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import edu.wpi.first.wpilibj.GenericHID;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FluidButtonTest {

    @Tested
    private FluidButton fluidButton;

    @Injectable
    private GenericHID genericHID;

    @Injectable
    private FluidConstant<String> binding;

    /**
     * Tests that all types of controller input can be used as input
     */
    @Tested
    public void testButtonGets() {
        new Expectations() {{
            genericHID.getRawButton(Config.XboxValue.XBOX_A_BUTTON.getPort());
            returns(false, true, true);
            genericHID.getRawAxis(Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getPort());
            returns(0.5, -0.8, 0.9, -0.9, 0.8, 0.5);
            genericHID.getPOV(FluidButton.POV_NUMBER);
            returns(Config.XboxValue.XBOX_POV_UP, 0, Config.XboxValue.XBOX_POV_RIGHT);

            binding.value();
            returns(
                    Config.XboxValue.XBOX_A_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_A_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_A_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_LEFT_AXIS_BUTTON.getNTString(),
                    Config.XboxValue.XBOX_POV_UP.getNTString(),
                    Config.XboxValue.XBOX_POV_UP.getNTString(),
                    Config.XboxValue.XBOX_POV_UP.getNTString());
        }};

        assertFalse(fluidButton.get());
        assertTrue(fluidButton.get());
        assertFalse(fluidButton.get());

        assertFalse(fluidButton.get());
        assertTrue(fluidButton.get());
        assertTrue(fluidButton.get());
        assertTrue(fluidButton.get());
        assertTrue(fluidButton.get());
        assertFalse(fluidButton.get());

        assertTrue(fluidButton.get());
        assertFalse(fluidButton.get());
        assertFalse(fluidButton.get());
    }
}