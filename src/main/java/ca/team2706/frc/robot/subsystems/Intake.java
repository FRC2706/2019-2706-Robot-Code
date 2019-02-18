package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The subsystem which comtrols the intake for cargo and hatches
 */
public class Intake extends Subsystem {
    private static Intake currentInstance;

    public WPI_TalonSRX m_intake;
    private AnalogInput m_sensor;

    private DoubleSolenoid m_intakeLift;
    private DoubleSolenoid m_hatchEjector;

    /**
     * The state of the intake subsystem.
     */
    private IntakeMode currentMode;

    public static Intake getInstance() {
        if (currentInstance == null) {
            init();
        }

        return currentInstance;
    }

    public static void init() {
        currentInstance = new Intake();
    }

    public Intake() {
        m_intake = new WPI_TalonSRX(6);
        m_sensor = new AnalogInput(3);
        m_intake.setNeutralMode(NeutralMode.Brake);
        m_intakeLift = new DoubleSolenoid(2, 3);
        m_hatchEjector = new DoubleSolenoid(0, 1);
    }

    /**
     * Different states that the intake subsystem can be in, either
     * inhaling hatches or inhaling cargo.
     */
    private enum IntakeMode {
        CARGO, HATCH
    }

    @Override
    public void initDefaultCommand() {
    }

    /**
     * Getting the voltage from the IR sensor
     *
     * @return the voltage reading
     */
    public double readIr() {
        final double irValue;

        // We only want to be reading the real ir sensor if we're supposed to be dealing with cargo.
        if (currentMode == IntakeMode.CARGO) {
            irValue = m_sensor.getVoltage();
        }
        // Otherwise we say 0 since we're dealing with hatches.
        else {
            irValue = 0;
        }

        return irValue;
    }

    /**
     * Spins the wheels to intake cargo.
     *
     * @param speed speed at which to change the wheels, in percentage.
     */
    public void inhaleCargo(double speed) {
        if (currentMode == IntakeMode.CARGO) {
            m_intake.set(speed * Config.INTAKE_MAX_SPEED);
        }
    }

    /**
     * Spins the wheels to eject cargo.
     *
     * @param speed Speed at which to spin the wheels, in percentage.
     */
    public void exhaleCargo(double speed) {
        if (currentMode == IntakeMode.CARGO) {
            m_intake.set(-(speed * Config.INTAKE_MAX_SPEED));
        }
    }

    /**
     * Stop motors
     */
    public void stop() {
        m_intake.set(0);
    }

    /**
     * Check if the intake has a cargo within it
     *
     * @return whether the intake has cargo or not
     */
    public boolean isCargoInMechanism() {
        return currentMode == IntakeMode.CARGO && m_sensor.getVoltage() > Config.CARGO_CAPTURED_IR_DIST;
    }

    /**
     * Lowers the intake arms, in preparation for inhaling cargo.
     */
    public void lowerIntake() {
        m_intakeLift.set(DoubleSolenoid.Value.kForward);
        currentMode = IntakeMode.CARGO;
    }

    /**
     * Raises the intake arms in preparation for inhaling a hatch.
     */
    public void raiseIntake() {
        m_intakeLift.set(DoubleSolenoid.Value.kReverse);
        currentMode = IntakeMode.HATCH;
    }

    /**
     * Extends the hatch deployment cylinder
     */
    public void ejectHatch() {
        if (currentMode == IntakeMode.HATCH) {
            m_hatchEjector.set(DoubleSolenoid.Value.kForward);
        }
    }

    /**
     * Retracts the hatch deployment cylinder
     */
    public void retractHatchMech() {
        m_hatchEjector.set(DoubleSolenoid.Value.kReverse);
    }

}