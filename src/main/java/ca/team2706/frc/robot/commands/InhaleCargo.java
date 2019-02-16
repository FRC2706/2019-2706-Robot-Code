package ca.team2706.frc.robot.commands;


import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class InhaleCargo extends Command {

    private Joystick joystick;

    public InhaleCargo(Joystick j) {
        requires(Intake.getInstance());
        joystick = j;
    }

    @Override
    public void execute() {
        double speed = joystick.getRawAxis(1);
        Intake.getInstance().inhale(speed);
    }

    @Override
    public void end() {
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
        //return Intake.getInstance().ballCaptured();
    }

}
