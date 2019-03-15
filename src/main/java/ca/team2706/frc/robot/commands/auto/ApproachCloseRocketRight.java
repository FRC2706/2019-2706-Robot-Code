package ca.team2706.frc.robot.commands.auto;

import ca.team2706.frc.robot.commands.drivebase.FollowTrajectoryFromFile;
import ca.team2706.frc.robot.commands.mirrorable.MirroredCommandGroup;

public class ApproachCloseRocketRight extends MirroredCommandGroup {

    public ApproachCloseRocketRight() {
        addMirroredSequential(new FollowTrajectoryFromFile(1.0, 10, "CloseRocketRight"));
    }
}
