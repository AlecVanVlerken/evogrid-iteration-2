package other;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import sim.naturalselection.NaturalSelection;
import sim.naturalselection.SouthEastHabitableZone;
import sim.neuralnet.ActivationFunctionNeuron;
import sim.neuralnet.BinarySensorNeuron;
import sim.neuralnet.FreePassageSensorNeuron;
import sim.neuralnet.HorizontalOrientationSensorNeuron;
import sim.neuralnet.HorizontalPositionSensorNeuron;
import sim.neuralnet.LinearFunctionNeuron;
import sim.neuralnet.NeuralNetwork;
import sim.neuralnet.Neuron;
import sim.neuralnet.RectifiedLinearUnitFunctionNeuron;
import sim.neuralnet.SensorNeuron;
import sim.neuralnet.VerticalOrientationSensorNeuron;
import sim.neuralnet.VerticalPositionSensorNeuron;
import sim.Chromosome;
import sim.Constants;
import sim.Creature;
import sim.Simulation;
import sim.World;
import util.Color;
import util.Orientation;
import util.Pair;
import util.Point;
import util.Vector;

class CoverageTests {

	//private static Simulation createSimulation(int worldSize, int populationSize, int populationASize)
	//{
	//	return new Simulation(worldSize, populationSize, populationASize);
	//}
	
	
	@Nested
	class PairTests
	{
		
		Pair<Integer, Integer> pair;
		
		@BeforeEach
		void setup() {
			pair = new Pair<Integer, Integer>(5, 6);
		}
		
		@Test
		public void getFirst()
		{
			assertEquals(5, pair.getFirst());
		}
		
		@Test
		public void getSecond()
		{
			assertEquals(6, pair.getSecond());
		}
		
		@Test
		public void setFirst()
		{
			pair.setFirst(4);
			assertEquals(4, pair.getFirst());
		}
		
		@Test
		public void setSecond()
		{
			pair.setSecond(3);
			assertEquals(3, pair.getSecond());
		}
	}
	
	
	@Nested
	class BorderHabitableZoneTests
	{
		
		BorderHabitableZone zone;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			zone = new BorderHabitableZone(10);
		}
		
		@Test
		public void survivesFalse()
		{				
			assertFalse(zone.survives(world, new Point(25, 25)));
			assertFalse(zone.survives(world, new Point(10, 25)));
			assertFalse(zone.survives(world, new Point(25, 10)));
		}
		
