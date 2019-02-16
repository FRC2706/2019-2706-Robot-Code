package ca.team2706.frc.robot.subsystems;

import mockit.*;
import org.junit.Test;
import edu.wpi.first.wpilibj.DigitalInput;
import static org.junit.Assert.assertEquals;
import com.ctre.phoenix.motorcontrol.can.*;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class ElevatorTest {

    @Tested
    private ElevatorWithPID elevator;

    @Mocked
    private WPI_TalonSRX talon;

    @Mocked
    private DoubleSolenoid hatch;

    @Mocked
    private DigitalInput limitSwitch;

    @Test
    public void testControlMode() {

        hatch = new DoubleSolenoid(0, 1);

        assertEquals(true, (elevator.getSetpoint() == 0));

        new Verifications() {{
            elevator.addToHeightGoal();
            times = 1;
        }};

        assertEquals(true, (elevator.getSetpoint() > 0));

        new Verifications() {{
            elevator.subtractFromHeightGoal();
            times = 1;
        }};

        assertEquals(true, (elevator.getSetpoint() > 0));
    }
}