package ca.team2706.frc.robot.logging;

import java.util.Set;

public interface PeriodicLoggable {
    Set<PeriodicLogEntry> getLogs();

    String getName();
}
