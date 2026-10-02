package sim;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sim.behaviors.Behavior;
import sim.behaviors.NeuralNetworkBehavior;
import sim.naturalselection.NaturalSelection;
import util.Orientation;
import util.Point;
import util.RandomUtil;

public class Simulation
{

	private final NaturalSelection nsel;
	
	/**
	 * not a representation object for performance
	 * 
	 * @invar | world != null
	 */
	private World world;

	/**
	 * @invar | populationSize >= 0
	 */
    private final int populationSize;
    
    /**
     * @post | result != null
     */
    public NaturalSelection getNaturalSelection() {
    	return nsel;
    }

    /**
     * @post | result >= 0
     */
	public int getPopulationSize() {
		return populationSize;
	}

	/**
	 * LEGIT
	 * @post | result != null
	 * @post | result.getPopulation().length == getPopulationSize()
	 */
	public World getWorld() {
		return world;
	}

	/**
     * Creates a simulation with distinct randomly occupied cells.
     *
     * @throws IllegalArgumentException | size <= 0 || populationSize < 0
     * @throws IllegalArgumentException | populationSize > (long) size * size
     * @throws IllegalArgumentException | nsel == null
     * @post | getPopulationSize() == populationSize
     * @post | getNaturalSelection() == nsel
	 */
    public Simulation(int size, int populationSize, NaturalSelection nsel) {
        validateCapacity(size, populationSize);
        if (nsel == null) { throw new IllegalArgumentException(); }
    	this.populationSize = populationSize;
    	this.world = createInitWorldNeuralnets(size, populationSize);
    	this.nsel = nsel;
    }
    
    /**
     * LEGIT
     * 
     * Returns a square World of side `size` with popuSize creatures.
     * The creatures use the supplied behaviors in array order.
     * Positions are sampled without replacement and orientations are random.
     * Empty populations are supported.
     * 
     * @param size is the length of the side of the returned world
     * 
     * @throws IllegalArgumentException | size <= 0 || popuSize < 0
     * @throws IllegalArgumentException | popuSize > (long) size * size
     * 
     * @throws IllegalArgumentException | behaviors == null || behaviors.length != popuSize
     * @throws IllegalArgumentException | Arrays.stream(behaviors).anyMatch(b -> b == null)
     * 
     * @creates | result
     * @post | result != null
     * @post | result.getPopulation().length == popuSize
     * @post | Arrays.stream(result.getPopulation()).map(Creature::getPosition).distinct().count() == popuSize
     */
    public static World createRandWorldWith(int size, int popuSize, Behavior[] behaviors) {
        validateCapacity(size, popuSize);
        if (behaviors == null || behaviors.length != popuSize
                || Arrays.stream(behaviors).anyMatch(b -> b == null)) {
            throw new IllegalArgumentException();
        }
        Creature[] pop = new Creature[popuSize];
        // A sparse partial shuffle samples free cells without retries or a full grid allocation.
        var remainingCells = new HashMap<Long, Long>();
        long remaining = (long) size * size;
        
        for (int i = 0 ; i < popuSize ; i ++) {
          long index = RandomUtil.integer(remaining);
          long cell = remainingCells.getOrDefault(index, index);
          long last = --remaining;
          if (index != last) {
              remainingCells.put(index, remainingCells.getOrDefault(last, last));
          }
          remainingCells.remove(last);
          Point position = new Point((int) (cell % size), (int) (cell / size));
      	  Orientation orient = Orientation.createRandom();
      	  var behavior = behaviors[i];
      	  pop[i] = new Creature(behavior, position, orient);
        }
               
        return new World(size, size, pop);
    }
    

    	
    /**
     * Creates a randomly initialized neural population with distinct occupied cells.
     *
     * @throws IllegalArgumentException | size <= 0 || popuSize < 0
     * @throws IllegalArgumentException | popuSize > (long) size * size
     * @creates | result
     * @post | result.getPopulation().length == popuSize
     */
    public static World createInitWorldNeuralnets(int size, int popuSize) {
        validateCapacity(size, popuSize);
    	Behavior[] behaviors = new Behavior[popuSize];
    	for (int i = 0 ; i < popuSize ; i++) {
    		behaviors[i] = new NeuralNetworkBehavior(Chromosome.createRandom());
    	}
    	return createRandWorldWith(size, popuSize, behaviors);
    }

    /**
     * Validates dimensions and capacity before allocation or random initialization.
     *
     * @throws IllegalArgumentException | size <= 0 || count < 0 || count > (long) size * size
     */
    private static void validateCapacity(int size, int count) {
        if (size <= 0 || count < 0 || count > (long) size * size) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Replaces the current world with a new one.
     * - If no creature survived (see private method) we reset world with createInitWorldNeuralnets. Else:
     * - We gather surviving creatures in a list `surv`.
     * - We compute the offpsring chromosomes (see private method) based on that list.
     *   Note that there should be `populationSize` chromosomes.
     * - Each new behavior is then obtained from an offspring chromosome by using
     *   Behavior.copyWithChromosome. To determine which kind of behavior to use, we cycle through `surv`.
     * - Finally the world is reset with the latter offspring behaviors using
     *   createRandWorldWith method.
     *  
     *  @mutates | getWorld()
     *  @post | getWorld() != null
	 *  @post | getWorld().getPopulation().length == getPopulationSize()
     */
    public void nextGeneration() {
    	
    	ArrayList<Creature> surv = survivingCreatures();
    	
    	if (surv.size() == 0) {
    		world = createInitWorldNeuralnets(world.getWidth(), populationSize);
    	} else {
    		
    	
	    	ArrayList<Chromosome> parentGeneration = new ArrayList<>();
	    	
	    	for (Creature creature : surv) {
	    		parentGeneration.add(creature.getChromosome());
	    	}
	    	Chromosome[] offsprings = computeOffspringWithSize(parentGeneration, populationSize);
	
	    	Behavior[] behaviors = new Behavior[populationSize];
	    	
	    	for (int i = 0; i < populationSize; i++) {
	            behaviors[i] = surv.get(i % surv.size()).getBehavior().copyWithChromosome(offsprings[i]);
	        }
	    	
	    	world = createRandWorldWith(world.getWidth(), populationSize, behaviors);
    	}
    }

    /**
     * The list of creatures that survive, according to `nsel : NaturalSelection` field
     */
    private ArrayList<Creature> survivingCreatures() {
    	
    	ArrayList<Creature> surv = new ArrayList<>();
    	
    	for (Creature creature: world.getPopulation()) {
            if (nsel.survives(world, creature.getPosition())) {
            	surv.add(creature);
            }
        }
    	return surv;
    }
    

    
    
    /**
     * LEGIT
     * 
     * @pre | parentGeneration != null && parentGeneration.size() > 0
     * @post | result != null && result.length == ofSize 
     */
    private Chromosome[] computeOffspringWithSize(ArrayList<Chromosome> parentGeneration, int ofSize) {

        var offspringChromosomes = new Chromosome[ofSize];

        for ( int i = 0; i != ofSize; ++i )
        {
            var parent1 = RandomUtil.pick(parentGeneration);
            var parent2 = RandomUtil.pick(parentGeneration);

            var offspring = parent1.crossover2(parent2);

            if ( RandomUtil.integer(100) < Constants.MUT_RATE )
            {
                offspring = offspring.randomlyMutate();
            }

            offspringChromosomes[i] = offspring;
        }

        return offspringChromosomes;  
    }
    

    
    
    
    
}
