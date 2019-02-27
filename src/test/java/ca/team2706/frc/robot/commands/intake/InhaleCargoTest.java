package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.input.FluidButton;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

public class InhaleCargoTest {
    @Tested
    private InhaleCargo inhaleCargo;

    @Mocked
    private WPI_TalonSRX intakeTalon;

    @Mocked
    private DoubleSolenoid intakeLiftSolenoid;

    @Mocked
    private DoubleSolenoid hatchEjectorSolenoid;

    @Mocked
    private AnalogInput irSensor;

    @Injectable
    private Joystick joystick;

    private final int axisPort = Config.XboxValue.getPortFromFluidConstant(Config.EXHALE_BINDING);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        inhaleCargo = new InhaleCargo(joystick, Config.EXHALE_BINDING);
    }

    /**
     * Ensures that inhaling cargo works properly.
     */
    @Test
    public void testInhaleCargo() {
        new Expectations() {{
            joystick.getRawAxis(axisPort);
            returns(0D, 0.1, 0.2, 0.5, 0.8, 1.0, 1.5);
        }};

        for (int i = 0; i < 7; i++) {
            inhaleCargo.execute();
            inhaleCargo.isFinished();
        }

        new VerificationsInOrder() {{
            intakeTalon.set(withEqual(0D, 0D));
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.1);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.2);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.5);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.8);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 1.0);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 1.5);
        }};
    }
}