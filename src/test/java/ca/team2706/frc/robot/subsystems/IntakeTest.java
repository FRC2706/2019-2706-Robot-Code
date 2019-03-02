package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
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
    private VictorSPX intakeMotor;

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
            new VictorSPX(Config.INTAKE_MOTOR_ID);
            result = intakeMotor;

            new AnalogInput(Config.CARGO_IR_SENSOR_ID);
            result = irSensor;

            new DoubleSolenoid(Config.INTAKE_LIFT_SOLENOID_FORWARD_ID, Config.INTAKE_LIFT_SOLENOID_BACKWARD_ID);
            result = intakeLiftSolenoid;

            new DoubleSolenoid(Config.HATCH_EJECTOR_SOLENOID_FORWARD_ID, Config.HATCH_EJECTOR_SOLENOID_BACKWARD_ID);
            result = hatchEjectorSolenoid;
        }};
    }

    /**
     * Tests to ensure that exhaling on the motor works only when in cargo mode.
     */
    @Test
    public void testExhaleCargo() {
        new Expectations() {{
            // This will simulate being in cargo mode.
            intakeLiftSolenoid.get();
            result = DoubleSolenoid.Value.kForward;
        }};

        intake.runIntakeBackward(1.0);
        intake.runIntakeBackward(0.5);
        intake.runIntakeBackward(0.25);
        intake.runIntakeBackward(0D);

        new VerificationsInOrder() {{
            intakeMotor.set(ControlMode.PercentOutput, -Config.MAX_INTAKE_SPEED);
            intakeMotor.set(ControlMode.PercentOutput, -Config.MAX_INTAKE_SPEED * 0.5);
            intakeMotor.set(ControlMode.PercentOutput, -Config.MAX_INTAKE_SPEED * 0.25);
            intakeMotor.set(ControlMode.PercentOutput, withEqual(0D, 0D));
        }};
    }

    /**
     * Test to ensure that inhaling on the motor works.
     */
    @Test
    public void testInhaleCargo() {
        new Expectations() {{
            // This simulates being in cargo mode.
            intakeLiftSolenoid.get();
            result = DoubleSolenoid.Value.kForward;
        }};

        intake.lowerIntake(); // Lower intake to get into cargo mode.
        intake.runIntakeForward(1.0);
        intake.runIntakeForward(0.5);
        intake.runIntakeForward(0.25);
        intake.runIntakeForward(0D);

        new VerificationsInOrder() {{
            intakeMotor.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED);
            intakeMotor.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.5);
            intakeMotor.set(ControlMode.PercentOutput, Config.MAX_INTAKE_SPEED * 0.25);
            intakeMotor.set(ControlMode.PercentOutput, withEqual(0D, 0D));
        }};
    }

    /**
     * Tests to ensure that detecting the cargo in the mechanism works.
     */
    @Test
    public void testIsCargoCaptured() {
        new Expectations() {{
            // This will simulate being in cargo mode.
            intakeLiftSolenoid.get();
            returns(DoubleSolenoid.Value.kForward, DoubleSolenoid.Value.kForward, DoubleSolenoid.Value.kForward, DoubleSolenoid.Value.kReverse, DoubleSolenoid.Value.kForward);
        }};

        final double[] returnValues = new double[]{
                Config.CARGO_CAPTURED_IR_MIN_VOLTAGE.value() * 0.25,
                Config.CARGO_CAPTURED_IR_MIN_VOLTAGE.value() * 0.95,
                Config.CARGO_CAPTURED_IR_MIN_VOLTAGE.value() * 1.1,
                Config.CARGO_CAPTURED_IR_MIN_VOLTAGE.value() * 1.5,
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
        new Expectations() {{
            // This will simulate cargo mode.
            intakeLiftSolenoid.get();
            result = DoubleSolenoid.Value.kForward;
        }};

        intake.lowerIntake(); // Lower intake to go into cargo mode.
        intake.runIntakeBackward(0.5 * Config.MAX_INTAKE_SPEED);
        intake.stop();

        new VerificationsInOrder() {{
            intakeMotor.set(ControlMode.PercentOutput, withNotEqual(0D));
            intakeMotor.set(ControlMode.PercentOutput, withEqual(0D, 0D));
        }};
    }

    /**
     * Ensures that the pneumatic plunger is ejected as it should be in both cargo and hatch mode.
     */
    @Test
    public void testEjectHatch() {
        intake.raiseIntake(); // Raise to go into hatch mode.
        intake.deployPlunger();

        intake.lowerIntake(); // Lower to go into cargo
        intake.deployPlunger(); // Try to eject the hatch in cargo mode, should work.

        new Verifications() {{
            hatchEjectorSolenoid.set(DoubleSolenoid.Value.kForward);
            times = 2;
        }};

    }

    /**
     * Makes sure that the intake plunger is retracted properly.
     */
    @Test
    public void testRetractHatchDeploymentCylinder() {
        new Expectations() {{
            // This will ensure that we can go into cargo mode by lowering the arms.
            hatchEjectorSolenoid.get();
            result = DoubleSolenoid.Value.kReverse;
        }};

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