package ca.team2706.frc.robot.config;

/**
 * Listener for the value of a fluid constant.
 *
 * @param <T> The type of the fluid constant.
 */
public interface FluidChangeListener<T> {
    /**
     * Called when the fluid constant's value is changed.
     *
     * @param oldValue The old constant value.
     * @param newValue The new constant value.
     */
    void changed(T oldValue, T newValue);
}
