package other;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import sim.behaviors.Behavior;
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
}