package ca.team2706.frc.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class AnalogSelector extends SendableBase {

    private static final Range[] voltages = {new Range(0, 2.5), new Range(2.5, 2.75),
            new Range(2.75, 3.1), new Range(3.1, 3.5), new Range(3.5, 3.75),
            new Range(3.75, 3.95), new Range(3.95, 4.1), new Range(4.1, 4.2),
            new Range(4.2, 4.3), new Range(4.3, 4.4), new Range(4.4, 4.5),
            new Range(4.5, 4.6), new Range(4.6, 5)};
    private final AnalogInput analogInput;

    public AnalogSelector(int channel) {
        this.analogInput = new AnalogInput(channel);
        addChild(analogInput);
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.addDoubleProperty("Voltage", analogInput::getAverageVoltage, null);
        builder.addDoubleProperty("Index", this::getIndex, null);
    }

    public int getIndex() {

        final double voltage = analogInput.getAverageVoltage();

        // Check each voltage range
        for (int i = 0; i < voltages.length; i++) {
            // Get the current voltage

            // Check if the voltage is within the current voltage range
            if (voltage >= voltages[i].min && voltage < voltages[i].max) {
                // The selector is within this range
                return i;
            }
        }

        // Default to index of 0
        return 0;
    }
    public static class Range {
        public final double min, max;

        public Range (double min, double max) {
            this.min = min;
            this.max = max;
        }
    }
}
