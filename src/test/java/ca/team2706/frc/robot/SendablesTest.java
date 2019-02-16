package ca.team2706.frc.robot;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import org.junit.Test;

import java.util.function.Consumer;

import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;

public class SendablesTest {

    @Tested
    private SendableBase sendableBase;

    @Mocked
    private SensorCollection sensorCollection;

    @Injectable
    private NetworkTable table;

    @Injectable
    private NetworkTableEntry ntEntry;

    /**
     * Checks the correct name was added to LiveWindow for the Pigeon sendable
     *
     * @param pigeon The pigeon to add
     */
    @Test
    public void pigeonNameTest(@Injectable PigeonIMU pigeon) {
        sendableBase = Sendables.newPigeonSendable(pigeon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        new Verifications() {{
            table.getEntry(Sendables.PIGEON_NAME);
            times = 1;
        }};
    }

    /**
     * Checks the correct name was added to LiveWindow for the Talon sendable
     *
     * @param talon The talon to add
     */
    @Test
    public void talonNameTest(@Injectable WPI_TalonSRX talon) {
        sendableBase = Sendables.newTalonEncoderSendable(talon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        new Verifications() {{
            table.getEntry(Sendables.TALON_NAME);
        }};
    }

    /**
     * Checks the LiveWindow is setting the Network Tables entry to the talon encoders
     *
     * @param talon The talon to add to LiveWindow
     */
    @Test
    public void newTalonSendableUpdateTest(@Injectable WPI_TalonSRX talon) {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;

            sensorCollection.getQuadraturePosition();
            returns(-5, 0, 5);

            table.getEntry(Sendables.TALON_NAME);
            result = ntEntry;
        }};

        sendableBase = Sendables.newTalonEncoderSendable(talon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        // Update the table three times.
        for (int i = 0; i < 3; i++) {
            builder.updateTable();
        }

        new Verifications() {{
            ntEntry.setDouble(-5);
            ntEntry.setDouble(0);
            ntEntry.setDouble(5);
        }};
    }

    /**
     * Checks the LiveWindow is setting the talon encoder value from the Network Tables entry
     *
     * @param talon The talon to add to LiveWindow
     */
    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @Test
    public void newTalonSendableCreateTest(@Injectable WPI_TalonSRX talon, @Injectable NetworkTableValue value) {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;

            table.getEntry(Sendables.TALON_NAME);
            result = ntEntry;

            ntEntry.isValid();
            result = true;

            ntEntry.addListener((Consumer<EntryNotification>) any, anyInt);
            result = new Delegate() {
                @SuppressWarnings("unused")
                public int addListener(Consumer<EntryNotification> listener, int flags) {
                    listener.accept(new EntryNotification(null, 0, 0, null, value, 0));

                    return 0;
                }
            };

            value.getDouble();
            returns(-24.0, 2.0, 0.0);

            value.isDouble();
            result = true;
        }};

        sendableBase = Sendables.newTalonEncoderSendable(talon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        builder.startListeners();
        builder.startListeners();
        builder.startListeners();

        new Verifications() {{
            talon.getSensorCollection().setQuadraturePosition(-24, anyInt);
            talon.getSensorCollection().setQuadraturePosition(2, anyInt);
            talon.getSensorCollection().setQuadraturePosition(0, anyInt);
        }};
    }

    /**
     * Checks the LiveWindow is setting the Network Tables entry to the pigeon angle
     *
     * @param pigeon The pigeon to add to LiveWindow
     */
    @Test
    public void newPigeonSendableUpdateTest(@Injectable PigeonIMU pigeon) {
        new Expectations() {{
            pigeon.getYawPitchRoll((double[]) any);

            returns(makePigeonExpectation(-5.0), makePigeonExpectation(0.0), makePigeonExpectation(5.0));

            table.getEntry(Sendables.PIGEON_NAME);
            result = ntEntry;
        }};

        sendableBase = Sendables.newPigeonSendable(pigeon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        // Update the table three times.

        for (int i = 0; i < 3; i++) {
            builder.updateTable();
        }

        new Verifications() {{
            ntEntry.setDouble(-5.0);
            ntEntry.setDouble(0.0);
            ntEntry.setDouble(5.0);
        }};
    }

    private static Delegate makePigeonExpectation(double expectedValue) {
        return new Delegate() {
            @SuppressWarnings("unused")
            public void getYawPitchRoll(double[] array) {
                array[0] = expectedValue;
                array[1] = 0;
                array[2] = 0;
            }
        };
    }

    /**
     * Checks the LiveWindow is setting the pigeon rotation value from the Network Tables entry
     *
     * @param pigeon The pigeon to add to LiveWindow
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "unchecked"})
    @Test
    public void newPigeonSendableCreateTest(@Injectable PigeonIMU pigeon, @Injectable NetworkTableValue value) {
        new Expectations() {{
            table.getEntry(Sendables.PIGEON_NAME);
            result = ntEntry;

            ntEntry.isValid();
            result = true;

            ntEntry.addListener((Consumer<EntryNotification>) any, anyInt);
            result = new Delegate() {
                @SuppressWarnings("unused")
                public int addListener(Consumer<EntryNotification> listener, int flags) {
                    listener.accept(new EntryNotification(null, 0, 0, null, value, 0));

                    return 0;
                }
            };

            value.getDouble();
            returns(-24.0, 2.0, 0.0);

            value.isDouble();
            result = true;
        }};

        sendableBase = Sendables.newPigeonSendable(pigeon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        builder.startListeners();
        builder.startListeners();
        builder.startListeners();

        new Verifications() {{
            pigeon.setYaw(-24.0);
            pigeon.setYaw(2.0);
            pigeon.setYaw(0.0);
        }};
    }
}