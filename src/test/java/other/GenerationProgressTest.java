package other;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sim.*;
import sim.behaviors.*;
import util.Orientation;
import util.Point;
import util.RandomUtil;

class GenerationProgressTest {
    private int originalMutationRate;

    @BeforeEach
    void setUp() {
        originalMutationRate = Constants.MUT_RATE;
        Constants.MUT_RATE = 0;
        RandomUtil.seed(1234);
    }

    @AfterEach
    void restoreMutationRate() {
        Constants.MUT_RATE = originalMutationRate;
    }

    private Chromosome chromosome(int value) {
        int[] genes = new int[Constants.CHROM_SIZE];
        Arrays.fill(genes, value);
        return new Chromosome(genes);
    }

    private Creature stationary(int x, int y, int gene) {
        return new Creature(new ImmobileBehavior(chromosome(gene)), new Point(x, y), Orientation.east());
    }

    private int[] genes(Chromosome chromosome) {
        int[] result = new int[Constants.CHROM_SIZE];
        for (int i = 0; i < result.length; i++) { result[i] = chromosome.getGene(i); }
        return result;
    }

    @Test
    void endpointSummarySeparatesInitialOccupancyFromCurrentEligibility() {
        Creature moving = new Creature(new BehaviorB(chromosome(0)), new Point(2, 2), Orientation.east());
        Simulation simulation = new Simulation(new World(8, 8,
                new Creature[] {moving, stationary(4, 4, 100)}), (w, p) -> p.getX() >= 4);
        assertEquals(1, simulation.getGeneration());
        assertEquals(0, simulation.getTickCount());
        assertEquals(1, simulation.getInitialZoneOccupancy());
        assertEquals(1, simulation.getEligibleParentCount());
        assertTrue(simulation.getLastCompletedGeneration().isEmpty());
        simulation.step();
        assertEquals(1, simulation.getEligibleParentCount());
        simulation.step();
        assertEquals(2, simulation.getEligibleParentCount());
        assertEquals(2, simulation.getTickCount());
        assertEquals(1, simulation.getInitialZoneOccupancy());
        simulation.nextGeneration();
        GenerationSummary first = simulation.getLastCompletedGeneration().orElseThrow();
        assertEquals(1, first.getGeneration());
        assertEquals(2, first.getEvaluationTicks());
        assertEquals(1, first.getInitialZoneOccupancy());
        assertEquals(2, first.getSelectedParentCount());
        assertEquals(2, simulation.getGeneration());
        assertEquals(0, simulation.getTickCount());
        assertEquals(simulation.getEligibleParentCount(), simulation.getInitialZoneOccupancy());
        int initial = simulation.getInitialZoneOccupancy();
        for (int tick = 0; tick < 7; tick++) { simulation.step(); }
        int selected = simulation.getEligibleParentCount();
        simulation.nextGeneration();
        GenerationSummary second = simulation.getLastCompletedGeneration().orElseThrow();
        assertEquals(2, second.getGeneration());
        assertEquals(7, second.getEvaluationTicks());
        assertEquals(initial, second.getInitialZoneOccupancy());
        assertEquals(selected, second.getSelectedParentCount());
        assertEquals(2, first.getEvaluationTicks());
        assertEquals(1, first.getGeneration());
        assertEquals(3, simulation.getGeneration());
        assertEquals(0, simulation.getTickCount());
    }

    @Test
    void onlyEligibleParentsSupplyWholeInheritedBlocks() {
        Creature[] parents = new Creature[12];
        parents[0] = stationary(0, 2, 100);
        parents[1] = stationary(1, 2, 300);
        for (int i = 2; i < parents.length; i++) {
            parents[i] = stationary(2 + i % 6, i / 6, 900);
        }
        Simulation simulation = new Simulation(new World(10, 10, parents), (w, p) -> p.getX() < 2);
        simulation.nextGeneration();
        assertEquals(2, simulation.getLastCompletedGeneration().orElseThrow().getSelectedParentCount());
        assertEquals(parents.length, simulation.getWorld().getPopulation().length);
        boolean sawFirst = false;
        boolean sawSecond = false;
        for (Creature offspring : simulation.getWorld().getPopulation()) {
            assertInstanceOf(ImmobileBehavior.class, offspring.getBehavior());
            for (int block = 0; block < 4; block++) {
                int inherited = offspring.getChromosome().getGene(block * 6);
                assertTrue(inherited == 100 || inherited == 300);
                sawFirst |= inherited == 100;
                sawSecond |= inherited == 300;
                for (int offset = 0; offset < 6; offset++) {
                    assertEquals(inherited, offspring.getChromosome().getGene(block * 6 + offset));
                }
            }
        }
        assertTrue(sawFirst && sawSecond);
        assertArrayEquals(genes(chromosome(100)), genes(parents[0].getChromosome()));
        assertArrayEquals(genes(chromosome(300)), genes(parents[1].getChromosome()));
        assertFreshPlacement(parents, simulation.getWorld());
    }

