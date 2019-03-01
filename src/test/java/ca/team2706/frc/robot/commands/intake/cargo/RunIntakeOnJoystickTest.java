package ca.team2706.frc.robot.commands.intake.cargo;

import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.subsystems.Intake;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.VerificationsInOrder;
import org.junit.Before;
import org.junit.Test;
import util.Util;

public class RunIntakeOnJoystickTest {
    @Mocked
    private VictorSPX intakeTalon;

    @Mocked
    private DoubleSolenoid intakeLiftSolenoid;

    @Mocked
    private DoubleSolenoid plunger;

    @Mocked
    private AnalogInput irSensor;

    @Injectable
    private Joystick joystick;

    private final int axisPort = Config.XboxValue.getPortFromFluidConstant(Config.INTAKE_FORWARD_BINDING);

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        new Expectations() {{
            new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID);
            result = plunger;

            new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID);
            result = intakeLiftSolenoid;
        }};

        Util.resetSubsystems();
    }

    /**
     * Ensures that running the intake motors forward works properly.
     */
    @Test
    public void testInhaleCargo() {
        new Expectations() {{
            joystick.getRawAxis(axisPort);
            returns(0D, 0.1, 0.2, 0.5, 0.8, 1.0, 1.5);

            // Simulate plunger is stowed.
            plunger.get();
            result = DoubleSolenoid.Value.kReverse;

            // Simulate cargo mode.
            intakeLiftSolenoid.get();
            result = DoubleSolenoid.Value.kForward;
        }};

        RunIntakeOnJoystick runForward = new RunIntakeOnJoystick(joystick, Config.INTAKE_FORWARD_BINDING, true);
        Intake.getInstance().lowerIntake(); // Need to do this to get the intake into cargo mode.

        for (int i = 0; i < 7; i++) {
            runForward.execute();
            runForward.isFinished();
        }

        new VerificationsInOrder() {{
            intakeTalon.set(ControlMode.PercentOutput, withEqual(0D, 0D));
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.1);
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.2);
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.5);
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.8);
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 1.0);
            intakeTalon.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 1.5);
        }};
    }
}