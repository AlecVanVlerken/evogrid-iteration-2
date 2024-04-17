package sim;


import java.util.ArrayList;
import java.util.Arrays;

import sim.behaviors.Behavior;
import sim.behaviors.NeuralNetworkBehavior;
import sim.naturalselection.NaturalSelection;
import util.Orientation;
import util.Pair;
import util.Point;
import util.RandomUtil;

public class Simulation
{

	private final NaturalSelection nsel;
	
	/**
	 * not a representation object for performance
	 */
	private World world;

    private final int populationSize;
    
    public NaturalSelection getNaturalSelection() {
    	return nsel;
    }

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
	 * LEGIT
	 * 
	 * You may change this function to experiment *but*
	 * remember to restore this function upon submitting.
	 * 
	 */
    public Simulation(int size, int populationSize, NaturalSelection nsel) {
    	this.populationSize = populationSize;
    	this.world = createInitWorldNeuralnets(size, populationSize);
    	this.nsel = nsel;
    }
    
    /**
     * LEGIT
     * 
     * Returns a square World of side `size` with popuSize creatures.
     * The creatures have Chromosomes as in `chroms` and behaviors as in `behaviors`.
     * Moreover all creatures have random positions (anywhere within the world) and orientations.
     * 
     * @param size is the length of the side of the returned world
     * 
     * @pre | size > 0
     * @pre | popuSize > 0
     * 
     * @pre | behaviors != null && behaviors.length == popuSize
     * @pre | Arrays.stream(behaviors).allMatch(b -> b != null)
     * 
     * @creates | result
     * @post | result != null
     * @post | result.getPopulation().length == popuSize
     * // post | IntStream.range(0, numA).allMatch(i -> result.getPopulationA()[i].getChromosome().isEqual(chromsA[i]))
     * // post | Arrays.stream(result.getPopulation()).allMatch(c -> Point.isWithin(c.getPosition().move(new Vector(-size/4, -size/4)), size/2, size/2))
     */
    public static World createRandWorldWith(int size, int popuSize, Behavior[] behaviors) {
        Creature[] pop = new Creature[popuSize];
        
        for (int i = 0 ; i < popuSize ; i ++) {
          Point position = Point.createRandom(size,size);
          //Point position = Point.createRandom(size/2, size/2).move(new Vector(size/4, size/4));
      	  Orientation orient = Orientation.createRandom();
      	  var behavior = behaviors[i];
      	  pop[i] = new Creature(behavior, position, orient);
        }
               
        return new World(size, size, pop);
    }
    

    	
    /**
     * LEGIT
     */
    public static World createInitWorldNeuralnets(int size, int popuSize) {
    	Behavior[] behaviors = new Behavior[popuSize];
    	//Behavior[] chooseFrom = {new NeuralNetworkBehavior(Chromosome.createRandom()), new ImmobileBehavior(Chromosome.createRandom()), new BehaviorA(Chromosome.createRandom()), new Behavior(Chromosome.createRandom())};
    	for (int i = 0 ; i < popuSize ; i++) {
    		behaviors[i] = new NeuralNetworkBehavior(Chromosome.createRandom());
    		//behaviors[i] = new NeuralNetworkBehavior(Chromosome.createRandom());
    	}
    	return createRandWorldWith(size, popuSize, behaviors);
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
