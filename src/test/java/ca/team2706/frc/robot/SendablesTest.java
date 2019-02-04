package ca.team2706.frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import mockit.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class SendablesTest {

    @Tested
    SendableBase sendableBase;

    @Capturing
    private WPI_TalonSRX talon;

    @Capturing
    private PigeonIMU gyro;

    @Test
    public void newPigeonSendableTest(@Mocked WPI_TalonSRX talon) {
        sendableBase = Sendables.newTalonEncoderSendable(talon);

        assertEquals("TalonEncoder", sendableBase.getName());

        SendableBuilder builder = new SendableBuilderImpl();
        sendableBase.initSendable(builder);

        List<Property>

        LiveWindow.run();

        new Verifications() {{
            sendableBase.
        }}
    }

    @Test
    public void newTalonEncoderSendableTest() {
    }
}