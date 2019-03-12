package ca.team2706.frc.robot.commands.ringlight;

import ca.team2706.frc.robot.subsystems.RingLight;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command for toggling the ring light.
 */
public class ToggleRingLight extends InstantCommand {

    /**
     * Constructs a nwe command for toggling the ring light with default arguments.
     */
    public ToggleRingLight() {
        requires(RingLight.getInstance());
    }

    @Override
    protected void initialize() {
        super.initialize();
        RingLight.getInstance().toggleLight();
    }
}
