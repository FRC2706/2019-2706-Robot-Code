package ca.team2706.frc.robot.commands;

import mockit.*;
import org.junit.Test;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.*;
import ca.team2706.frc.robot.subsystems.*;

public class LowerLiftTest {

    @Tested
    private MoveLiftDownPID lower;

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
        
        lower = new MoveLiftDownPID(j);
        lower.execute();
        lower.end();

    }
}