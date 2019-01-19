package ca.team2706.frc.robot.commands.bling.patterns;

import ca.team2706.frc.robot.commands.bling.BlingController;
import ca.team2706.frc.robot.subsystems.Bling;

/**
 * Blank command (nothing showing) for the bling system.
 */
public class Blank extends BlingPattern {
    public Blank() {
        
        // add this command to all periods
        operationPeriod.add(BlingController.Period.AUTONOMOUS);
        operationPeriod.add(BlingController.Period.CLIMB);
        operationPeriod.add(BlingController.Period.TELEOP_WITHOUT_CLIMB);
        
        command = Bling.RAINBOW_CYCLE;
        
        repeatCount = 20;
        waitMs = 100;
    }
    
    /**
     * Determines whether or not the conditions for this command are met.
     * @return Always true because this is the blank command
     */
    @Override
    public boolean conditionsMet() {
        return true;
    }
}
