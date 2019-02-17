package ca.team2706.frc.robot.commands.mirrorable;

import edu.wpi.first.wpilibj.command.CommandGroup;
import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MirrorableTest {

    @Tested
    private MirroredCommand a, b, c, d, e;

    @Tested
    private MirroredCommandGroup f, g, h;

    @Before
    public void setUp() {
        a = newMirroredCommand();
        b = newMirroredCommand();
        c = newMirroredCommand();
        d = newMirroredCommand();
        e = newMirroredCommand();

        f = new MirroredCommandGroup();
        g = new MirroredCommandGroup();
        h = new MirroredCommandGroup();
    }

    private MirroredCommand newMirroredCommand() {
        return new MirroredCommand() {
            @Override
            protected boolean isFinished() {
                return false;
            }
        };
    }

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
        h.addMirroredParallel(e);
        g.addMirroredSequential(c);
        g.addMirroredParallel(h);
        f.addMirroredParallel(a);
        f.addMirroredSequential(b);
        f.addMirroredSequential(g);

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertFalse(d.isMirrored());
        assertTrue(e.isMirrored());
        assertTrue(f.isMirrored());
        assertTrue(g.isMirrored());
        assertTrue(h.isMirrored());

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertTrue(c.isMirrored());
        assertFalse(d.isMirrored());
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

        h.addMirroredSequential(d);
        h.addMirroredParallel(e);
        testCommandGroup.addSequential(c);
        testCommandGroup.addParallel(h);
        f.addMirroredParallel(a);
        f.addMirroredSequential(b);
        f.addMirroredSequential(g);

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertFalse(c.isMirrored());
        assertFalse(d.isMirrored());
        assertFalse(e.isMirrored());
        assertTrue(f.isMirrored());
        assertFalse(h.isMirrored());

        assertTrue(f.mirror().isMirrored());

        assertTrue(a.isMirrored());
        assertTrue(b.isMirrored());
        assertFalse(c.isMirrored());
        assertFalse(d.isMirrored());
        assertFalse(e.isMirrored());
        assertTrue(f.isMirrored());
        assertFalse(h.isMirrored());
    }
}