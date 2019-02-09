package ca.team2706.frc.robot.commands;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import ca.team2706.frc.robot.subsystems.Intake;

public class InhaleCargo extends Command {
    
    private double speed;

    public InhaleCargo(Joystick joystick){
        requires(Intake.getInstance());
        speed = joystick.getRawAxis(1);
    }

    @Override
    public void execute() {
        Intake.getInstance().inhale(speed); 
    }

    @Override
    public void end() {
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return Intake.getInstance().ballCaptured();
    }

}