    private void assertFreshPlacement(Creature[] parents, World world) {
        Creature[] children = world.getPopulation();
        assertEquals(children.length, Arrays.stream(children).map(Creature::getPosition).distinct().count());
        boolean changed = false;
        for (int i = 0; i < children.length; i++) {
            assertTrue(world.isInside(children[i].getPosition()));
            changed |= !parents[i].getPosition().equals(children[i].getPosition());
        }
        assertTrue(changed);
    }

    @Test
    void singleParentSuppliesEveryGeneAtZeroMutation() {
        Creature[] parents = {stationary(0, 0, 123), stationary(2, 2, 900), stationary(3, 3, 900)};
        Simulation simulation = new Simulation(new World(8, 8, parents), (w, p) -> p.getX() == 0);
        simulation.nextGeneration();
        assertEquals(1, simulation.getLastCompletedGeneration().orElseThrow().getSelectedParentCount());
        for (Creature child : simulation.getWorld().getPopulation()) {
            assertTrue(child.getChromosome().isEqual(parents[0].getChromosome()));
            assertNotSame(parents[0].getChromosome(), child.getChromosome());
        }
        assertFreshPlacement(parents, simulation.getWorld());
    }

    @Test
    void zeroParentsReinitializeInsteadOfInheritingIneligibleGenes() {
        Creature[] parents = {stationary(2, 2, 900), stationary(3, 3, 900)};
        Simulation simulation = new Simulation(new World(8, 8, parents), (w, p) -> false);
        simulation.nextGeneration();
        assertEquals(0, simulation.getLastCompletedGeneration().orElseThrow().getSelectedParentCount());
        assertEquals(0, simulation.getLastCompletedGeneration().orElseThrow().getEvaluationTicks());
        for (Creature child : simulation.getWorld().getPopulation()) {
            assertInstanceOf(NeuralNetworkBehavior.class, child.getBehavior());
            assertFalse(child.getChromosome().isEqual(parents[0].getChromosome()));
            assertTrue(Arrays.stream(genes(child.getChromosome())).allMatch(Chromosome::isValidGene));
        }
        assertFreshPlacement(parents, simulation.getWorld());
    }

    @Test
    void emptyGenerationsStillRecordTransitionsAndResetTicks() {
        Simulation simulation = new Simulation(3, 0, (w, p) -> true);
        simulation.step();
        simulation.nextGeneration();
        var summary = simulation.getLastCompletedGeneration().orElseThrow();
        assertEquals(1, summary.getEvaluationTicks());
        assertEquals(0, summary.getInitialZoneOccupancy());
        assertEquals(0, summary.getSelectedParentCount());
        assertEquals(2, simulation.getGeneration());
        assertEquals(0, simulation.getTickCount());
        assertEquals(0, simulation.getEligibleParentCount());
    }

    @Test
    void mutationProbabilityExtremesRespectInheritance() {
        for (int probability : new int[] {0, 100}) {
            Constants.MUT_RATE = probability;
            RandomUtil.seed(1234);
            Creature[] parents = new Creature[30];
            for (int i = 0; i < parents.length; i++) { parents[i] = stationary(i % 8, i / 8, 0); }
            Simulation simulation = new Simulation(new World(8, 8, parents), (w, p) -> true);
            int[][] expected = new int[parents.length][Constants.CHROM_SIZE];
            // Replay the existing seeded draws, including the probability draw at both endpoints.
            for (int child = 0; child < parents.length; child++) {
                RandomUtil.integer(parents.length);
                RandomUtil.integer(parents.length);
                for (int block = 0; block < 4; block++) { RandomUtil.bool(); }
                RandomUtil.integer(100);
                if (probability == 100) {
                    int index = RandomUtil.integer(Constants.CHROM_SIZE);
                    expected[child][index] = RandomUtil.integer(-Constants.GENE_DELTA, Constants.GENE_DELTA);
                }
            }
            RandomUtil.seed(1234);
            simulation.nextGeneration();
            int mutated = 0;
            Creature[] children = simulation.getWorld().getPopulation();
            for (int childIndex = 0; childIndex < children.length; childIndex++) {
                Creature child = children[childIndex];
                assertArrayEquals(expected[childIndex], genes(child.getChromosome()));
                int differences = 0;
                for (int gene : genes(child.getChromosome())) {
                    if (gene != 0) {
                        differences++;
                        assertTrue(gene >= -Constants.GENE_DELTA && gene < Constants.GENE_DELTA);
                    }
                }
                assertTrue(differences <= 1);
                if (differences > 0) { mutated++; }
            }
            if (probability == 0) { assertEquals(0, mutated); }
            else { assertTrue(mutated > 0); }
            for (Creature parent : parents) { assertTrue(parent.getChromosome().isEqual(chromosome(0))); }
        }
    }

