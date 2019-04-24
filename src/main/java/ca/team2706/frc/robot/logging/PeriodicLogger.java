package ca.team2706.frc.robot.logging;

import ca.team2706.frc.robot.config.Config;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

import java.util.*;

public class PeriodicLogger {

    private static final Map<String, Set<PeriodicLogEntry>> loggers = new LinkedHashMap<>();
    private static boolean running = false;
    private static Object[] writter;
    private static final Notifier periodic = new Notifier(PeriodicLogger::logPeriodic);
    private static final Set<PeriodicLogEntry> ignored = new HashSet<>();
    private static String[] header;
    private static boolean firstCSVWrite = true;

    static {
        loggers.put("root", Set.of(PeriodicLogEntry.of("timestamp",
                System::currentTimeMillis,
                SmartDashboardEntryType.NUMBER,
                PeriodicLogPriority.NT_NEVER)));
    }

    public static void register(Object logger) {
        if(logger instanceof PeriodicLoggable) {
            register((PeriodicLoggable) logger);
        }
    }

    public static void register(PeriodicLoggable logger) {
        if(running) {
            Log.w("Can't register logger once initialized");
        }
        else {
            loggers.put(logger.getName(), logger.getLogs());
        }
    }

    public static void initialize() {
        if(running) {
            Log.w("Can't initialize more than once");
        }
        else {
            List<String> header = new ArrayList<>();

            for (Map.Entry<String, Set<PeriodicLogEntry>> entry : loggers.entrySet()) {
                for (PeriodicLogEntry logEntry : entry.getValue()) {
                    header.add(entry.getKey() + "." + logEntry.getName());
                }
            }

            PeriodicLogger.header = header.toArray(new String[0]);


            writter = new Object[header.size()];
            periodic.startPeriodic(Config.LOG_PERIOD);
        }
    }

    private static void logPeriodic() {
        double start = Timer.getFPGATimestamp();

        boolean fms = DriverStation.getInstance().isFMSAttached() && DriverStation.getInstance().isEnabled();

        int i =  0;
        for(Map.Entry<String, Set<PeriodicLogEntry>> entry : loggers.entrySet()) {
            for(PeriodicLogEntry logEntry : entry.getValue()) {
                String name = entry.getKey() + " " + logEntry.getName();
                Object data = logEntry.getData().get();

                if(fms) {
                    writter[i++] = data;
                }

                if((logEntry.getPriority() == PeriodicLogPriority.NT_ALWAYS || (!fms && logEntry.getPriority() == PeriodicLogPriority.NT_MAYBE)) && !ignored.contains(logEntry)) {
                    try {
                        logEntry.getType().put(name, data);
                    }
                    catch (ClassCastException e) {
                        Log.e("Invalid data type for " + name, e);
                        ignored.add(logEntry);
                    }
                }
            }
        }

        if(fms) {
            logFile(writter);
        }
    }

    private static void logFileHeader(String[] header) {
        Log.csvLogData(header);
    }
    private static void logFile(Object[] data) {
        if(firstCSVWrite) {
            logFileHeader(header);
            firstCSVWrite = false;
        }

        Log.csvLogData(data);
    }
}
