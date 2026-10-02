package other;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import sim.behaviors.Behavior;
import sim.behaviors.BehaviorA;
import sim.behaviors.BehaviorB;
import sim.behaviors.ImmobileBehavior;
import sim.behaviors.NeuralNetworkBehavior;
import sim.naturalselection.BorderHabitableZone;
import sim.naturalselection.CircularHabitableZone;
import sim.naturalselection.Disjunction;
import sim.neuralnet.ActivationFunctionNeuron;
import sim.neuralnet.FreePassageSensorNeuron;
import sim.neuralnet.LinearFunctionNeuron;
import sim.neuralnet.Neuron;
import sim.Chromosome;
import sim.Constants;
import sim.Creature;
import sim.Simulation;
import sim.World;
import simpleui.Movie;
import util.Color;
import util.FrameRateTimer;
import util.Orientation;
import util.Pair;
import util.Point;
import util.Chronometer;
import util.RandomUtil;


/**
 * Our flaw detecting unit tests are marked as such with
 * a FLAW comment under the `@Test` annotation.
 * Then it is followed by a description of the correct, expected behavior
 * 
 * There are also tests that are not marked with FLAW
 */
class CorrectionTestSuite {
	
	
	
	/**
	 * @pre | n < width * height
	 * @pre | 0 < n
	 * gives n distinct Point positions within width, height.
	 * if beg is true, result gives "the first" n positions (at the top of the field)
	 * Else, result gives "the last" n positions (at the bot. of the field)
	 */
	static Point[] rows(int n, int width, int height, boolean beg) {
		Point[] res = new Point[n];
		if (beg) {
			for (int i = 0 ; i < n ; i++) {
				res[i] = new Point(i % width, i / width);
			}
		}
		else {
			for (int i = 0 ; i < n ; i ++) {
				res[i] = new Point((width - i - 1) % width, height - (i / width) - 1);
			}
		}

		return res;
	}

	
	@Nested
	class Defensive {
		
