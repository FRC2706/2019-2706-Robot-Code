package ca.team2706.frc.robot.commands.intake;


import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for exhaling cargo on the robot.
 */
public class ExhaleCargo extends Command {
    private final int port;

    private final Joystick joystick;

    public ExhaleCargo(Joystick joystick, final int port) {
        requires(Intake.getInstance());
        this.port = port;
        this.joystick = joystick;
    }

    @Override
    public void execute() {
        double speed = joystick.getRawAxis(port);
        Intake.getInstance().exhaleCargo(speed);
    }

    @Override
    public void end() {
        Intake.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
