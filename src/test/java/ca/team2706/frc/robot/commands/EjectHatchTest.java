package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.commands.intake.EjectHatch;
import ca.team2706.frc.robot.subsystems.Intake;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

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
    }
}