package ca.team2706.frc.robot.config;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;

/**
 * Slot configuration using FluidConstants for parameters
 */
public class PIDConfiguration extends SlotConfiguration {

    private final FluidConstant<Double> kP;
    private final FluidConstant<Double> kI;
    private final FluidConstant<Double> kD;
    private final FluidConstant<Double> kF;

    private final FluidConstant<Integer> integralZone;
    private final FluidConstant<Integer> allowableClosedloopError;
    private final FluidConstant<Double> maxIntegralAccumulator;
    private final FluidConstant<Double> closedLoopPeakOutput;
    private final FluidConstant<Integer> closedLoopPeriod;

    /**
     * Constructs a fluid slot configuration
     *
     * @param name                     The name of the configuration
     * @param kP                       The default fP
     * @param kI                       The default kI
     * @param kD                       The default kD
     * @param kF                       The default kF
     * @param integralZone             The default integralZone
     * @param allowableClosedloopError The default allowableClosedLoopError
     * @param maxIntegralAccumulator   The default maxIntegralAccumulator
     * @param closedLoopPeakOutput     The default closedLoopPeakOutput
     * @param closedLoopPeriod         The default closedLoopPeriod
     */
    private PIDConfiguration(String name, double kP, double kI, double kD, double kF, int integralZone, int allowableClosedloopError, double maxIntegralAccumulator, double closedLoopPeakOutput, int closedLoopPeriod) {
        this.kP = new FluidConstant<>(name + ".kP", kP);
        this.kI = new FluidConstant<>(name + ".kI", kI);
        this.kD = new FluidConstant<>(name + ".kD", kD);
        this.kF = new FluidConstant<>(name + ".kF", kF);

        this.integralZone = new FluidConstant<>(name + ".integralZone", integralZone);
        this.allowableClosedloopError = new FluidConstant<>(name + ".allowableClosedloopError", allowableClosedloopError);
        this.maxIntegralAccumulator = new FluidConstant<>(name + ".maxIntegralAccumulator", maxIntegralAccumulator);
        this.closedLoopPeakOutput = new FluidConstant<>(name + ".closedLoopPeakOutput", closedLoopPeakOutput);
        this.closedLoopPeriod = new FluidConstant<>(name + ".closedLoopPeriod", closedLoopPeriod);
    }

    /**
     * Concigures a motor controller with PID settings
     *
     * @param controller The motor to configure
     * @param slot       The slot index to update
     * @return The worst {@code ErrorCode} of all the configurations performed
     */
    public ErrorCode setForSlot(IMotorController controller, int slot) {
        ErrorCode error = ErrorCode.OK;

        error = ErrorCode.worstOne(error, controller.config_kP(slot, kP.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.config_kI(slot, kI.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.config_kD(slot, kD.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.config_kF(slot, kF.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.config_IntegralZone(slot, integralZone.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.configAllowableClosedloopError(slot, allowableClosedloopError.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.configMaxIntegralAccumulator(slot, maxIntegralAccumulator.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.configClosedLoopPeakOutput(slot, closedLoopPeakOutput.value(), Config.CAN_LONG));
        error = ErrorCode.worstOne(error, controller.configClosedLoopPeriod(slot, closedLoopPeriod.value(), Config.CAN_LONG));

        return error;
    }

    /**
     * Constructs a fluid slot configuration
     *
     * @param name                     The name of the configuration
     * @param kP                       The default fP
     * @param kI                       The default kI
     * @param kD                       The default kD
     * @param kF                       The default kF
     * @param integralZone             The default integralZone
     * @param allowableClosedloopError The default allowableClosedLoopError
     * @param maxIntegralAccumulator   The default maxIntegralAccumulator
     * @param closedLoopPeakOutput     The default closedLoopPeakOutput
     * @param closedLoopPeriod         The default closedLoopPeriod
     */
    public static PIDConfiguration of(String name, double kP, double kI, double kD, double kF, int integralZone, int allowableClosedloopError, double maxIntegralAccumulator, double closedLoopPeakOutput, int closedLoopPeriod) {
        return new PIDConfiguration(name, kP, kI, kD, kF, integralZone, allowableClosedloopError, maxIntegralAccumulator, closedLoopPeakOutput, closedLoopPeriod);
    }
}
