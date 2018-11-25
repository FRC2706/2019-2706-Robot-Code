package ca.team2706.frc.robot.config;

/**
 * Class to represent all type of fluid constants.
 * @author Kyle Anderson
 */
public class FluidConstant<A> {

    // Fields
    A value;
    final A originalValue; // Keep track of the original value.
    final String name;

    /**
     * Creates a new FluidConstant class.
     * @param name The name of the constant used when printing it to file.
     * @param initialVAlue The initial value of the constant.
     */
    public FluidConstant(String name, A initialVAlue) {
        this.name = name;
        this.value = initialVAlue;
        this.originalValue = initialVAlue;
    }

    /**
     * Gets the current value of this constant.
     * @return
     */
    public A getValue() {
        return value;
    }

    /**
     * Sets the value of this constant to a new value.
     * @param value
     */
    public void setValue(A value) {
        this.value = value;
    }

    /**
     * Gets the name of this constant.
     * @return
     */
    public String getName() {
        return name;
    }
}
