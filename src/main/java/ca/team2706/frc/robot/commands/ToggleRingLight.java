package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.subsystems.RingLight;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command for toggling the ring light.
 */
public class ToggleRingLight extends InstantCommand {

    public ToggleRingLight() {
        requires(RingLight.getInstance());
    }

    @Override
    protected void initialize() {
        super.initialize();
        RingLight.getInstance().toggleLight();
    }
}
