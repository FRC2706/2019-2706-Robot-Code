package ca.team2706.frc.robot.commands;

import mockit.*;
import org.junit.Test;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import ca.team2706.frc.robot.subsystems.*;

public class EjectHatchTest {

    @Tested
    private EjectHatch eject;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked 
    private Intake in;

    @Mocked
    private DoubleSolenoid hatch;

    @Test
    public void testControlMode() {
        
        eject.execute();
        eject.end();

    }
}