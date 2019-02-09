package ca.team2706.frc.robot.config;

import ca.team2706.frc.robot.Robot;
import ca.team2706.frc.robot.RobotState;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.wpilibj.DriverStation;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Class to represent all type of fluid constants.
 */
public class FluidConstant<A> {

    // Fields
    private A value;
    private final A deployedValue; // Keep track of the original value, the one which was deployed to the robot.
    private final String name;

    /**
     * The NetworkTables entry for this fluid constant.
     */
    private NetworkTableEntry ntEntry;
    private ArrayList<FluidChangeListener<A>> listeners = new ArrayList<>();

    // Listener for when the robot's state is changed.
    private final Consumer<RobotState> robotStateListener = this::addNTEntry;

    /**
     * Creates a new FluidConstant class.
     *
     * @param name         The name of the constant used when printing it to file.
     * @param initialValue The initial value of the constant.
     */
    FluidConstant(String name, A initialValue) {
        this.name = name;
        this.value = initialValue;
        this.deployedValue = initialValue;

        if (Robot.isRobotInitialized()) {
            addNTEntry();
        } else {
            Robot.setOnStateChange(robotStateListener);
        }
    }

    /**
     * Initializer for the Networktables Entry object for this fluid constant object.
     *
     * @param newState The new robot's state.
     */
    private void addNTEntry(RobotState newState) {
        if (newState == RobotState.ROBOT_INIT) {
            addNTEntry();
            Robot.removeStateListener(robotStateListener);
        }
    }

    /**
     * Initializer for the Networktables entry object for this fluid constant object.
     */
    private void addNTEntry() {
        // Initialize the networktables key for this fluid constant.
        NetworkTable table = Config.constantsTable;
        if (table != null && ntEntry == null) {
            ntEntry = table.getEntry(getName());
            ntEntry.setValue(value());


            // Add a listener so we can change update the value.
            ntEntry.addListener(entryNotification -> setValue(ntEntry.getValue()), EntryListenerFlags.kUpdate);
        }
    }

    /**
     * Gets the current value of this constant.
     *
     * @return The current value of this constant.
     */
    public A value() {
        return value;
    }

    /**
     * Adds a listener to be called when the constant's value is changed.
     *
     * @param valueListener The listener.
     */
    public void addChangeListener(FluidChangeListener<A> valueListener) {
        listeners.add(valueListener);
    }

    /**
     * Sets the value of this constant to a new value. Will only set constants if {@link #canSet()} returns true.
     *
     * @param value The value to which the constant should be set.
     */
    public void setValue(A value) {
        // Only allow the changing of the fluid constant while disabled.
        if (canSet()) {
            final A oldValue = value;
            this.value = value;
            updateNTEntry(); // Update the Networktables entry if the value changed.
            listeners.forEach(aConsumer -> aConsumer.changed(oldValue, value));
        }
    }

    /**
     * Sets the value of this constant to the specified networktables value.
     *
     * @param value The value to which
     */
    @SuppressWarnings("unchecked")
    private void setValue(NetworkTableValue value) {
        if (canSet()) {
            Object objValue = value.getValue();
            setValue((A) objValue);
        } else {
            updateNTEntry();
        }
    }

    /**
     * Updates the networktables entry to the current value of this fluid constant.
     */
    private void updateNTEntry() {
        if (ntEntry != null) {
            ntEntry.setValue(value());
        }
    }

    /**
     * Determines if fluid constants should be allowed to be set.
     *
     * @return True if fluid constants should be allowed to be set, false otherwise.
     */
    private static boolean canSet() {
        return DriverStation.getInstance().isDisabled();
    }

    /**
     * Gets the name of this constant.
     *
     * @return The name of this constant, as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Creates a human-readable string to be displayed in a file showing the default value
     * of this fluid constant alongside its current value.
     *
     * @return The string to be printed to file, with no newline character added.
     */
    public String toFileString() {
        return String.format("Deployed: %s || Current: %s", deployedValue, value);
    }
}
