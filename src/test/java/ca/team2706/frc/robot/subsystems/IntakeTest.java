package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IntakeTest {
    @Tested
    private Intake intake;

    @Mocked
    private WPI_TalonSRX intakeTalon;

    @Mocked
    private DoubleSolenoid intakeLiftSolenoid;

    @Mocked
    private DoubleSolenoid hatchEjectorSolenoid;

    @Mocked
    private AnalogInput irSensor;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Util.resetSubsystems();

        new Expectations() {{
            new WPI_TalonSRX(Config.INTAKE_MOTOR_ID);
            result = intakeTalon;

            new AnalogInput(Config.CARGO_IR_SENSOR_ID);
            result = irSensor;

            new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID);
            result = intakeLiftSolenoid;

            new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID);
            result = hatchEjectorSolenoid;
        }};
    }

    /**
     * Tests to ensure that exhaling on the motor works.
     */
    @Test
    public void testExhaleCargo() {
        intake.lowerIntake();
        intake.exhaleCargo(1.0);
        intake.exhaleCargo(0.5);
        intake.exhaleCargo(0.25);
        intake.exhaleCargo(0D);

        new VerificationsInOrder() {{
            intakeTalon.set(-Config.MAX_INTAKE_SPEED);
            intakeTalon.set(-Config.MAX_INTAKE_SPEED * 0.5);
            intakeTalon.set(-Config.MAX_INTAKE_SPEED * 0.25);
            intakeTalon.set(withEqual(0D, 0D));
        }};
    }

    /**
     * Test to ensure that inhaling on the motor works.
     */
    @Test
    public void testInhaleCargo() {
        intake.lowerIntake(); // Lower intake to get into cargo mode.
        intake.inhaleCargo(1.0);
        intake.inhaleCargo(0.5);
        intake.inhaleCargo(0.25);
        intake.inhaleCargo(0D);

        new VerificationsInOrder() {{
            intakeTalon.set(Config.MAX_INTAKE_SPEED);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.5);
            intakeTalon.set(Config.MAX_INTAKE_SPEED * 0.25);
            intakeTalon.set(withEqual(0D, 0D));
        }};
    }

    /**
     * Tests to ensure that detecting the cargo in the mechanism works.
     */
    @Test
    public void testIsCargoCaptured() {
        final double[] returnValues = new double[]{
                Config.CARGO_CAPTURED_IR_VOLTAGE.value() * 0.25,
                Config.CARGO_CAPTURED_IR_VOLTAGE.value() * 0.95,
                Config.CARGO_CAPTURED_IR_VOLTAGE.value() * 1.1,
                Config.CARGO_CAPTURED_IR_VOLTAGE.value() * 1.5,
        };

        new Expectations() {{
            irSensor.getVoltage();
            result = returnValues;
        }};


        intake.lowerIntake();
        assertFalse(intake.isCargoInMechanism());
        assertFalse(intake.isCargoInMechanism());
        assertTrue(intake.isCargoInMechanism());
        // Raise the intake and go into hatch mode.
        intake.raiseIntake();
        assertFalse(intake.isCargoInMechanism());
        intake.lowerIntake();
        assertTrue(intake.isCargoInMechanism());
    }

    /**
     * Ensures that the cargo motor can be stopped properly.
     */
    @Test
    public void testStopCargoMotors() {
        intake.lowerIntake(); // Lower intake to go into cargo mode.
        intake.exhaleCargo(0.5 * Config.MAX_INTAKE_SPEED);
        intake.stop();

        new VerificationsInOrder() {{
            intakeTalon.set(withNotEqual(0D));
            intakeTalon.set(withEqual(0D, 0D));
        }};
    }

    /**
     * Ensures that the hatch is ejected properly.
     */
    @Test
    public void testEjectHatch() {
        intake.raiseIntake(); // Raise to go into hatch mode.
        intake.ejectHatch();

        intake.lowerIntake(); // Lower to go into cargo
        intake.ejectHatch(); // Try to eject the hatch in cargo mode.

        new Verifications() {{
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
            times = 1;
        }};

    }

    /**
     * Makes sure that the intake plunger is retracted properly.
     */
    @Test
    public void testRetractHatchDeploymentCylinder() {
        intake.raiseIntake(); // Go into hatch mode
        intake.retractPlunger();

        // Should still work with cargo manipulation.
        intake.lowerIntake();
        intake.retractPlunger();

        new Verifications() {{
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kReverse);
            times = 2;
        }};
    }
}