package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import util.Util;

public class EjectHatchTest {
    @Mocked
    private WPI_TalonSRX intakeTalon;

    @Mocked
    private DoubleSolenoid hatchEjectorSolenoid;

    @Mocked
    private AnalogInput irSensor;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();
        new Verifications() {{
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
        }};
        new Expectations() {{
            new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID);
            result = hatchEjectorSolenoid;
        }};
    }

    /**
     * Ensure that the hatch is ejected properly.
     */
    @Test
    public void testEject() {
        

        Intake.getInstance().raiseIntake(); // Go into hatch mode.
        EjectHatch hatchEject = new EjectHatch();
        hatchEject.execute();


        new Verifications() {{
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
        }};
    }
}