package ca.team2706.frc.robot;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.networktables.EntryNotification;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;
import mockit.*;
import org.junit.Test;

import java.util.function.Consumer;

public class SendablesTest {

    @Tested
    SendableBase sendableBase;

    @Mocked
    SensorCollection sensorCollection;

    @Injectable
    NetworkTable table;

    @Injectable
    NetworkTableEntry entry;

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

    @Test
    public void newTalonSendableUpdateTest(@Injectable WPI_TalonSRX talon) {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            sensorCollection.getQuadraturePosition();
            returns(-5, 0, 5);
            table.getEntry(Sendables.TALON_NAME);
            result = entry;
        }};

        sendableBase = Sendables.newTalonEncoderSendable(talon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        builder.updateTable();
        builder.updateTable();
        builder.updateTable();

        new Verifications() {{
            entry.setDouble(-5);
            entry.setDouble(0);
            entry.setDouble(5);
        }};
    }

    @Test
    public void newTalonSendableCreateTest(@Injectable WPI_TalonSRX talon, @Injectable NetworkTableValue value) {
        new Expectations() {{
            talon.getSensorCollection();
            result = sensorCollection;
            table.getEntry(Sendables.TALON_NAME);
            result = entry;
            entry.isValid();
            result = true;
            value.getDouble();
            returns(-24.0, 2.0, 0.0);
            value.isDouble();
            result = true;
        }};

        new MockUp<NetworkTableEntry>() {
            @Mock
            public int addListener(Consumer<EntryNotification> listener, int flags) {
                listener.accept(new EntryNotification(null, 0, 0, null, value, 0));

                return 0;
            }
        };

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

    @Test
    public void newPigeonSendableUpdateTest(@Injectable PigeonIMU pigeonIMU) {
        new Expectations() {{
            pigeonIMU.getFusedHeading();
            returns(-5.0, 0.0, 5.0);
            table.getEntry(Sendables.PIGEON_NAME);
            result = entry;
        }};

        sendableBase = Sendables.newPigeonSendable(pigeonIMU);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        builder.updateTable();
        builder.updateTable();
        builder.updateTable();

        new Verifications() {{
            entry.setDouble(-5.0);
            entry.setDouble(0.0);
            entry.setDouble(5.0);
        }};
    }

    @Test
    public void newPigeonSendableCreateTest(@Injectable PigeonIMU pigeon, @Injectable NetworkTableValue value) {
        new Expectations() {{
            ;
            table.getEntry(Sendables.PIGEON_NAME);
            result = entry;
            entry.isValid();
            result = true;
            value.getDouble();
            returns(-24.0, 2.0, 0.0);
            value.isDouble();
            result = true;
        }};

        new MockUp<NetworkTableEntry>() {
            @Mock
            public int addListener(Consumer<EntryNotification> listener, int flags) {
                listener.accept(new EntryNotification(null, 0, 0, null, value, 0));

                return 0;
            }
        };

        sendableBase = Sendables.newPigeonSendable(pigeon);

        SendableBuilderImpl builder = new SendableBuilderImpl();
        builder.setTable(table);
        sendableBase.initSendable(builder);

        builder.startListeners();
        builder.startListeners();
        builder.startListeners();

        new Verifications() {{
            pigeon.setFusedHeading(-24.0);
            pigeon.setFusedHeading(2.0);
            pigeon.setFusedHeading(0.0);
        }};
    }
}