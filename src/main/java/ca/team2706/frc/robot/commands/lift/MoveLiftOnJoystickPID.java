package ca.team2706.frc.robot.commands.lift;

import ca.team2706.frc.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for moving the lift up and down with PID based on controller input.
 */
public class MoveLiftOnJoystickPID extends Command {

    private Joystick controller;

    private final int axisPort;

    /**
     * Moves the lift down using PID.
     *
     * @param joystick The joystick at which to look at to determine speed.
     * @param port     The axis port at which to look at for the speed.
     */
    public MoveLiftOnJoystickPID(Joystick joystick, final int port) {
        requires(Lift.getInstance());
        controller = joystick;

        this.axisPort = port;
    }

    @Override
    public void execute() {
        final double percentSpeed = controller.getRawAxis(axisPort);
        Lift.getInstance().setPercentOuput(percentSpeed);
    }

    @Override
    public void end() {
        Lift.getInstance().stop();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}