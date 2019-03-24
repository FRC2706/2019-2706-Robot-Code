package ca.team2706.frc.robot;

import com.ctre.phoenix.ErrorCode;

import java.util.Arrays;
import java.util.Collections;

/**
 * Status for subsystems when they initialize
 */
public enum SubsystemStatus {
    /**
     * All is well
     */
    OK,

    /**
     * Something went wrong, but still functional
     */
    WORKABLE,

    /**
     * Something went wrong, and autonomous shouldn't be run
     */
    DISABLE_AUTO,

    /**
     * Something bad happened
     */
    ERROR;

    /**
     * Finds the maximum error based on the lowest item in the enum
     *
     * @param subsystemStatuses The stauses to compare
     * @return The most severe subsystem status of those given
     */
    public static SubsystemStatus maxError(SubsystemStatus... subsystemStatuses) {
        return Collections.max(Arrays.asList(subsystemStatuses));
    }

    /**
     * Checks whether an ErrorCode results in an error
     *
     * @param errorCode The error code to check
     * @return True when the error code means there was an error
     */
    public static boolean checkError(ErrorCode errorCode) {
        return errorCode != null && ErrorCode.valueOf(errorCode.value) != ErrorCode.OK;

    }
}
