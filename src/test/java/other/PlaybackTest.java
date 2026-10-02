package other;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import sim.*;
import sim.behaviors.BehaviorB;
import simpleui.Movie;
import util.Orientation;
import util.Point;
import util.RandomUtil;

class PlaybackTest {
    private Movie movie;
    private Timer checkTimer;

    private Simulation simulation() {
        return new Simulation(new World(8, 8, new Creature[] {
                new Creature(new BehaviorB(new Chromosome(new int[Constants.CHROM_SIZE])),
                        new Point(2, 2), Orientation.east())}), (world, position) -> position.getX() >= 3);
    }

    @AfterEach
    void stopPlayback() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (checkTimer != null) { checkTimer.stop(); }
            if (movie != null) { movie.setPaused(true); movie.removeNotify(); }
        });
    }

    @Test
    void repeatedPaintingChangesNeitherModelNorRandomSequence() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            movie = new Movie(60, simulation());
            movie.setPaused(true);
            movie.setSize(movie.getPreferredSize());
            Creature[] before = movie.getSim().getWorld().getPopulation();
            RandomUtil.seed(4567);
            int expected = RandomUtil.integer();
            RandomUtil.seed(4567);
            BufferedImage image = new BufferedImage(movie.getWidth(), movie.getHeight(), BufferedImage.TYPE_INT_ARGB);
            var graphics = image.createGraphics();
            try {
                for (int i = 0; i < 20; i++) { movie.paint(graphics); }
            } finally { graphics.dispose(); }
            assertEquals(expected, RandomUtil.integer());
            assertTrue(World.areEqualCreatureArrays(before, movie.getSim().getWorld().getPopulation()));
            assertEquals(1, movie.getSim().getGeneration());
            assertEquals(0, movie.getSim().getTickCount());
            assertEquals(0, movie.getSim().getEligibleParentCount());
            assertTrue(movie.getSim().getLastCompletedGeneration().isEmpty());
            movie.setPaused(false);
            RandomUtil.seed(4567);
            graphics = image.createGraphics();
            try {
                for (int i = 0; i < 20; i++) { movie.paint(graphics); }
            } finally { graphics.dispose(); }
            assertEquals(expected, RandomUtil.integer());
            assertEquals(0, movie.getSim().getTickCount());
            assertTrue(World.areEqualCreatureArrays(before, movie.getSim().getWorld().getPopulation()));
        });
    }

    @Test
    void timerRunsWithoutPaintingPauseStopsItAndResumeContinues() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            movie = new Movie(60, simulation());
            movie.addNotify();
        });
        CountDownLatch advanced = new CountDownLatch(1);
        SwingUtilities.invokeAndWait(() -> {
            Timer check = new Timer(10, null);
            checkTimer = check;
            check.addActionListener(event -> {
                if (movie.getSim().getTickCount() >= 2) {
                    movie.setPaused(true);
                    check.stop();
                    advanced.countDown();
                }
            });
            check.setRepeats(true);
            check.start();
        });
        assertTrue(advanced.await(2, TimeUnit.SECONDS), "Timer did not advance evaluation");
        long[] pausedTick = new long[1];
        SwingUtilities.invokeAndWait(() -> pausedTick[0] = movie.getSim().getTickCount());
        waitForEventInterval();
        SwingUtilities.invokeAndWait(() -> {
            assertEquals(pausedTick[0], movie.getSim().getTickCount());
            assertTrue(movie.isPaused());
            movie.setPaused(false);
        });
        waitForEventInterval();
        SwingUtilities.invokeAndWait(() -> {
            assertFalse(movie.isPaused());
            assertTrue(movie.getSim().getTickCount() > pausedTick[0]);
            movie.removeNotify();
        });
        long[] removedTick = new long[1];
        SwingUtilities.invokeAndWait(() -> removedTick[0] = movie.getSim().getTickCount());
        waitForEventInterval();
        SwingUtilities.invokeAndWait(() -> assertEquals(removedTick[0], movie.getSim().getTickCount()));
    }

    private void waitForEventInterval() throws Exception {
        CountDownLatch elapsed = new CountDownLatch(1);
        SwingUtilities.invokeAndWait(() -> {
            Timer wait = new Timer(150, event -> elapsed.countDown());
            wait.setRepeats(false);
            wait.start();
        });
        assertTrue(elapsed.await(2, TimeUnit.SECONDS));
    }

    @Test
    void manualAdvanceSelectsCurrentPositionsAndRetainsPlaybackState() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            movie = new Movie(60, simulation());
            movie.setPaused(true);
            assertEquals(0, movie.getSim().getEligibleParentCount());
            movie.getSim().step();
            assertEquals(1, movie.getSim().getEligibleParentCount());
            movie.advanceGeneration();
            assertTrue(movie.isPaused());
            var summary = movie.getSim().getLastCompletedGeneration().orElseThrow();
            assertEquals(0, summary.getInitialZoneOccupancy());
            assertEquals(1, summary.getSelectedParentCount());
            assertEquals(1, summary.getEvaluationTicks());
            assertEquals(0, movie.getSim().getTickCount());
            movie.setPaused(false);
            movie.advanceGeneration();
            assertFalse(movie.isPaused());
            assertEquals(3, movie.getSim().getGeneration());
        });
    }

    @Test
    void runningTimerCompletesGenerationsWithoutManualAdvance() throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        SwingUtilities.invokeAndWait(() -> {
            Simulation automatic = new Simulation(4, 0, (world, position) -> true, 3);
            movie = new Movie(60, automatic);
            assertFalse(movie.isPaused());
            movie.addNotify();
            checkTimer = new Timer(10, event -> {
                if (automatic.getGeneration() >= 2) {
                    movie.setPaused(true);
                    assertEquals(3, automatic.getLastCompletedGeneration().orElseThrow().getEvaluationTicks());
                    checkTimer.stop();
                    completed.countDown();
                }
            });
            checkTimer.start();
        });
        assertTrue(completed.await(2, TimeUnit.SECONDS), "Playback did not complete a generation");
        SwingUtilities.invokeAndWait(() -> {
            assertTrue(movie.isPaused());
            assertTrue(movie.getSim().getTickCount() < movie.getSim().getGenerationTicks());
        });
    }

    @Test
    void restartClearsProgressAndResumesWithFreshValidPopulation() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            movie = new Movie(Constants.DEFAULT_FRAME_RATE, simulation());
            movie.setPaused(true);
            Simulation sim = movie.getSim();
            sim.step();
            movie.advanceGeneration();
            sim.step();
            World previousWorld = sim.getWorld();
            var selection = sim.getNaturalSelection();
            int duration = sim.getGenerationTicks();
            movie.restart();
            assertFalse(movie.isPaused());
            assertEquals(1, sim.getGeneration());
            assertEquals(0, sim.getTickCount());
            assertTrue(sim.getLastCompletedGeneration().isEmpty());
            assertNotSame(previousWorld, sim.getWorld());
            assertSame(selection, sim.getNaturalSelection());
            assertEquals(duration, sim.getGenerationTicks());
            assertEquals(8, sim.getWorld().getWidth());
            assertEquals(1, sim.getWorld().getPopulation().length);
            assertEquals(sim.getEligibleParentCount(), sim.getInitialZoneOccupancy());
            for (Creature creature : sim.getWorld().getPopulation()) {
                assertTrue(sim.getWorld().isInside(creature.getPosition()));
                assertInstanceOf(sim.behaviors.NeuralNetworkBehavior.class, creature.getBehavior());
            }
        });
    }
}
