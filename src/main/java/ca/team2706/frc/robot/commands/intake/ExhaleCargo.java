package ca.team2706.frc.robot.commands.intake;


import ca.team2706.frc.robot.config.Config;
import ca.team2706.frc.robot.config.FluidConstant;
import ca.team2706.frc.robot.subsystems.Intake;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Command for exhaling cargo on the robot.
 */
public class ExhaleCargo extends Command {
    private int port;

    /**
     * The joystick used to determine the cargo speed.
     */
    private final Joystick joystick;

    public ExhaleCargo(Joystick joystick, final FluidConstant<String> portBinding) {
        requires(Intake.getInstance());
        this.port = Config.XboxValue.getPortFromFluidConstant(portBinding);
        this.joystick = joystick;

        portBinding.addChangeListener((oldValue, newValue) -> this.port = Config.XboxValue.getPortFromNTString(newValue));
    }

    @Override
    public void execute() {
        final double speed = joystick.getRawAxis(port);
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
