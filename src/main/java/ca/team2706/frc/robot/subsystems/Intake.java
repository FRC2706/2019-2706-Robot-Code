package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The subsystem which comtrols the intake for cargo and hatches
 */

public class Intake extends Subsystem {

    public WPI_TalonSRX m_intake;
    private AnalogInput m_sensor;
    private static final double BALL_CAPTURED = 1;
    private double m_intakeSpeed;
    private DoubleSolenoid m_intakeLift;
    private DoubleSolenoid m_hatchEjector;
    public boolean hatchMode = false;
    private static Intake currentInstance;

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
        m_intakeSpeed = Config.INTAKE_MAX_SPEED;
        m_intakeLift = new DoubleSolenoid(2, 3);
        m_hatchEjector = new DoubleSolenoid(0, 1);
    }

    public void initDefaultCommand() {}

    /**
     * Getting the voltage from the IR sensor
     * @return the voltage reading
     */

    public double readIr() {
        if (!hatchMode){
            return m_sensor.getVoltage();
        } else {
            return 0;
        }
    }

    /**
     * Spins the wheels to intake a cargo
     * @param speed speed at which to change the wheels
     */

    public void inhale(double speed) {
        if (!hatchMode) {
            m_intake.set(speed * m_intakeSpeed);
        }
    }

    /**
     * Spins the wheels to eject a cargo
     * @param speed speed at which to spin the wheels
     */

    public void exhale(double speed) {
        if (!hatchMode) {
            m_intake.set(speed * m_intakeSpeed);
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
     * @return whether the intake has cargo or not
     */

    public boolean ballCaptured() {
        if (!hatchMode) {
            return m_sensor.getVoltage() > BALL_CAPTURED;
        } else {
            return false;
        }
    }

    /**
     * Checks to see if there's no ball in the intake
     * @return whether there is not ball in the intake or not
     */

    public boolean noBall() {
        if (!hatchMode) {
            return m_sensor.getVoltage() <= 0;
        } else {
            return false;
        }
    }

    /**
     * Lowers the intake arms
     */

    public void lowerIntake() {
        if (!(m_hatchEjector.get() == Value.kForward)){
            m_intakeLift.set(DoubleSolenoid.Value.kForward);
            hatchMode = false;
        }
    }

    /**
     * Raises the intake arms
     */

    public void raiseIntake() {
        m_intakeLift.set(DoubleSolenoid.Value.kReverse);
        hatchMode = true;
    }

    /**
     * Extends the hatch deployment cylinder
     */

    public void ejectHatch() {
        if (hatchMode) {
            m_hatchEjector.set(DoubleSolenoid.Value.kForward);
        }
    }

    /**
     * Lowers the lift to deploy a hatch
     */

    public void lowerLiftToDeploy() {
        ElevatorWithPID.getInstance().lowertoDeployHatch();
    }

    /**
     * Retracts the hatch deployment cylinder
     */

    public void retractHatchMech() {
        m_hatchEjector.set(DoubleSolenoid.Value.kReverse);
    }

}