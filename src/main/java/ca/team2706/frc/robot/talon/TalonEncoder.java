package ca.team2706.frc.robot.talon;

import ca.team2706.frc.robot.config.Config;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SensorBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Class to read quadrature encoders.
 *
 * <p>
 * Quadrature encoders are devices that count shaft rotation and can sense direction. The output of
 * the Encoder class is an integer that can count either up or down, and can go negative for reverse
 * direction counting. When creating Encoders, a direction can be supplied that inverts the sense of
 * the output to make code more readable if the encoder is mounted such that forward movement
 * generates negative values. Quadrature encoders have two digital outputs, an A Channel and a B
 * Channel, that are out of phase with each other for direction sensing.
 *
 * <p>
 * All encoders will immediately start counting - reset() them if you need them to be zeroed before
 * use.
 */
public class TalonEncoder extends SensorBase implements PIDSource, Sendable {

    /**
     * The source type of the PID
     */
    private PIDSourceType m_pidSource;

    /**
     * The motor controlling the PID
     */
    private final TalonSRX controller;

    /**
     * The scaling to turn ticks into distance
     */
    private double dpp = 1;

    /**
     * Creates a new TalonEncoder from a talon
     *
     * @param controller The talon with the encoder attached
     */
    public TalonEncoder(TalonSRX controller) {
        this.controller = controller;
        this.m_pidSource = PIDSourceType.kDisplacement;
    }

    /**
     * Gets the current count. Returns the current count on the Encoder. This method compensates for
     * the decoding type.
     *
     * @return Current count from the Encoder adjusted for the 1x, 2x, or 4x scale factor.
     */
    public int get() {
        return controller.getSensorCollection().getQuadraturePosition();
    }

    /**
     * Reset the Encoder distance to zero. Resets the current count to zero on the encoder.
     */
    public void reset() {
        controller.getSensorCollection().setQuadraturePosition(0, Config.CAN_SHORT);
    }

    /**
     * Get the current rate of the encoder. Units are distance per second as scaled by the value
     * from setDistancePerPulse().
     *
     * @return The current rate of the encoder.
     */
    public double getRate() {
        // Convert to ft/s from ticks/100ms
        return controller.getSensorCollection().getQuadratureVelocity() * dpp * 10;
    }

    /**
     * Get the distance the robot has driven since the last reset as scaled by the value from
     * {@link #setDistancePerPulse(double)}.
     *
     * @return The distance driven since the last reset
     */
    public double getDistance() {
        return get() * dpp;
    }

    /**
     * Set the distance per pulse for this encoder. This sets the multiplier used to determine the
     * distance driven based on the count value from the encoder. Do not include the decoding type
     * in this scale. The library already compensates for the decoding type. Set this value based on
     * the encoder's rated Pulses per Revolution and factor in gearing reductions following the
     * encoder shaft. This distance can be in any units you like, linear or angular.
     *
     * @param distancePerPulse The scale factor that will be used to convert pulses to useful units.
     */
    public void setDistancePerPulse(double distancePerPulse) {
        dpp = distancePerPulse;
    }

    /**
     * Get the distance per pulse for this encoder.
     *
     * @return The scale factor that will be used to convert pulses to useful units.
     */
    public double getDistancePerPulse() {
        return dpp;
    }

    /**
     * Set which parameter of the encoder you are using as a process control variable. The encoder
     * class supports the rate and distance parameters.
     *
     * @param pidSource An enum to select the parameter.
     */
    public void setPIDSourceType(PIDSourceType pidSource) {
        m_pidSource = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return m_pidSource;
    }

    /**
     * Implement the PIDSource interface.
     *
     * @return The current value of the selected source parameter.
     */
    public double pidGet() {
        switch (m_pidSource) {
            case kDisplacement:
                return getDistance();
            case kRate:
                return getRate();
            default:
                return 0.0;
        }
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Encoder");

        builder.addDoubleProperty("Speed", this::getRate, null);
        builder.addDoubleProperty("Distance", this::getDistance, null);
        builder.addDoubleProperty("Distance per Tick", this::getDistancePerPulse, null);
    }
}