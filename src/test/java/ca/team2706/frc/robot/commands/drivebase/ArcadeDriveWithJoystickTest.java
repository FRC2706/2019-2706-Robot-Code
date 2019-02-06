package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.Joystick;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;

public class ArcadeDriveWithJoystickTest {
    @Mocked
    private DriveBase driveBase;

    @Mocked
    private Joystick joy1;

    @Mocked
    private Joystick joy2;

    /**
     * Makes sure that values are negated correctly
     */
    @Test
    public void testNegate() {
        new Expectations() {{
            joy1.getRawAxis(0);
            returns(1.0D, -1.0D, 0.0D, 0.1D);

            joy2.getRawAxis(0);
            returns(1.0D, -1.0D, 0.0D, 0.1D);
        }};

        ArcadeDriveWithJoystick arcadeDrive = new ArcadeDriveWithJoystick(joy1, 0, true, joy2, 0, false);

        arcadeDrive.initialize();

        arcadeDrive.execute();
        arcadeDrive.execute();
        arcadeDrive.execute();
        arcadeDrive.execute();

        arcadeDrive.end();

        new Verifications() {{
            DriveBase.getInstance().arcadeDrive(-1, 1, anyBoolean);
            DriveBase.getInstance().arcadeDrive(1, -1, anyBoolean);
            DriveBase.getInstance().arcadeDrive(-0.0, 0, anyBoolean);
            DriveBase.getInstance().arcadeDrive(-0.1, 0.1, anyBoolean);
        }};
    }
}
