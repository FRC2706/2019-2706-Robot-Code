package ca.team2706.frc.robot.commands;

import ca.team2706.frc.robot.subsystems.ElevatorWithPID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class MoveLiftDownPID extends Command {

    private Joystick m_joystick;
    private double speed;

    public MoveLiftDownPID(Joystick joystick) {
        requires(ElevatorWithPID.getInstance());
        m_joystick = joystick;
        speed = m_joystick.getRawAxis(1);
    }

    @Override
    public void execute(){
        ElevatorWithPID.getInstance().moveDown(speed);
    }

    @Override
    public void end(){
        ElevatorWithPID.getInstance().stop();
    }

    @Override
    protected boolean isFinished(){
        return ElevatorWithPID.getInstance().reachedLimits();
    }
}