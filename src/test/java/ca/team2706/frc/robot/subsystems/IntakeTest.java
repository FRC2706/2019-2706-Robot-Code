package ca.team2706.frc.robot.subsystems;

import mockit.*;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.DoubleSolenoid;

import edu.wpi.first.wpilibj.AnalogInput;

import static org.junit.Assert.assertEquals;

import com.ctre.phoenix.motorcontrol.can.*;

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
        intake.inhale(0.5);

        new Verifications() {{
            intake.m_intake.set(anyDouble);
            times = 1;
        }};

        intake.stop();
        intake.raiseIntake();
        intake.inhale(0.5);

    }
}