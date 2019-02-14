package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MirrorableTest {

    @Tested
    private MirroredCommand a, b, c, d, e;

    @Tested
    private MirroredCommandGroup f, g, h;

    @Mocked
    private Subsystem subsystem;

    /**
     * Test that commands are mirrored correctly
     */
    @Test
    public void testCommands() {
        assertFalse(a.isMirrored());
        assertFalse(b.isMirrored());
        assertFalse(c.isMirrored());
        assertFalse(d.isMirrored());
        assertFalse(e.isMirrored());
        assertFalse(f.isMirrored());
        assertFalse(g.isMirrored());
        assertFalse(h.isMirrored());

        h.addSequential(d);
        h.addParallel(e);
        g.addSequential(c);
        g.addParallel(h);
        f.addParallel(a);
        f.addSequential(b);
        f.addSequential(g);

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertTrue(d.isMirrored());
        assertTrue(e.isMirrored());
        assertTrue(f.isMirrored());
        assertTrue(g.isMirrored());
        assertTrue(h.isMirrored());

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertTrue(d.isMirrored());
        assertTrue(e.isMirrored());
        assertTrue(f.isMirrored());
        assertTrue(g.isMirrored());
        assertTrue(h.isMirrored());
    }


    /**
     * Test that commands are mirrored correctly when some command groups are non-mirrored
     */
    @Test
    public void testCommandsNonMirrored() {
        CommandGroup testCommandGroup = new CommandGroup();

        assertFalse(a.isMirrored());
        assertFalse(b.isMirrored());
        assertFalse(c.isMirrored());
        assertFalse(d.isMirrored());
        assertFalse(e.isMirrored());
        assertFalse(f.isMirrored());
        assertFalse(h.isMirrored());

        h.addSequential(d);
        h.addParallel(e);
        testCommandGroup.addSequential(c);
        testCommandGroup.addParallel(h);
        f.addParallel(a);
        f.addSequential(b);
        f.addSequential(g);

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertTrue(d.isMirrored());
        assertTrue(e.isMirrored());
        assertTrue(f.isMirrored());
        assertTrue(h.isMirrored());

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertTrue(d.isMirrored());
        assertTrue(e.isMirrored());
        assertTrue(f.isMirrored());
        assertTrue(h.isMirrored());
    }
}