package ca.team2706.frc.robot.operatorfeedback.rumbler;

import ca.team2706.frc.robot.subsystems.DriveBase;
import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import mockit.VerificationsInOrder;
import org.junit.Before;
import org.junit.Test;
import util.Util;

import java.time.Clock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RumblerTest {
    @Mocked
    private Joystick driverStick;

    @Mocked
    private Joystick operatorStick;

    @Mocked
    private DriveBase driveBase;

    @Mocked
    private VictorSPX intakeMotor;

    @Mocked
    private WPI_TalonSRX talons;

    @Mocked
    private DoubleSolenoid solenoids;

    @Mocked
    private Relay relays;

    @Mocked
    private AnalogInput irSensor;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        // I should re-initialize OI for each time.
        Util.resetSubsystems();

        new Expectations() {{
            new Joystick(0);
            result = driverStick;

            new Joystick(1);
            result = operatorStick;
        }};

        new Expectations(ErrorCode.class) {{
            ErrorCode.worstOne((ErrorCode)any, (ErrorCode)any);
            result = ErrorCode.OK;
            minTimes = 0;
        }};
    }

    /**
     * Tests to ensure that the rumbler rumbles when the pattern it has wants it to.
     */
    @Test
    public void testRumbles() {
        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }

            @Override
            public double getRumbleIntensity() {
                return 1.0;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();

        assertTrue(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0D));
            times = 1;
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0D));
            times = 1;

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0D));
            times = 1;
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0D));
            times = 1;
        }};
    }

    /**
     * Tests to ensure that the rumbler turns off when asked to do so by the pattern.
     */
    @Test
    public void testStopsRumble() {
        RumblePattern pattern = new RumblePattern() {
            int times = 0;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                times++;
                return times <= 1; // First time it should rumble, second time it should stop.
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }

            @Override
            public double getRumbleIntensity() {
                return 1.0;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        runRumbler(rumbler, 2);

        assertFalse(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0D);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0D);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0D);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0D);
        }};
    }

    /**
     * Ensures that when the rumbler is ended the controller is told to stop rumbling.
     */
    @Test
    public void testRumbleStopsOnEnd() {
        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        rumbler.end();
        assertFalse(rumbler.isRumbling());

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
        }};
    }

    @Test
    public void testPatternEnd() {
        RumblePattern pattern = new RumblePattern() {
            int times = 0;

            @Override
            boolean shouldRumble(long millisecondsOn) {
                times++;
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return times >= 2; // Will say the pattern is over after 4 seconds.
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        rumbler.execute();
        assertTrue(rumbler.isRumbling());
        assertTrue(rumbler.isFinished());

        rumbler.end();

        new Verifications() {{
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0);
        }};
    }

    @Test
    public void testRightControllerIsRumbled() {
        RumblePattern pattern1 = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.DRIVER_JOYSTICK;
            }
        };
        RumblePattern pattern2 = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return true;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return false;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.OPERATOR_JOYSTICK;
            }
        };

        Rumbler rumbler1 = new Rumbler(pattern1);
        rumbler1.initialize();
        rumbler1.execute();
        assertTrue(rumbler1.isRumbling());

        Rumbler rumbler2 = new Rumbler(pattern2);
        rumbler2.initialize();
        rumbler2.execute();
        assertTrue(rumbler2.isRumbling());

        new VerificationsInOrder() {{
            driverStick.setRumble((GenericHID.RumbleType) any, withNotEqual(0));
            times = 2;

            operatorStick.setRumble((GenericHID.RumbleType) any, withNotEqual(0));
            times = 2;
        }};
    }

    /**
     * Tests going through an entire rumbler pattern realistically, with changing conditions
     * and time passing.
     */
    @Test
    public void testRealisticPattern(@Mocked final Clock clock) {
        new Expectations() {{
            clock.millis();
            returns(0L, 50L, 50L, 100L, 100L, 150L, 150L, 200L, 200L, 300L, 300L,
                    500L, 500L, 550L, 550L, 590L, 590L, 700L, 700L, 800L, 800L, 2000L, 2000L);
        }};

        RumblePattern pattern = new RumblePattern() {
            @Override
            boolean shouldRumble(long millisecondsOn) {
                return millisecondsOn % 200 < 150;
            }

            @Override
            boolean isOver(long millisecondsOn) {
                return millisecondsOn >= 2000;
            }

            @Override
            public Rumbler.JoystickSelection getJoystick() {
                return Rumbler.JoystickSelection.BOTH_JOYSTICKS;
            }

            @Override
            public double getRumbleIntensity() {
                return 1.0;
            }
        };

        Rumbler rumbler = new Rumbler(pattern);
        rumbler.initialize();

        runRumbler(rumbler, 2);
        assertTrue(rumbler.isRumbling());
        runRumbler(rumbler, 1);
        assertFalse(rumbler.isRumbling());
        runRumbler(rumbler, 3);
        assertTrue(rumbler.isRumbling());
        runRumbler(rumbler, 2);
        assertFalse(rumbler.isRumbling());
        runRumbler(rumbler, 3);
        assertTrue(rumbler.isRumbling());

        assertTrue(rumbler.isFinished());
        rumbler.end();

        new Verifications() {{
            // Verifications for turning on.
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0D));
            times = 3;
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0D));
            times = 3;

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, withNotEqual(0D));
            times = 3;
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, withNotEqual(0D));
            times = 3;


            // Verifications for turning off.
            driverStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0D);
            times = 3;
            driverStick.setRumble(GenericHID.RumbleType.kRightRumble, 0D);
            times = 3;

            operatorStick.setRumble(GenericHID.RumbleType.kLeftRumble, 0D);
            times = 3;
            operatorStick.setRumble(GenericHID.RumbleType.kRightRumble, 0D);
            times = 3;
        }};
    }


    /**
     * Runs the {@link Rumbler#execute()} and {@link Rumbler#isFinished()} methods <code>numTimes</code> number of times,
     * simulating actual usage.
     *
     * @param rumbler  The rumbler class to be run.
     * @param numTimes The number of times to call the execute method.
     */
    private static void runRumbler(Rumbler rumbler, final int numTimes) {
        for (int i = 0; i < numTimes; i++) {
            rumbler.execute();
            rumbler.isFinished();
        }
    }
}