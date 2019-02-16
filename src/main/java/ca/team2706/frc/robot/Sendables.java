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

    /**
     * The name used for the rotation degrees property of the gyro sendable
     */
    public static final String PIGEON_NAME = "Rotation Degrees";

    /**
     * The name used for the encoder ticks property of the talon sendable
     */
    public static final String TALON_NAME = "Encoder Ticks";

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
                builder.addDoubleProperty(PIGEON_NAME, () -> getPigeonYaw(pigeonIMU), pigeonIMU::setYaw);
            }
        };
    }

    /**
     * Gets the Yaw of the PigeonIMU
     *
     * @param pigeonIMU The PigeonIMU or gyro to get the yaw from
     * @return The current yaw
     */
    public static double getPigeonYaw(PigeonIMU pigeonIMU) {
        double[] yawPitchRoll = new double[3];
        pigeonIMU.getYawPitchRoll(yawPitchRoll);
        return yawPitchRoll[0];
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
                builder.addDoubleProperty(TALON_NAME, talon.getSensorCollection()::getQuadraturePosition,
                        (position) ->
                                talon.getSensorCollection().setQuadraturePosition((int) position, Config.CAN_SHORT));
            }
        };
    }

}
