package ca.team2706.frc.robot;

import java.util.Arrays;
import java.util.Collections;

public enum SubsystemStatus {
    OK,
    WORKABLE,
    DISABLE_AUTO,
    ERROR;

    public static SubsystemStatus maxError(SubsystemStatus... subsystemStatuses) {
        return Collections.max(Arrays.asList(subsystemStatuses));
    }
}
