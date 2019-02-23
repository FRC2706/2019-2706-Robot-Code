package ca.team2706.frc.robot.commands;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.command.Command;

public class ToggleRingLight extends Command {

    private Relay light;

    public ToggleRingLight() {
        light = new Relay(1);
    }

    @Override
    public void execute() {
        if (light.get() == Relay.Value.kForward){
            light.set(Relay.Value.kReverse);
        } else {
            light.set(Relay.Value.kForward);
        }
    }

    @Override
    public void end() {
        
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}