package ca.team2706.frc.robot.commands.drivebase;

import ca.team2706.frc.robot.subsystems.DriveBase;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Test;

import java.util.function.Supplier;

public class ArcadeDriveTest {

    @Mocked
    private Supplier<Double> forwardVal;

    @Mocked
    private Supplier<Double> rotateVal;

    @Mocked
    private DriveBase driveBase;

    @Test
    public void testBrakeModeOn() {
        testBrakeMode(true);
    }

    @Test
    public void testBrakeModeOff() {
        testBrakeMode(false);
    }

    /**
     * Makes sure that the brake mode gets set to the correct value at the end of the match
     */
    private void testBrakeMode(boolean brake) {
        new Expectations() {{
            forwardVal.get();
            result = 0.0;

            rotateVal.get();
            result = 0.0;
        }};

        ArcadeDrive arcadeDrive = new ArcadeDrive(forwardVal, rotateVal, false, brake) {
            @Override
            public boolean isFinished() {
                return false;
            }
        };

        arcadeDrive.initialize();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};

        arcadeDrive.execute();

        arcadeDrive.end();

        new Verifications() {{
            DriveBase.getInstance().setBrakeMode(brake);
        }};
    }
}
