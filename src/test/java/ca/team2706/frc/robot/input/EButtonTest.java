package ca.team2706.frc.robot.input;

import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import mockit.*;
import org.junit.Test;

import java.sql.Driver;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class EButtonTest {

    @Tested
    private IEButton button;

    @Mocked
    private DriverStation driverStation;

    @Mocked(stubOutClassInitialization = true)
    private MotControllerJNI motControllerJNI;

    @Test
    public void whenPressedTest() {
        new Expectations() {{
           driverStation.isDisabled(); result = false;
        }};

        Command command = new Command() {
            @Override
            protected boolean isFinished() {
                return false;
            }
        };

        button.expect(false, false, true, true, true, false);

        Scheduler.getInstance().enable();

        assertFalse(command.isRunning());

        button.whenPressed(command);
        Scheduler.getInstance().run();
        assertFalse(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertTrue(command.isRunning());

        Scheduler.getInstance().run();
        assertFalse(command.isRunning());

        Scheduler.getInstance().disable();
    }

    private static class IEButton extends EButton{

        private boolean[] results;
        private int i;

        private void expect(boolean... results) {
            i = 0;
            this.results = results;
        }

        @Override
        public boolean get() {
            if(i < results.length) {
                return results[i++];
            }

            return false;
        }
    }
}