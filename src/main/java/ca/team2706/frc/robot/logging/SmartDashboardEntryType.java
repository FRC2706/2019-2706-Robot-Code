package ca.team2706.frc.robot.logging;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public enum SmartDashboardEntryType {

    NUMBER((key, data) -> SmartDashboard.putNumber(key, ((Number)data).doubleValue())),
    BOOLEAN(SmartDashboard::putBoolean),
    STRING(SmartDashboard::putString),
    RAW((SmartDashboardFunction<byte[]>) SmartDashboard::putRaw),
    NUMBER_ARRAY((SmartDashboardFunction<double[]>) SmartDashboard::putNumberArray),
    BOOLEAN_ARRAY((SmartDashboardFunction<boolean[]>) SmartDashboard::putBooleanArray),
    STRING_ARRAY(SmartDashboard::putStringArray);

    private final SmartDashboardFunction put;

    <T> SmartDashboardEntryType(SmartDashboardFunction<T> put) {
        this.put = put;
    }

    @SuppressWarnings("unchecked")
    public boolean put(String name, Object data) throws ClassCastException {
        return put.put(name, data);
    }
}
