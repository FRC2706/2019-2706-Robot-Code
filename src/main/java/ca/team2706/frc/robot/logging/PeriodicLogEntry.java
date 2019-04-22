package ca.team2706.frc.robot.logging;

import java.util.function.Supplier;

public class PeriodicLogEntry {

    private final String name;
    private final Supplier<?> data;
    private final SmartDashboardEntryType type;
    private final PeriodicLogPriority priority;

    public PeriodicLogEntry(String name, Supplier<?> data, SmartDashboardEntryType type, PeriodicLogPriority priority) {
        this.name = name;
        this.data = data;
        this.type = type;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Supplier<?> getData() {
        return data;
    }

    public SmartDashboardEntryType getType() {
        return type;
    }

    public PeriodicLogPriority getPriority() {
        return priority;
    }

    public static PeriodicLogEntry of(String name, Supplier<?> data, SmartDashboardEntryType type, PeriodicLogPriority priority) {
        return new PeriodicLogEntry(name, data, type, priority);
    }

    public static PeriodicLogEntry of(String name, Supplier<?> data, SmartDashboardEntryType type) {
        return of(name, data, type, PeriodicLogPriority.NT_MAYBE);
    }
}
