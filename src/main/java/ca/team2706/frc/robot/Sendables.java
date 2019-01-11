package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class Sendables {

    private Sendables() {
        throw new IllegalStateException("Utility method cannot be initialized");
    }

    public static SendableBase newPigeonSendable(PigeonIMU pigeonIMU) {
        return new SendableBase() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.addDoubleProperty("PigeonIMU", pigeonIMU::getFusedHeading, pigeonIMU::setFusedHeading);
            }
        };
    }

    public static SendableBase newTalonEncoderSendable(TalonSRX talon) {
        return new SendableBase() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.addDoubleProperty("TalonEncoder", talon.getSensorCollection()::getQuadraturePosition,
                        (position) ->
                                talon.getSensorCollection().setQuadraturePosition((int) position, Config.CAN_SHORT));
            }
        };
    }

}
