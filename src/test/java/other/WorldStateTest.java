package other;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import sim.Chromosome;
import sim.Constants;
import sim.Creature;
import sim.Simulation;
import sim.World;
import sim.behaviors.Behavior;
import sim.behaviors.BehaviorB;
import sim.behaviors.ImmobileBehavior;
import util.Orientation;
import util.Point;
import util.Vector;

class WorldStateTest {
    private Creature stationary(int x, int y) {
        return new Creature(new ImmobileBehavior(new Chromosome(new int[Constants.CHROM_SIZE])),
                new Point(x, y), Orientation.east());
    }

    @Test
    void rejectsMixedInvalidPopulations() {
        for (Point position : new Point[] {new Point(-1, 0), new Point(3, 0), new Point(0, 3)}) {
            Creature invalid = new Creature(stationary(0, 0).getBehavior(), position, Orientation.north());
            assertThrows(IllegalArgumentException.class,
                    () -> new World(3, 3, new Creature[] {stationary(1, 1), invalid}));
        }
        assertThrows(IllegalArgumentException.class,
                () -> new World(3, 3, new Creature[] {stationary(1, 1), null}));
    }

    @Test
    void supportsEmptyWorldsAndGenerations() {
        for (World world : new World[] {new World(0, 0, new Creature[0]),
                new World(0, 3, new Creature[0]), new World(3, 3, new Creature[0])}) {
            world.step();
            assertEquals(0, world.getPopulation().length);
        }
        Simulation simulation = new Simulation(3, 0, (world, position) -> true);
        simulation.nextGeneration();
        assertEquals(0, simulation.getWorld().getPopulation().length);
        assertTrue(simulation.getWorld().isFree(new Point(1, 1)));
    }

    @Test
    void rejectsDuplicatePlacement() {
        Creature creature = stationary(1, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new World(3, 3, new Creature[] {creature, creature}));
        assertThrows(IllegalArgumentException.class,
                () -> new World(3, 3, new Creature[] {creature, stationary(1, 1)}));
    }

    @Test
    void fillsCapacityWithoutOverlapAcrossReplacementGenerations() {
        for (boolean survivors : new boolean[] {false, true}) {
            Simulation simulation = new Simulation(3, 9, (world, position) -> survivors);
            for (int generation = 0; generation < 5; generation++) {
                World world = simulation.getWorld();
                assertEquals(9, world.getPopulation().length);
                assertEquals(9, Arrays.stream(world.getPopulation()).map(Creature::getPosition).distinct().count());
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        assertFalse(world.isFree(new Point(x, y)));
                    }
                }
                world.step();
                assertEquals(9, Arrays.stream(world.getPopulation()).map(Creature::getPosition).distinct().count());
                simulation.nextGeneration();
            }
        }
    }

    @Test
    void validatesCapacityAndInitializationInputs() {
        assertThrows(IllegalArgumentException.class, () -> new Simulation(2, 5, (w, p) -> true));
        assertThrows(IllegalArgumentException.class, () -> Simulation.createInitWorldNeuralnets(2, 5));
        assertThrows(IllegalArgumentException.class, () -> Simulation.createRandWorldWith(2, 5, new Behavior[5]));
        assertThrows(IllegalArgumentException.class, () -> new Simulation(0, 0, (w, p) -> true));
        assertThrows(IllegalArgumentException.class, () -> new Simulation(2, -1, (w, p) -> true));
        assertThrows(IllegalArgumentException.class, () -> new Simulation(2, 1, null));
        assertThrows(IllegalArgumentException.class, () -> Simulation.createRandWorldWith(2, 1, null));
        assertThrows(IllegalArgumentException.class, () -> Simulation.createRandWorldWith(2, 1, new Behavior[0]));
        assertThrows(IllegalArgumentException.class, () -> Simulation.createRandWorldWith(2, 1, new Behavior[1]));
    }

    @Test
    void sparsePlacementSupportsLargeCapacityAndPreservesBehaviorOrder() {
        Behavior[] behaviors = {stationary(0, 0).getBehavior(), stationary(0, 0).getBehavior()};
        World world = Simulation.createRandWorldWith(100000, 2, behaviors);
        Creature[] population = world.getPopulation();
        assertNotEquals(population[0].getPosition(), population[1].getPosition());
        for (int i = 0; i < behaviors.length; i++) {
            assertSame(behaviors[i], population[i].getBehavior());
            assertTrue(world.isInside(population[i].getPosition()));
        }
    }

    @Test
    void blockedMovementDoesNotOverwriteAnotherCreature() {
        Creature mover = new Creature(new BehaviorB(new Chromosome(new int[Constants.CHROM_SIZE])),
                new Point(2, 2), Orientation.east());
        World world = new World(5, 5, new Creature[] {mover, stationary(3, 2)});
        world.step();
        assertEquals(new Point(2, 2), world.getPopulation()[0].getPosition());
        assertEquals(new Point(3, 2), world.getPopulation()[1].getPosition());
        mover.moveForward(world, new Vector(10, 0));
        assertEquals(new Point(2, 2), mover.getPosition());
    }

    @Test
    void populationSnapshotsAndConstructorInputsHaveIndependentState() {
        Creature original = stationary(1, 1);
        Creature[] input = {original};
        World world = new World(4, 4, input);
        original.moveForward(world, new Vector(0, 0));
        original.turnClockwise();
        input[0] = null;
        Creature[] snapshot = world.getPopulation();
        snapshot[0].moveForward(world, new Vector(0, 0));
        snapshot[0].turnCounterclockwise();
        snapshot[0] = null;
        assertEquals(new Point(1, 1), world.getPopulation()[0].getPosition());
        assertSame(Orientation.east(), world.getPopulation()[0].getOrientation());
        assertFalse(world.isFree(new Point(1, 1)));
        assertTrue(world.isFree(new Point(2, 1)));
    }

    @Test
    void chromosomeCopiesConstructorGenes() {
        int[] genes = new int[Constants.CHROM_SIZE];
        Chromosome chromosome = new Chromosome(genes);
        Arrays.fill(genes, Constants.GENE_MAX);
        for (int i = 0; i < genes.length; i++) {
            assertEquals(0, chromosome.getGene(i));
        }
    }

    @Test
    void directionCatalogCannotBeChangedByCallers() {
        var directions = Orientation.orientations();
        assertThrows(UnsupportedOperationException.class, () -> directions.set(0, Orientation.south()));
        assertThrows(UnsupportedOperationException.class, () -> directions.clear());
        assertSame(Orientation.north(), Orientation.orientations().get(0));
        assertSame(Orientation.northEast(), Orientation.north().turnClockwise(1));
    }

    @Test
    void equalCoordinatesWorkInHashCollections() {
        var points = new HashSet<Point>();
        points.add(new Point(-3, 7));
        assertTrue(points.contains(new Point(-3, 7)));
        assertFalse(points.add(new Point(-3, 7)));
        var vectors = new HashSet<Vector>();
        vectors.add(new Vector(4, -2));
        assertTrue(vectors.contains(new Vector(4, -2)));
        assertFalse(vectors.add(new Vector(4, -2)));
    }
}