		@Test
		void pairDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new Pair<>(null, 1));
			assertThrows(IllegalArgumentException.class, () ->
				new Pair<>(1, null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void borderHabitableZoneDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new BorderHabitableZone(-10));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void circularHabitableZoneDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new CircularHabitableZone(null, 5));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void disjunctionDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new Disjunction(null, new BorderHabitableZone(10)));
			assertThrows(IllegalArgumentException.class, () ->
				new Disjunction(new BorderHabitableZone(10), null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void BehaviorADefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new BehaviorA(null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void BehaviorBDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new BehaviorB(null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void immobileBehaviorDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new ImmobileBehavior(null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void neuralNetworkBehaviorDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new NeuralNetworkBehavior(null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void freePassageSensorNeuronDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new FreePassageSensorNeuron(null));
		}
		
		
		@Test
		/**
		 * FLAW
		 * 
		 * No defensive programming.
		 */
		void worldDefensive() {
			Creature[] creatures1 = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north()), null};
			Creature[] creatures2 = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			Creature[] creatures3 = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(100,100), Orientation.north())};
			Creature[] creatures4 = {};
			
			assertThrows(IllegalArgumentException.class, () ->
				new World(50, 50, null));
			assertThrows(IllegalArgumentException.class, () ->
				new World(50, 50, creatures1));
			assertThrows(IllegalArgumentException.class, () ->
				new World(50, 50, creatures3));
			assertThrows(IllegalArgumentException.class, () ->
				new World(50, -50, creatures2));
			assertThrows(IllegalArgumentException.class, () ->
				new World(-50, 50, creatures2));
			assertThrows(IllegalArgumentException.class, () ->
				new World(-50, 50, creatures4));
		}
		
				
		@Test
		void creatureDefensive() {
			assertThrows(IllegalArgumentException.class, () ->
				new Creature(null, new Point(1,1), Orientation.north()));
			assertThrows(IllegalArgumentException.class, () ->
				new Creature(new BehaviorA(Chromosome.createRandom()), null, Orientation.north()));
			assertThrows(IllegalArgumentException.class, () ->
				new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), null));
		}
		
		
		@Test
		void chromosomeDefensive() {
			if (Constants.CHROM_SIZE != 0) {
				int[] weights1 = new int[Constants.CHROM_SIZE - 1];
				for (int i = 0; i < Constants.CHROM_SIZE - 1; i++) {
					weights1[i] = Constants.GENE_MAX;
				}
				assertThrows(IllegalArgumentException.class, () ->
					new Chromosome(weights1));
			}
			
			int[] weights2 = new int[Constants.CHROM_SIZE];
			for (int i = 0; i < Constants.CHROM_SIZE; i++) {
				weights2[i] = Constants.GENE_MAX + 1;
			}
			assertThrows(IllegalArgumentException.class, () ->
				new Chromosome(weights2));
			
			assertThrows(IllegalArgumentException.class, () ->
				new Chromosome(null));
			
		}	
	}
	
	@Nested
	class PairReferenceContracts {
		
		Pair<Creature, Creature> pair;
		Creature first;
		Creature second;		
		
		@BeforeEach
		void setup() {
			first = new Creature(new BehaviorA(Chromosome.createRandom()), new Point(Constants.WSIZE / 2, Constants.WSIZE / 2), Orientation.north());
			second = new Creature(new BehaviorA(Chromosome.createRandom()), new Point(Constants.WSIZE / 2, Constants.WSIZE / 2), Orientation.north());
			pair = new Pair<Creature, Creature>(first, second);
		}
		
        @Test
        void constructorKeepsSuppliedCreatureReferences() {
            assertSame(first, pair.getFirst());
            assertSame(second, pair.getSecond());
        }

        @Test
        void gettersShareTheSuppliedCreatureReferences() {
            pair.getFirst().turnClockwise();
            pair.getSecond().turnCounterclockwise();
            assertTrue(first.getOrientation().isEqual(Orientation.northEast()));
            assertTrue(second.getOrientation().isEqual(Orientation.northWest()));
            assertSame(first, pair.getFirst());
            assertSame(second, pair.getSecond());
        }
    }

	@Nested
	class BorderHabitableZoneEncapsulation {
		
		BorderHabitableZone zone;
		int borderSize;		
		
		@BeforeEach
		void setup() {
			borderSize = 10;
			zone = new BorderHabitableZone(borderSize);
		}
		
		@Test
		void constructorCapturesBorderSize() {
			int other = 20;
			
			borderSize = other;
			
			assertEquals(10, zone.getBorderSize());
		}
	}
	
	
	@Nested
	class CircularHabitableZoneEncapsulation {
		
		CircularHabitableZone zone;
		Point center;
		int radius;
		
		@BeforeEach
		void setup() {
			center = new Point(25, 25);
			radius = 5;
			zone = new CircularHabitableZone(center, radius);
		}
		
		@Test
		void constructorCapturesCenterAndRadius() {
			Point originalCenter = center;
            int other1 = 10;
			Point other2 = new Point(20, 20);
			
			center = other2;
			radius = other1;
			
			assertEquals(25, zone.getRadiusSquared());
			assertSame(originalCenter, zone.getCenter());
		}
	}
	
	
	@Nested
	class ActivationFunctionNeuronEncapsulation {
		
		ActivationFunctionNeuron neuron;
		ArrayList<Pair<Neuron, Integer>> dependencies;
		
		@BeforeEach
		void setup() {
			neuron = new LinearFunctionNeuron();
			dependencies =	new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				dependencies.add(new Pair<>(neuron, 500));
				}
			neuron.setDependencies(dependencies);
		}
		
		@Test
		void activationFunctionNeuronEncapsIn() {		
			ArrayList<Pair<Neuron, Integer>> other = neuron.getDependencies();

            other.get(0).setSecond(100);
            other.clear();
            dependencies.get(0).setSecond(200);
            dependencies.clear();
            assertEquals(7, neuron.getDependencies().size());
            assertEquals(500, neuron.getDependencies().get(0).getSecond());
		}
	}
	
	
	@Nested
	class CreatureEncapsulation {
		
		Creature creature;
		BehaviorA behavior;		
		
		@BeforeEach
		void setup() {
			behavior = new BehaviorA(Chromosome.createRandom());
			creature = new Creature(behavior, new Point(Constants.WSIZE / 2, Constants.WSIZE / 2), Orientation.north());
		}
		
        @Test
        void chromosomeGetterSharesAnImmutableInheritedValue() {
            Chromosome original = creature.getChromosome();
            int before = original.getGene(0);
            Chromosome changed = original.mutate(0, before == Constants.GENE_MAX ? -1 : 1);
            assertFalse(original.isEqual(changed));
            assertEquals(before, creature.getChromosome().getGene(0));
            assertSame(behavior.getChromosome(), creature.getChromosome());
        }
    }

	@Nested
	/**
	 *  FLAW 
	 * 
	 *	population array ref encaps. by getter
	 */
	class WorldEncapsulation {
		
		Creature creature;
		BehaviorA behavior;	
		Creature[] pop;
		World world;
		
		@BeforeEach
		void setup() {
			pop = new Creature[Constants.POPU_SIZE];
			Point[] pos = rows(pop.length, Constants.WSIZE, Constants.WSIZE, true);
			for (int i = 0 ; i < pop.length ; i++) {
				pop[i] = new Creature(new BehaviorA(Chromosome.createRandom()), pos[i], Orientation.north());
			}
			
			world = new World(Constants.WSIZE, Constants.WSIZE, pop);
		}
		
		@Test
		void WorldEncapsOut() {		
			Creature[] pA = world.getPopulation();
			pA[0] = new Creature(new BehaviorA(Chromosome.createRandom()), new Point(Constants.WSIZE / 3, Constants.WSIZE / 2), Orientation.east());
			
			assertNotEquals(pA[0].getOrientation(), world.getPopulation()[0].getOrientation());
		}
	}
	
	
	@Nested
	/**
	 * FLAW
	 * 
	 * If fails should return false.
	 */
	class More {
		
		ActivationFunctionNeuron neuron;
		
		@BeforeEach
		void setup() {
			neuron = new LinearFunctionNeuron();
		}
		
		@Test
		public void testConnect()
		{							
			ArrayList<Pair<Neuron, Integer>> dependenciesFull =	new ArrayList<>();
			ArrayList<Pair<Neuron, Integer>> dependenciesAlmost = new ArrayList<>();
			
			for (int i = 0; i < 7; i++) {
				dependenciesFull.add(new Pair<>(neuron, 500));
				
				if (i < 6) {
					dependenciesAlmost.add(new Pair<>(neuron, 500));
				}
			}

			assertTrue(neuron.connect(neuron, 400));
			assertFalse(dependenciesFull.equals(neuron.getDependencies()));
			
			neuron.setDependencies(dependenciesFull);
			
			assertFalse(neuron.connect(neuron, 500));
			assertEquals(dependenciesFull.size(), neuron.getDependencies().size());
            for (int i = 0; i < dependenciesFull.size(); i++) {
                assertEquals(dependenciesFull.get(i).getFirst(), neuron.getDependencies().get(i).getFirst());
                assertEquals(dependenciesFull.get(i).getSecond(), neuron.getDependencies().get(i).getSecond());
            }
		}
		
		@Test
		/**
		 * FLAW
		 * 
		 * Should fail silently if conditions don't hold
		 */
		public void testDoubleSensor()
		{							
			ArrayList<Pair<Neuron, Integer>> dependenciesFull =	new ArrayList<>();
			ArrayList<Pair<Neuron, Integer>> dependenciesAlmost = new ArrayList<>();
			var neuron1 = new LinearFunctionNeuron();
			
			for (int i = 0; i < 7; i++) {
				if (i == 0) {
					dependenciesFull.add(new Pair<>(neuron, 400));
				} else {
					dependenciesFull.add(new Pair<>(neuron, 500));
				}
				
				if (i < 6) {
					dependenciesAlmost.add(new Pair<>(neuron, i == 0 ? 400 : 500));
				}
			}
			
			neuron1.setDependencies(dependenciesFull);
			neuron1.doubleSensor(0);
			assertEquals(dependenciesFull.size(), neuron1.getDependencies().size());
            for (int i = 0; i < dependenciesFull.size(); i++) {
                assertEquals(dependenciesFull.get(i).getFirst(), neuron1.getDependencies().get(i).getFirst());
                assertEquals(dependenciesFull.get(i).getSecond(), neuron1.getDependencies().get(i).getSecond());
            }
			
			neuron1.setDependencies(dependenciesAlmost);
			neuron1.doubleSensor(0);
			assertEquals(7, neuron1.getDependencies().size());
            assertEquals(dependenciesAlmost.get(0).getFirst(), neuron1.getDependencies().get(6).getFirst());
            assertEquals(400, neuron1.getDependencies().get(6).getSecond());
		}
		
	}
}