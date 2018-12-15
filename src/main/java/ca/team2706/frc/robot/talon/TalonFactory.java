package ca.team2706.frc.robot.talon;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public final class TalonFactory {

    private TalonFactory() {
        throw new IllegalStateException("Utility method cannot be instantiated");
    }

    public static WPI_TalonSRX defaultConfig(int id) {
        return new WPI_TalonSRX(id);
    }

    public static WPI_TalonSRX fileConfig(int id, String configLocation) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }

    public static WPI_TalonSRX stringConfig(int id, String configString) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }

    public static WPI_TalonSRX talonConfig(int id, Object config) {
        // TODO: Actually configure
        return new WPI_TalonSRX(id);
    }
}
