package ca.team2706.frc.robot;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Utility class for creating custom sendables
 */
public class Sendables {

    private Sendables() {
        throw new IllegalStateException("Utility method cannot be initialized");
    }

    /**
     * Creates a sendable to get and set the gyro heading
     *
     * @param pigeonIMU The gyro to keep track of
     * @return The sendable with a get and set of the gyro heading
     */
    public static SendableBase newPigeonSendable(PigeonIMU pigeonIMU) {
        return new SendableBase() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.addDoubleProperty("PigeonIMU", pigeonIMU::getFusedHeading, pigeonIMU::setFusedHeading);
            }
        };
    }

    /**
     * Creates a sendable to get and set the encoder ticks
     *
     * @param talon The talon with an encoder attached to it
     * @return The sendable with a get and set of the encoder ticks
     */
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
