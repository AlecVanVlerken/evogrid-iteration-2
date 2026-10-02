package other;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import sim.*;
import sim.behaviors.*;
import sim.naturalselection.NaturalSelection;
import util.Orientation;
import util.Point;
import util.RandomUtil;

class AutomaticGenerationTest {
    private Chromosome chromosome(int gene) {
        int[] genes = new int[Constants.CHROM_SIZE];
        Arrays.fill(genes, gene);
        return new Chromosome(genes);
    }

    @Test
    void automaticSelectionIncludesMovementOnTheFinalTick() {
        int originalRate = Constants.MUT_RATE;
        try {
            Constants.MUT_RATE = 0;
            Creature moving = new Creature(new BehaviorB(chromosome(123)), new Point(1, 2), Orientation.east());
            World original = new World(8, 8, new Creature[] {moving,
                    new Creature(new ImmobileBehavior(chromosome(900)), new Point(0, 0), Orientation.north())});
            Simulation simulation = new Simulation(original, (world, point) -> point.getX() >= 3, 2);
            assertEquals(0, simulation.getEligibleParentCount());
            simulation.step();
            assertEquals(1, simulation.getTickCount());
            assertEquals(1, simulation.getGeneration());
            assertEquals(0, simulation.getEligibleParentCount());
            assertTrue(simulation.getLastCompletedGeneration().isEmpty());
            simulation.step();
            assertEquals(2, simulation.getGeneration());
            assertEquals(0, simulation.getTickCount());
            GenerationSummary result = simulation.getLastCompletedGeneration().orElseThrow();
            assertEquals(1, result.getGeneration());
            assertEquals(2, result.getEvaluationTicks());
            assertEquals(0, result.getInitialZoneOccupancy());
            assertEquals(1, result.getSelectedParentCount());
            for (Creature child : simulation.getWorld().getPopulation()) {
                assertInstanceOf(BehaviorB.class, child.getBehavior());
                assertTrue(child.getChromosome().isEqual(moving.getChromosome()));
            }
            assertEquals(simulation.getEligibleParentCount(), simulation.getInitialZoneOccupancy());
        } finally { Constants.MUT_RATE = originalRate; }
    }

    @Test
    void automaticAndEarlyManualTransitionsUseIdenticalReproduction() {
        Creature[] parents = {
                new Creature(new ImmobileBehavior(chromosome(100)), new Point(0, 2), Orientation.north()),
                new Creature(new ImmobileBehavior(chromosome(300)), new Point(1, 2), Orientation.east()),
                new Creature(new ImmobileBehavior(chromosome(900)), new Point(4, 4), Orientation.south())};
        World initial = new World(8, 8, parents);
        NaturalSelection selection = (world, point) -> point.getX() < 2;
        Simulation automatic = new Simulation(initial, selection, 2);
        Simulation manual = new Simulation(initial, selection, 3);
        RandomUtil.seed(4567);
        automatic.step();
        automatic.step();
        RandomUtil.seed(4567);
        manual.step();
        manual.step();
        assertEquals(1, manual.getGeneration());
        manual.nextGeneration();
        assertTrue(World.areEqualCreatureArrays(automatic.getWorld().getPopulation(), manual.getWorld().getPopulation()));
        GenerationSummary autoResult = automatic.getLastCompletedGeneration().orElseThrow();
        GenerationSummary manualResult = manual.getLastCompletedGeneration().orElseThrow();
        assertEquals(2, autoResult.getSelectedParentCount());
        assertEquals(autoResult.getGeneration(), manualResult.getGeneration());
        assertEquals(autoResult.getEvaluationTicks(), manualResult.getEvaluationTicks());
        assertEquals(autoResult.getInitialZoneOccupancy(), manualResult.getInitialZoneOccupancy());
        assertEquals(autoResult.getSelectedParentCount(), manualResult.getSelectedParentCount());
        assertEquals(0, automatic.getTickCount());
        assertEquals(0, manual.getTickCount());
    }

    @Test
    void repeatedAutomaticGenerationsResetAtExactlyTheFixedLength() {
        Simulation simulation = new Simulation(4, 0, (world, position) -> true, 3);
        for (int total = 1; total <= 10; total++) {
            simulation.step();
            assertEquals(1 + total / 3, simulation.getGeneration());
            assertEquals(total % 3, simulation.getTickCount());
            if (total >= 3) {
                GenerationSummary result = simulation.getLastCompletedGeneration().orElseThrow();
                assertEquals(total / 3, result.getGeneration());
                assertEquals(3, result.getEvaluationTicks());
                assertEquals(0, result.getInitialZoneOccupancy());
                assertEquals(0, result.getSelectedParentCount());
            }
        }
    }

    @Test
    void earlyAdvanceRecordsActualTicksThenStartsAFullEvaluationPeriod() {
        Simulation simulation = new Simulation(4, 0, (world, position) -> false, 3);
        simulation.step();
        simulation.nextGeneration();
        assertEquals(1, simulation.getLastCompletedGeneration().orElseThrow().getEvaluationTicks());
        simulation.step();
        simulation.step();
        assertEquals(2, simulation.getGeneration());
        assertEquals(2, simulation.getTickCount());
        simulation.step();
        assertEquals(3, simulation.getGeneration());
        assertEquals(3, simulation.getLastCompletedGeneration().orElseThrow().getEvaluationTicks());
    }

    @Test
    void evaluationDurationIsPositiveAndDefaultsToTheProjectConstant() {
        assertEquals(Constants.GENERATION_TICKS, new Simulation(4, 0, (w, p) -> true).getGenerationTicks());
        World empty = new World(4, 4, new Creature[0]);
        assertEquals(Constants.GENERATION_TICKS, new Simulation(empty, (w, p) -> true).getGenerationTicks());
        for (int duration : new int[] {0, -1}) {
            assertThrows(IllegalArgumentException.class, () -> new Simulation(4, 0, (w, p) -> true, duration));
            assertThrows(IllegalArgumentException.class, () -> new Simulation(empty, (w, p) -> true, duration));
        }
        Simulation immediate = new Simulation(empty, (w, p) -> true, 1);
        immediate.step();
        assertEquals(2, immediate.getGeneration());
        assertEquals(1, immediate.getLastCompletedGeneration().orElseThrow().getEvaluationTicks());
    }
}
