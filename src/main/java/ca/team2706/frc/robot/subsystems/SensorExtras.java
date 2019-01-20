package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.function.Consumer;

/**
 * Extra sensors used on Plyboy for testing
 *
 * Remove any sensors that are used on the real robot in other parts of the code
 */
public class SensorExtras extends Subsystem {

    private static SensorExtras currentInstance;

    /**
     * Gets the reference to the subsystem
     *
     * @return The subsystem singleton
     */
    public static SensorExtras getInstance() {
        if (currentInstance == null) {
            init();
        }

        return currentInstance;
    }

    /**
     * Initializes the subsystem
     */
    public static void init() {
        currentInstance = new SensorExtras();
    }

    /**
     * Creates sensor extras
     *
     * Remove sensors that are being used in other parts of the code from here
     */
    private SensorExtras() {
        allocate(this::createTalon, 5, 8);
        allocate(this::createPwm, 0, 4);
        allocate(this::createAnalogInput, 0, 1);
        allocate(this::createDio, 0, 4);
        allocate(this::createRelay, 0, 1);
    }

    /**
     * Allocates a TalonSRX
     *
     * @param port The CAN Bus port of the Talon
     */
    private void createTalon(int port) {
        WPI_TalonSRX talon = new WPI_TalonSRX(port);
        this.addChild("Unused Talon " + port, talon);
    }

    /**
     * Allocates a PWM
     *
     * @param port The PWM port to allocate
     */
    private void createPwm(int port) {
        PWM pwm = new PWM(port);
        this.addChild("Unused PWM " + port, pwm);
    }

    /**
     * Allocates an analog input
     *
     * @param port The analog input port to allocate
     */
    private void createAnalogInput(int port) {
        AnalogInput analogInput = new AnalogInput(port);
        this.addChild("Unused Analog Input " + port, analogInput);
    }

    /**
     * Allocates a DIO
     *
     * @param port The DIO port to allocate
     */
    private void createDio(int port) {
        DigitalOutput dio = new DigitalOutput(port);
        this.addChild("Unused DIO " + port, dio);
    }

    /**
     * Allocates a relay
     *
     * @param port The relay port to allocate
     */
    private void createRelay(int port) {
        Relay relay = new Relay(port);
        this.addChild("Unused Relay " + port, relay);
    }

    /**
     * Allocates resources within a certain range
     *
     * @param allocation Reference to the allocator
     * @param portStart The port to start allocating
     * @param portEnd The port to stop allocating
     */
    private static void allocate(Consumer<Integer> allocation, int portStart, int portEnd) {
        for(int i = portStart; i <= portEnd; i++) {
            wrap(allocation, i);
        }
    }

    /**
     * Tries to allocate a resource, and reports a warning if the resource cannot be allocated
     *
     * @param allocation The reference to the allocator
     * @param port The port to allocate
     */
    private static void wrap(Consumer<Integer> allocation, int port) {
        try {
            allocation.accept(port);
        } catch(RuntimeException e) {
            System.out.println("Sensor Extras Warning:\n\t" + e.getMessage());
        }
    }

    @Override
    protected void initDefaultCommand() {}
}
