package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftDownPID extends Command {

    private Joystick m_joystick;
    private double speed;

    public MoveLiftDownPID(Joystick joystick) {
        requires(Lift.getInstance());
        m_joystick = joystick;
        speed = m_joystick.getRawAxis(1);
    }

    @Override
    public void execute() {
        Lift.getInstance().moveDown(speed);
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return Lift.getInstance().reachedLimits();
    }
}