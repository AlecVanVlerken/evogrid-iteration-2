package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import sim.neuralnet.NeuralNetwork;
import util.Color;
import util.Vector;


/**
 * @invar | getColor().equals( Color.GREEN )
 */
public class NeuralNetworkBehavior extends Behavior	{
	/**
	 * @representationObject
	 * neuralNetwork != null
	 */
    private final NeuralNetwork neuralNetwork;

    /**
     * Initializes a new NeuralNetworkBehavior object.
     * @inspects | chromosome
     * @throws IllegalArgumentException | chromosome == null
     * @post | this.getChromosome() == chromosome
     * @post | this.getNeuralNetwork() != null
     * @post | this.getNeuralNetwork().getInputNeurons() != null
     * @post | this.getNeuralNetwork().getOutputNeurons() != null
     */
    public NeuralNetworkBehavior(Chromosome chromosome)
    {
    	super(chromosome);
        this.neuralNetwork = NeuralNetwork.fromChromosome(chromosome);
    }
    
    
    @Override
    /**
	 * @post | result != null
	 */
	public Color getColor() {
		return Color.GREEN;
	}
    
    /**
	 * @post | result != null
	 */
	public NeuralNetwork getNeuralNetwork() {
		return this.neuralNetwork;
	}    

    @Override
    /**
     * Decides at every position, including edges, moving before evaluating turning.
     * Occupied cells and world boundaries still block movement.
     * 
	 * @inspects | world
	 * @mutates | creature
	 * @pre | world != null
	 * @pre | creature != null
	 */
    public void applyBehavior(World world, Creature creature)
    {
        processForwardMovement(world, creature);
        processTurning(world, creature);
    }

    /**
     * Applies the forward movement of the given Creature in the given the given world.
     * 
	 * @inspects | world
	 * @mutates | creature
	 * @pre | world != null
	 * @pre | creature != null
	 */
    private void processForwardMovement(World world, Creature creature)
    {
    	int forwardVal = neuralNetwork.getMoveForwardNeuron().computeOutput(world, creature);
    	
    	if (forwardVal > 0) {
    		creature.moveForward(world, new Vector(0, 0));
    	}
        
    }

    /**
     * LEGIT
     */
    private void processTurning(World world, Creature creature)
    {

    	int clockVal = neuralNetwork.getTurnClockwiseNeuron().computeOutput(world, creature);
    	int counterVal = neuralNetwork.getTurnCounterclockwiseNeuron().computeOutput(world, creature);
    	
    	if ((Math.abs(clockVal - counterVal) > 150)) { //if the 2 values are substantially different
    		if (clockVal > counterVal) {creature.turnClockwise();}
    		else {creature.turnCounterclockwise();}
    	}

    }
	



	@Override
	/**
     * @creates | result
     * @inspects | chromosome
     * @pre | chromosome != null
     * @post | result.getChromosome() == chromosome
     */
	public NeuralNetworkBehavior copyWithChromosome(Chromosome chromosome)
	{
		return new NeuralNetworkBehavior(chromosome);
	}
}
