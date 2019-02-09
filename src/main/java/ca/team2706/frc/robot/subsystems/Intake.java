package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;


public class Intake extends Subsystem {

    private WPI_TalonSRX m_intake;
    private AnalogInput m_sensor;
    private static final double BALL_CAPTURED = 1;
    private double m_intakeSpeed;
    private DoubleSolenoid m_intakeLift;
    private DoubleSolenoid m_hatchEjector;
    public boolean hatchMode = true;
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

    public Intake(double intakeSpeed) {
        this();
        m_intakeSpeed = intakeSpeed;
    }

    public Intake() {
        m_intake = new WPI_TalonSRX(1);
        m_sensor = new AnalogInput(3);
        m_intake.setNeutralMode(NeutralMode.Brake);
        m_intakeSpeed = Config.INTAKE_MAX_SPEED;
        m_intakeLift = new DoubleSolenoid(0, 1);
        m_hatchEjector = new DoubleSolenoid(0, 1);
    }

    public void initDefaultCommand() {

    }

    public double readIr() {
        return m_sensor.getVoltage();
    }

    public void inhale(double speed) {
        if (!hatchMode) {
            m_intake.set(speed * -m_intakeSpeed);
        }
    }

    public void exhale(double speed) {
        if (!hatchMode) {
            m_intake.set(speed * -m_intakeSpeed);
        }
    }

    public void stop() {
        m_intake.set(0);
    }

    public boolean ballCaptured() {
        if (!hatchMode) {
            return m_sensor.getVoltage() > BALL_CAPTURED;
        } else {
            return false;
        }
    }

    public boolean noBall() {
        if (!hatchMode) {
            return m_sensor.getVoltage() <= 0;
        } else {
            return false;
        }
    }

    public void lowerIntake() {
        m_intakeLift.set(DoubleSolenoid.Value.kForward);
        hatchMode = false;
    }

    public void raiseIntake() {
        m_intakeLift.set(DoubleSolenoid.Value.kReverse);
        hatchMode = true;
    }

    public void ejectHatch() {
        if (hatchMode) {
            m_hatchEjector.set(DoubleSolenoid.Value.kForward);
        }
    }

    public void lowerLiftToDeploy() {
        ElevatorWithPID.getInstance().lowertoDeployHatch();
    }

    public void retractHatchMech() {
        m_hatchEjector.set(DoubleSolenoid.Value.kReverse);
    }

}