		@Test
		public void survivesTrue()
		{				
			assertTrue(zone.survives(world, new Point(0, 0)));
			assertTrue(zone.survives(world, new Point(49, 49)));
			assertTrue(zone.survives(world, new Point(5, 25)));
			assertTrue(zone.survives(world, new Point(25, 5)));
			assertTrue(zone.survives(world, new Point(45, 25)));
			assertTrue(zone.survives(world, new Point(25, 45)));
		}
	}
	
	
	@Nested
	class CircularHabitableZoneTests
	{
		
		CircularHabitableZone zone;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			zone = new CircularHabitableZone(new Point(25,25), 5);
		}
		
		@Test
		public void survivesFalse()
		{				
			assertFalse(zone.survives(world, new Point(0, 0)));
			assertFalse(zone.survives(world, new Point(49, 49)));
			assertFalse(zone.survives(world, new Point(100, 100)));
		}
		
		@Test
		public void survivesTrue()
		{				
			assertTrue(zone.survives(world, new Point(25, 25)));
			assertTrue(zone.survives(world, new Point(30, 25)));
			assertTrue(zone.survives(world, new Point(20, 25)));
			assertTrue(zone.survives(world, new Point(25, 30)));
			assertTrue(zone.survives(world, new Point(25, 20)));
		}
	}
	
	
	@Nested
	class SouthEastHabitableZoneTests
	{
		
		SouthEastHabitableZone zone;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			zone = new SouthEastHabitableZone();
		}
		
		@Test
		public void survivesFalse()
		{				
			assertFalse(zone.survives(world, new Point(1, 1)));
		}
		
		@Test
		public void survivesTrue()
		{				
			assertTrue(zone.survives(world, new Point(49, 49)));

		}
	}
	
	
	@Nested
	class DisjunctionTests
	{
		
		CircularHabitableZone zone1;
		BorderHabitableZone zone2;
		Disjunction zone;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			zone1 = new CircularHabitableZone(new Point(25,25), 5);
			zone2 = new BorderHabitableZone(10);
			zone = new Disjunction(zone1, zone2);
		}
		
		@Test
		public void survivesFalse()
		{				
			assertFalse(zone.survives(world, new Point(11, 11)));
		}
		
		@Test
		public void survivesTrue()
		{				
			assertTrue(zone.survives(world, new Point(25, 25)));
			assertTrue(zone.survives(world, new Point(30, 25)));
			assertTrue(zone.survives(world, new Point(20, 25)));
			assertTrue(zone.survives(world, new Point(25, 30)));
			assertTrue(zone.survives(world, new Point(25, 20)));
			
			assertTrue(zone.survives(world, new Point(0, 0)));
			assertTrue(zone.survives(world, new Point(49, 49)));
			assertTrue(zone.survives(world, new Point(5, 25)));
			assertTrue(zone.survives(world, new Point(25, 5)));
			assertTrue(zone.survives(world, new Point(45, 25)));
			assertTrue(zone.survives(world, new Point(25, 45)));
		}
	}
	
	
	@Nested
	class BehaviorATests
	{
		
		BehaviorA behavior;
		World world;
		Chromosome chrom;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			chrom = Chromosome.createRandom();
			behavior = new BehaviorA(chrom);
		}
		
		@Test
		public void getColor()
		{				
			assertEquals(Color.RED, behavior.getColor());
		}
		
		@Test
		public void copyWithChromosome()
		{	
			BehaviorA copyBehavior = behavior.copyWithChromosome(chrom);
			for (int i = 0; i < Constants.CHROM_SIZE; i++) {
				assertEquals(behavior.getChromosome().getGene(i), copyBehavior.getChromosome().getGene(i));
			}
		}
	}
	
	
	@Nested
	class BehaviorBTests
	{
		
		BehaviorB behavior;
		World world;
		Chromosome chrom;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			chrom = Chromosome.createRandom();
			behavior = new BehaviorB(chrom);
		}
		
		@Test
		public void getColor()
		{				
			assertEquals(Color.BLUE, behavior.getColor());
		}
		
		@Test
		public void copyWithChromosome()
		{	
			BehaviorB copyBehavior = behavior.copyWithChromosome(chrom);
			for (int i = 0; i < Constants.CHROM_SIZE; i++) {
				assertEquals(behavior.getChromosome().getGene(i), copyBehavior.getChromosome().getGene(i));
			}
		}
	}
	
	
	@Nested
	class ImmobileBehaviorTests
	{
		
		ImmobileBehavior behavior;
		World world;
		Chromosome chrom;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			chrom = Chromosome.createRandom();
			behavior = new ImmobileBehavior(chrom);
		}
		
		@Test
		public void getColor()
		{				
			assertEquals(Color.WHITE, behavior.getColor());
		}
		
		@Test
		public void copyWithChromosome()
		{	
			ImmobileBehavior copyBehavior = behavior.copyWithChromosome(chrom);
			for (int i = 0; i < Constants.CHROM_SIZE; i++) {
				assertEquals(behavior.getChromosome().getGene(i), copyBehavior.getChromosome().getGene(i));
			}
		}
	}
	
	
	@Nested
	class NeuralNetworkBehaviorTests
	{
		
		NeuralNetworkBehavior behavior;
		World world;
		Chromosome chrom;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			world = new World(50, 50, creature);
			chrom = Chromosome.createRandom();
			behavior = new NeuralNetworkBehavior(chrom);
		}
		
		@Test
		public void getColor()
		{				
			assertEquals(Color.GREEN, behavior.getColor());
		}
		
		@Test
		public void copyWithChromosome()
		{	
			NeuralNetworkBehavior copyBehavior = behavior.copyWithChromosome(chrom);
			for (int i = 0; i < Constants.CHROM_SIZE; i++) {
				assertEquals(behavior.getChromosome().getGene(i), copyBehavior.getChromosome().getGene(i));
			}
		}
	}
	
	
	@Nested
	class LinearFunctionNeuronTests
	{
		
		LinearFunctionNeuron neuron;
		
		@BeforeEach
		void setup() {
			neuron = new LinearFunctionNeuron();
		}
		
		@Test
		public void applyActivationFunction()
		{				
			assertEquals(1000, neuron.applyActivationFunction(2000));
			assertEquals(-1000, neuron.applyActivationFunction(-2000));
			assertEquals(500, neuron.applyActivationFunction(500));
		}
		
	}
	
	
	@Nested
	class HorizontalOrientationSensorNeuronTests
	{
		
		HorizontalOrientationSensorNeuron neuron;
		
		@BeforeEach
		void setup() {
			neuron = new HorizontalOrientationSensorNeuron();
		}
		
		@Test
		public void cardinalValues()
		{				
			assertEquals(0, neuron.north());
			assertEquals(500, neuron.northEast());
			assertEquals(-500, neuron.northWest());
			assertEquals(0, neuron.south());
			assertEquals(500, neuron.southEast());
			assertEquals(-500, neuron.southWest());
			assertEquals(-1000, neuron.west());
			assertEquals(1000, neuron.east());
		}
		
	}
	
	
	@Nested
	class VerticalOrientationSensorNeuronTests
	{
		
		VerticalOrientationSensorNeuron neuron;
		
		@BeforeEach
		void setup() {
			neuron = new VerticalOrientationSensorNeuron();
		}
		
		@Test
		public void cardinalValues()
		{				
			assertEquals(-1000, neuron.north());
			assertEquals(-500, neuron.northEast());
			assertEquals(-500, neuron.northWest());
			assertEquals(1000, neuron.south());
			assertEquals(500, neuron.southEast());
			assertEquals(500, neuron.southWest());
			assertEquals(0, neuron.west());
			assertEquals(0, neuron.east());
		}
		
	}
	
	
	@Nested
	class HorizontalPositionSensorNeuronTests
	{
		
		HorizontalPositionSensorNeuron neuron;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(25,25), Orientation.north())};
			world = new World(50, 50, creature);
			neuron = new HorizontalPositionSensorNeuron();
		}
		
		@Test
		public void computeOutput()
		{				
			assertEquals(20, neuron.computeOutput(world, world.getPopulation()[0]));
		}
	}
	
	
	@Nested
	class VerticalPositionSensorNeuronTests
	{
		
		VerticalPositionSensorNeuron neuron;
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(25,25), Orientation.north())};
			world = new World(50, 50, creature);
			neuron = new VerticalPositionSensorNeuron();
		}
		
		@Test
		public void computeOutput()
		{				
			assertEquals(20, neuron.computeOutput(world, world.getPopulation()[0]));
		}
	}
	
	
	@Nested
	class FreePassageSensorNeuronTests
	{

		@Test
		public void detectNorth()
		{				
			FreePassageSensorNeuron neuron = new FreePassageSensorNeuron(Orientation.north());
			
			Creature[] creature1 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east()),new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(6,5), Orientation.east())};
			World world1 = new World(50, 50, creature1);
			
			assertFalse(neuron.detect(world1, world1.getPopulation()[0]));
			
			Creature[] creature2 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east())};
			World world2 = new World(50, 50, creature2);
			
			assertTrue(neuron.detect(world2, world2.getPopulation()[0]));
		}
		
		@Test
		public void detectNorthEast()
		{				
			FreePassageSensorNeuron neuron = new FreePassageSensorNeuron(Orientation.northEast());
			
			Creature[] creature1 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east()),new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(6,6), Orientation.east())};
			World world1 = new World(50, 50, creature1);
			
			assertFalse(neuron.detect(world1, world1.getPopulation()[0]));
			
			Creature[] creature2 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east())};
			World world2 = new World(50, 50, creature2);
			
			assertTrue(neuron.detect(world2, world2.getPopulation()[0]));
		}
		
		@Test
		public void detectNorthWest()
		{				
			FreePassageSensorNeuron neuron = new FreePassageSensorNeuron(Orientation.northWest());
			
			Creature[] creature1 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east()),new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(6,4), Orientation.east())};
			World world1 = new World(50, 50, creature1);
			
			assertFalse(neuron.detect(world1, world1.getPopulation()[0]));
			
			Creature[] creature2 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east())};
			World world2 = new World(50, 50, creature2);
			
			assertTrue(neuron.detect(world2, world2.getPopulation()[0]));
		}
	}
	
	
	@Nested
	class BinarySensorNeuronTests
	{

		@Test
		public void computeOutput()
		{				
			FreePassageSensorNeuron neuron = new FreePassageSensorNeuron(Orientation.north());
			
			Creature[] creature1 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east()),new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(6,5), Orientation.east())};
			World world1 = new World(50, 50, creature1);
			
			assertEquals(-750, neuron.computeOutput(world1, world1.getPopulation()[0]));
			
			Creature[] creature2 = {new Creature(new NeuralNetworkBehavior(Chromosome.createRandom()), new Point(5,5), Orientation.east())};
			World world2 = new World(50, 50, creature2);
			
			assertEquals(750, neuron.computeOutput(world2, world2.getPopulation()[0]));
		}
	}
	
	
	@Nested
	class ActivationFunctionNeuronTests
	{
		
		ActivationFunctionNeuron neuron;
		
		@BeforeEach
		void setup() {
			neuron = new LinearFunctionNeuron();
		}

		@Test
		public void settersGetters()
		{				
			neuron.setBias(10);
			assertEquals(10, neuron.getBias());
			
			ArrayList<Pair<Neuron, Integer>> dependencies =	new ArrayList<>();
			dependencies.add(new Pair<>(neuron, 500));
			neuron.setDependencies(dependencies);
			assertEquals(dependencies, neuron.getDependencies());
		}
		
		@Test
		public void connect()
		{							
			ArrayList<Pair<Neuron, Integer>> dependenciesFull =	new ArrayList<>();
			ArrayList<Pair<Neuron, Integer>> dependenciesAlmost = new ArrayList<>();
			
			for (int i = 0; i < 7; i++) {
				dependenciesFull.add(new Pair<>(neuron, 500));
				
				if (i < 6) {
					dependenciesAlmost.add(new Pair<>(neuron, 500));
				}
			}

			neuron.setDependencies(dependenciesFull);
			
			assertFalse(neuron.connect(neuron, 500));
			assertEquals(dependenciesFull, neuron.getDependencies());
			
			neuron.setDependencies(dependenciesAlmost);
			assertTrue(neuron.connect(neuron, 400));
			assertFalse(dependenciesFull.equals(neuron.getDependencies()));
			
		}
	}
	
	
	@Nested
	class NeuralNetworkTests
	{
		
		NeuralNetwork network;
		
		@BeforeEach
		void setup() {
			network = new NeuralNetwork();
		}
		
		@Test
		public void getters()
		{	
			SensorNeuron[] inputLayerNeurons = network.getInputNeurons();
			assertEquals(inputLayerNeurons, network.getInputNeurons());
			
			ActivationFunctionNeuron moveForwardNeuron = network.getMoveForwardNeuron();
			ActivationFunctionNeuron turnClockwiseNeuron = network.getTurnClockwiseNeuron();
			ActivationFunctionNeuron turnCounterclockwiseNeuron = network.getTurnCounterclockwiseNeuron();
			assertEquals(moveForwardNeuron, network.getMoveForwardNeuron());
			assertEquals(turnClockwiseNeuron, network.getTurnClockwiseNeuron());
			assertEquals(turnCounterclockwiseNeuron, network.getTurnCounterclockwiseNeuron());
			
			//ActivationFunctionNeuron[] outputLayerNeurons = network.getOutputNeurons();
			//assertEquals(outputLayerNeurons, network.getOutputNeurons());
		}
	}
	
	
	@Nested
	class ChromosomeTests
	{
		
		Chromosome chrom1;
		Chromosome chrom2;
		Chromosome chrom3;
		
		@BeforeEach
		void setup() {
			chrom1 = new Chromosome(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 });
			chrom2 = new Chromosome(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 });
			chrom3 = new Chromosome(new int[] { 1, 2, 3, 4, 5, 7, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 });
		}
		
		@Test
		public void isEqual()
		{
			assertTrue(chrom1.isEqual(chrom1));
			
			assertTrue(chrom1.isEqual(chrom2));
     		assertTrue(chrom2.isEqual(chrom1));
     		
			assertFalse(chrom1.isEqual(chrom3));
			assertFalse(chrom3.isEqual(chrom1));
		}
		
		@Test
		public void onlyDiffersAt()
		{
			assertFalse(chrom1.onlyDiffersAt(chrom3, 0));
			assertTrue(chrom1.onlyDiffersAt(chrom3, 5));
		}
		
		@Test
		public void matchesFrom()
		{
			assertTrue(chrom1.matchesFrom(chrom3, 10));
			assertFalse(chrom1.matchesFrom(chrom3, 5));
		}
		
		@Test
		public void matchesUntil()
		{
			assertTrue(chrom1.matchesUntil(chrom3, 4));
			assertFalse(chrom1.matchesUntil(chrom3, 10));
		}
		
		@Test
		public void getter()
		{
			assertEquals(4, chrom1.getGene(3));
		}
		
		@Test
		public void isValidGene()
		{
			assertTrue(Chromosome.isValidGene(Constants.GENE_MIN));
			assertTrue(Chromosome.isValidGene(Constants.GENE_MAX));
			assertTrue(Chromosome.isValidGene(Constants.GENE_MAX/2));
			
			assertFalse(Chromosome.isValidGene(Constants.GENE_MIN-1));
			assertFalse(Chromosome.isValidGene(Constants.GENE_MAX+1));
		}
	}
	
	@Nested
	class CreatureTests
	{
		
		Creature creature;
		BehaviorA behavior;
		
		@BeforeEach
		void setup() {
			behavior = new BehaviorA(Chromosome.createRandom());
			creature = new Creature(behavior, new Point(5,5), Orientation.east());

		}
		
		@Test
		public void getters()
		{	
			assertEquals(behavior, creature.getBehavior());
			assertEquals(behavior.getChromosome(), creature.getChromosome());
			assertEquals(5, creature.getPosition().getX());
			assertEquals(5, creature.getPosition().getY());
			assertEquals(Orientation.east(), creature.getOrientation());
		}
		
		@Test
		public void giveCopy()
		{	
			Creature copyCreature = creature.giveCopy();
			assertEquals(creature.getBehavior(), copyCreature.getBehavior());
			assertEquals(creature.getPosition(), copyCreature.getPosition());
			assertEquals(creature.getOrientation(), copyCreature.getOrientation());
		}
		
		@Test
		public void isEqual()
		{	
			Creature copyCreature = creature.giveCopy();
			Creature creatureNotSame = new Creature(behavior, new Point(4,5), Orientation.east());
			
			assertTrue(creature.isEqual(copyCreature));
			assertFalse(creature.isEqual(creatureNotSame));
		}
		
		@Test
		public void performAction()
		{	
			Creature[] creatures = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(1,1), Orientation.north())};
			World world = new World(50, 50, creatures);
			Creature copyCreature = creature.giveCopy();
			creature.performAction(world);
			
			assertFalse(copyCreature.isEqual(creature));						
		}
		
		@Test
		public void turnCounterclockwise()
		{	
			creature.turnCounterclockwise();;
			
			assertEquals(Orientation.northEast(), creature.getOrientation());						
		}
		
		@Test
		public void turnClockwise()
		{	
			creature.turnClockwise();
			
			assertEquals(Orientation.southEast(), creature.getOrientation());						
		}
		
		@Test
		public void destination()
		{				
			creature.destination(new Vector(1, 0));
			
			assertEquals(7, creature.destination(new Vector(1, 0)).getX());						
		}
		
		public void moveForward()
		{	
			Creature[] creatures = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(9,5), Orientation.north())};
			World world = new World(50, 50, creatures);
			creature.moveForward(world, new Vector(1, 0));
			
			assertEquals(7, creature.getPosition().getX());
			
			creature.moveForward(world, new Vector(1, 0));
			
			assertEquals(7, creature.getPosition().getX());
		}
	}
	
	
	@Nested
	class WorldTests
	{
		
		World world;
		
		@BeforeEach
		void setup() {
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(25,25), Orientation.north())};
			world = new World(50, 50, creature);
		}
		
		@Test
		public void getters()
		{				
			assertEquals(50, world.getWidth());
			assertEquals(50, world.getHeight());
			
			Creature[] creature = world.getPopulation();
			
			assertEquals(creature, world.getPopulation());
		}
		
		@Test
		public void isFree()
		{				
			assertFalse(world.isFree(new Point(60, 60)));
			assertFalse(world.isFree(new Point(25, 25)));
			assertTrue(world.isFree(new Point(10, 10)));
		}
		
		@Test
		public void step()
		{				
			Creature[] creature = {new Creature(new BehaviorA(Chromosome.createRandom()), new Point(25,25), Orientation.north())};
			world.step();
			
			assertFalse(creature.equals(world.getPopulation()));
		}
	}
	
	
	@Nested
	class SimulationTests
	{
		
		Simulation simulation;
		
		@BeforeEach
		void setup() {
			simulation = new Simulation(Constants.WSIZE, Constants.POPU_SIZE, new BorderHabitableZone(20));
		}
		
		@Test
		public void getters()
		{	
			World world = simulation.getWorld();
			NaturalSelection selection = simulation.getNaturalSelection();
			
			assertEquals(Constants.POPU_SIZE, simulation.getPopulationSize());
			assertEquals(world, simulation.getWorld());
			assertEquals(selection, simulation.getNaturalSelection());
		}
		
		@Test
		public void nextGeneration()
		{	
			World world = simulation.getWorld();
			simulation.nextGeneration();
			
			assertFalse(world.equals(simulation.getWorld()));

		}
	}
}