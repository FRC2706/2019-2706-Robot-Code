package ca.team2706.frc.robot.commands.intake;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.input.FluidButton;
import ca.team2706.frc.robot.subsystems.Intake;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;

public class ExhaleCargoTest {
    @Tested
    private ExhaleCargo exhaleCargo;

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

    private final int axisPort = FluidButton.getPort(Config.EXHALE_BINDING).getPort();

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        exhaleCargo = new ExhaleCargo(joystick, axisPort);
    }

    @Test
    public void testExhaleCargoWhileInCargoMode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        new Expectations() {{
            joystick.getRawAxis(axisPort);
            returns(0D, 0.1, 0.2, 0.5, 0.8, 1.0, 1.5);
        }};

        Intake.getInstance().lowerIntake(); // Put robot in cargo mode.
        for (int i = 0; i < 7; i++) {
            exhaleCargo.execute();
            assertFalse("Exhale command terminated prematurely.", exhaleCargo.isFinished());
        }

        new VerificationsInOrder() {{
            intakeTalon.set(withEqual(0D, 0D));
            intakeTalon.set(-0.1 * Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-0.2 * Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-0.5 * Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-0.8 * Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-1.0 * Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-1.5 * Config.MAX_INTAKE_SPEED);
        }};
    }
}