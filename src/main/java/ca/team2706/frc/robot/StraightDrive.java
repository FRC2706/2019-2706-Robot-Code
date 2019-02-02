package ca.team2706.frc.robot;

import ca.team2706.frc.robot.subsystems.DriveBase;
import edu.wpi.first.wpilibj.command.Command;

import java.util.function.Supplier;

public class StraightDrive extends Command {

    private final Supplier<Double> speed, position;
    private final Supplier<Integer> minDoneCycles;
    private int doneCycles = 0;

    public StraightDrive(Supplier<Double> speed, Supplier<Double> position, Supplier<Integer> minDoneCycles) {
        requires(DriveBase.getInstance());
        this.speed = speed;
        this.position = position;
        this.minDoneCycles = minDoneCycles;
    }

    @Override
    public void initialize() {
        DriveBase.getInstance().setBrakeMode(true);
        DriveBase.getInstance().setPositionNoGyroMode();

        doneCycles = 0;
    }

    @Override
    public void execute() {
        DriveBase.getInstance().setPosition(speed.get(), position.get());
    }

    @Override
    public boolean isFinished() {
        if(DriveBase.getInstance().getLeftError() < 0.3) {
            doneCycles++;
        }

        return doneCycles >= minDoneCycles.get();
    }
}
