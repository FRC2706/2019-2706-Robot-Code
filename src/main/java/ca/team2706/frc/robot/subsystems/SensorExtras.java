package ca.team2706.frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.hal.util.AllocationException;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.function.Consumer;

/**
 * Extra sensors used on Plyboy for testing
 *
 * Remove any sensors that are used on the real robot
 */
public class SensorExtras extends Subsystem {

    private static SensorExtras sensorExtras;

    /**
     * Initializes the subsystem
     */
    public static void init() {
        sensorExtras = new SensorExtras();
    }

    /**
     * Creates sensor extras
     *
     * Remove creation of objects that already exist
     */
    private SensorExtras() {
        wrap(this::createTalon, 5);
        wrap(this::createTalon, 6);
        wrap(this::createTalon, 7);
        wrap(this::createTalon, 8);

        wrap(this::createPwm, 0);
        wrap(this::createPwm, 1);
        wrap(this::createPwm, 2);
        wrap(this::createPwm, 3);

        wrap(this::createAnalogInput, 0);
        wrap(this::createAnalogInput, 1);

        wrap(this::createDio, 0);
        wrap(this::createDio, 1);
        wrap(this::createDio, 2);
        wrap(this::createDio, 3);

        wrap(this::createRelay, 0);
        wrap(this::createRelay, 1);
    }

    private void createTalon(int port) {
        WPI_TalonSRX talon = new WPI_TalonSRX(port);
        this.addChild("Unused Talon " + port, talon);
    }

    private void createPwm(int port) {
        PWM pwm = new PWM(port);
        this.addChild("Unused PWM " + port, pwm);
    }

    private void createAnalogInput(int port) {
        AnalogInput analogInput = new AnalogInput(port);
        this.addChild("Unused Analog Input " + port, analogInput);
    }

    private void createDio(int port) {
        DigitalOutput dio = new DigitalOutput(port);
        this.addChild("Unused DIO " + port, dio);
    }

    private void createRelay(int port) {
        Relay relay = new Relay(port);
        this.addChild("Unused Relay " + port, relay);
    }

    private static void wrap(Consumer<Integer> allocation, int port) {
        try {
            allocation.accept(port);
        } catch(AllocationException e) {
            System.out.println("Sensor Extras Warning:\n\t" + e.getMessage());
        }
    }

    @Override
    protected void initDefaultCommand() {}
}
