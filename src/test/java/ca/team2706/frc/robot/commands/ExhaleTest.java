package ca.team2706.frc.robot.commands;

import mockit.*;
import org.junit.Test;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.*;
import ca.team2706.frc.robot.subsystems.*;

public class ExhaleTest {

    @Tested
    private ExhaleCargo exhale;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked 
    private Intake in;

    @Injectable
    private Joystick j;

    @Mocked
    private DoubleSolenoid hatch;

    @Test
    public void testControlMode() {
        exhale = new ExhaleCargo(j);
        exhale.execute();
        exhale.end();

    }
}