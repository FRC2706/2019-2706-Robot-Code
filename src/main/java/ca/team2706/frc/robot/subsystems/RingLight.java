package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RingLight extends Subsystem {
    private static RingLight currentInstance;

    /**
     * Gets the current ring light instance.
     * @return The current instance.
     */
    public static RingLight getInstance() {
        return currentInstance;
    }

    /**
     * Initializes a new ring light instance, if needed.
     */
    public static void init() {
        if (currentInstance == null) {
            currentInstance = new RingLight();
        }
    }

    private final Relay relay;

    /**
     * Constructs a new ring light on default id
     */
    private RingLight() {
        this(new Relay(Config.RING_LIGHT_ID));
    }

    /**
     * Constructs a new ring light with the given Relay.
     * @param light The ring light's relay.
     */
    private RingLight(final Relay light) {
        this.relay = light;
    }

    /**
     * Toggles the status of the light.
     */
    public void toggleLight() {
        if (relay.get() == Relay.Value.kForward) {
            relay.set(Relay.Value.kReverse);
        } else {
            relay.set(Relay.Value.kForward);
        }
    }

    @Override
    protected void initDefaultCommand() {
    }
}