    @Test
    void explicitInitialPopulationHasIndependentCreatureState() {
        World supplied = new World(8, 8, new Creature[] {
                new Creature(new BehaviorB(chromosome(0)), new Point(2, 2), Orientation.east())});
        Simulation simulation = new Simulation(supplied, (w, p) -> p.getX() >= 3);
        supplied.step();
        assertEquals(new Point(2, 2), simulation.getWorld().getPopulation()[0].getPosition());
        assertEquals(0, simulation.getInitialZoneOccupancy());
        assertThrows(IllegalArgumentException.class, () -> new Simulation(null, (w, p) -> true));
        assertThrows(IllegalArgumentException.class, () -> new Simulation(supplied, null));
        assertThrows(IllegalArgumentException.class,
                () -> new Simulation(new World(0, 0, new Creature[0]), (w, p) -> true));
        assertThrows(IllegalArgumentException.class,
                () -> new Simulation(new World(3, 4, new Creature[0]), (w, p) -> true));
    }

    @Test
    void crossoverUsesFourWholeBlocksAndPreservesBothParents() {
        int[] left = new int[24];
        int[] right = new int[24];
        for (int i = 0; i < 24; i++) { left[i] = i + 1; right[i] = -i - 1; }
        Chromosome first = new Chromosome(left);
        Chromosome second = new Chromosome(right);
        boolean mixed = false;
        for (int seed = 0; seed < 20; seed++) {
            RandomUtil.seed(seed);
            boolean[] fromLeft = new boolean[4];
            for (int block = 0; block < 4; block++) { fromLeft[block] = RandomUtil.bool(); }
            RandomUtil.seed(seed);
            Chromosome child = first.crossover2(second);
            for (int block = 0; block < 4; block++) {
                mixed |= fromLeft[block] != fromLeft[0];
                for (int offset = 0; offset < 6; offset++) {
                    int index = block * 6 + offset;
                    assertEquals(fromLeft[block] ? left[index] : right[index], child.getGene(index));
                }
            }
        }
        assertTrue(mixed);
        assertArrayEquals(left, genes(first));
        assertArrayEquals(right, genes(second));
    }

    @Test
    void requestedMutationChangesOnlyItsGeneAndPreservesParent() {
        Chromosome parent = chromosome(100);
        for (int index = 0; index < Constants.CHROM_SIZE; index++) {
            for (int delta : new int[] {-200, 0, 200}) {
                Chromosome child = parent.mutate(index, delta);
                for (int gene = 0; gene < Constants.CHROM_SIZE; gene++) {
                    assertEquals(gene == index ? 100 + delta : 100, child.getGene(gene));
                }
            }
        }
        assertArrayEquals(genes(chromosome(100)), genes(parent));
    }

    @Test
    void outOfRangeMutationsLeaveGenesUnchanged() {
        Chromosome maximum = chromosome(Constants.GENE_MAX);
        Chromosome minimum = chromosome(Constants.GENE_MIN);
        for (int index = 0; index < Constants.CHROM_SIZE; index++) {
            assertTrue(maximum.isEqual(maximum.mutate(index, 1)));
            assertTrue(minimum.isEqual(minimum.mutate(index, -1)));
            assertTrue(maximum.isEqual(maximum.mutate(index, Integer.MAX_VALUE)));
            assertTrue(minimum.isEqual(minimum.mutate(index, Integer.MIN_VALUE)));
        }
    }

    @Test
    void randomMutationUsesRequestedIndexAndDelta() {
        Chromosome parent = chromosome(0);
        for (int seed = 0; seed < 20; seed++) {
            RandomUtil.seed(seed);
            int index = RandomUtil.integer(Constants.CHROM_SIZE);
            int delta = RandomUtil.integer(-Constants.GENE_DELTA, Constants.GENE_DELTA);
            RandomUtil.seed(seed);
            Chromosome child = parent.randomlyMutate();
            for (int gene = 0; gene < Constants.CHROM_SIZE; gene++) {
                assertEquals(gene == index ? delta : 0, child.getGene(gene));
            }
        }
        assertTrue(parent.isEqual(chromosome(0)));
    }
}
