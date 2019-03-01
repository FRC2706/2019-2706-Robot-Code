package ca.team2706.frc.robot;

import mockit.Tested;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PairTest {

    @Tested
    private Pair<String> pair;

    @Before
    public void setUp() {
        pair = Pair.of("A", "B");
    }

    /**
     * Tests that the pair works
     */
    @Test
    public void testPair() {
        assertEquals("A", pair.getFirst());
        assertEquals("B", pair.getSecond());
    }

}