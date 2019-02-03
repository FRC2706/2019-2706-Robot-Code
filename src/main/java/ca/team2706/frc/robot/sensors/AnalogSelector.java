package ca.team2706.frc.robot.sensors;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class AnalogSelector extends SendableBase {

    private static final Range[] VOLTAGE_RANGES = {new Range(0, 2.5), new Range(2.5, 2.75),
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

        int index = 0;
        // Check each voltage range
        for (int i = 0; i < VOLTAGE_RANGES.length; i++) {
            // Check if the voltage is within the current voltage range
            if (VOLTAGE_RANGES[i].isWithin(voltage)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public static class Range {
        public final double min, max;

        public Range(double min, double max) {
            this.min = min;
            this.max = max;
        }

        /**
         * Determines if the number is within this range.
         *
         * @param number The number to be tested.
         * @return True if it's within range, false otherwise.
         */
        public boolean isWithin(final double number) {
            return min <= number && number <= max;
        }
    }
}
