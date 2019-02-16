package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.config.Config;
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
            joy1.getRawAxis(Config.ARCADE_DRIVE_FORWARD);
            returns(1.0D, -1.0D, 0.0D, 0.1D);

            joy2.getRawAxis(Config.ARCADE_DRIVE_ROTATE);
            returns(1.0D, -1.0D, 0.0D, 0.1D);
        }};

        ArcadeDriveWithJoystick arcadeDrive = new ArcadeDriveWithJoystick(joy1, Config.ARCADE_DRIVE_FORWARD, true, joy2, Config.ARCADE_DRIVE_ROTATE, false);

        arcadeDrive.initialize();

        for (int i = 0; i < 4; i++) {
            arcadeDrive.execute();
        }

        arcadeDrive.end();

        new Verifications() {{
            DriveBase.getInstance().arcadeDrive(-1, 1, anyBoolean);
            DriveBase.getInstance().arcadeDrive(1, -1, anyBoolean);
            DriveBase.getInstance().arcadeDrive(-0.0, 0, anyBoolean);
            DriveBase.getInstance().arcadeDrive(-0.1, 0.1, anyBoolean);
        }};
    }
}
