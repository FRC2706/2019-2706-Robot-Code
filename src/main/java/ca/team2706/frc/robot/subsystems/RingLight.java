package ca.team2706.frc.robot.subsystems;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import ca.team2706.frc.robot.SubsystemStatus;
import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.logging.PeriodicLogEntry;
import ca.team2706.frc.robot.logging.PeriodicLogPriority;
import ca.team2706.frc.robot.logging.PeriodicLoggable;
import ca.team2706.frc.robot.logging.SmartDashboardEntryType;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Set;

public class RingLight extends Subsystem implements PeriodicLoggable {
    private static RingLight currentInstance;

    /**
     * Gets the current ring light instance.
     *
     * @return The current instance.
     */
    public static RingLight getInstance() {
        init();
        return currentInstance;
    }

    /**
     * Initializes a new ring light instance, if needed.
     */
    public static SubsystemStatus init() {
        if (currentInstance == null) {
            currentInstance = new RingLight();
        }

        return SubsystemStatus.OK;
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
     *
     * @param light The ring light's relay.
     */
    private RingLight(final Relay light) {
        this.relay = light;
        addChild("Ring Light", relay);

        Robot.setOnStateChange((state) -> {
            if (state == RobotState.AUTONOMOUS || state == RobotState.TELEOP) {
                enableLight();
            }
        });
    }

    /**
     * Toggles the status of the light.
     */
    public void toggleLight() {
        if (relay.get() == Relay.Value.kForward) {
            disableLight();
        } else {
            enableLight();
        }
    }

    /**
     * Turns on the ring light.
     */
    public void enableLight() {
        relay.set(Relay.Value.kForward);
    }

    /**
     * Turns off the ring light.
     */
    public void disableLight() {
        if (!Config.DISABLE_RING_LIGHT.value()) {
            relay.set(Relay.Value.kReverse);
        }
    }

    @Override
    protected void initDefaultCommand() {
    }

    @Override
    public Set<PeriodicLogEntry> getLogs() {
        return Set.of(
                PeriodicLogEntry.of(
                        "Ring Light",
                        relay.get()::name,
                        SmartDashboardEntryType.STRING),
                PeriodicLogEntry.of(
                        "Current Command",
                        () -> this.getCurrentCommandName().isEmpty() ? "No Command" : this.getCurrentCommandName(),
                        SmartDashboardEntryType.STRING,
                        PeriodicLogPriority.NT_NEVER));
    }
}
