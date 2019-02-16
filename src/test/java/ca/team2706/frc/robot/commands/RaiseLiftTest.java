package ca.team2706.frc.robot.commands;

import mockit.*;
import org.junit.Test;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.*;
import ca.team2706.frc.robot.subsystems.*;
import static org.junit.Assert.assertEquals;

public class RaiseLiftTest {

    @Tested
    private MoveLiftUpPID raise;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked 
    private ElevatorWithPID e;

    @Injectable
    private Joystick j;

    @Mocked
    private DoubleSolenoid hatch;

    @Test
    public void testControlMode() {
        
        raise = new MoveLiftUpPID(j);
        raise.execute();
        assertEquals(true, (e.m_liftMotor.get() > 0));
        raise.end();

    }
}