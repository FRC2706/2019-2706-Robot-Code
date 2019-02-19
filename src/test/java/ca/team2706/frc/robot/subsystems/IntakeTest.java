package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import org.junit.Test;

public class IntakeTest {

    @Tested
    private Intake intake;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private DoubleSolenoid intakeRaise;

    @Mocked
    private AnalogInput ir;

    @Test
    public void testControlMode() {

        intake.lowerIntake();
        intake.inhaleCargo(0.5);

        new Verifications() {{
            intake.m_intake.set(anyDouble);
            times = 1;
        }};

        intake.stop();
        intake.raiseIntake();
        intake.inhaleCargo(0.5);

    }
}