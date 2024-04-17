package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import sim.neuralnet.NeuralNetwork;
import util.Color;
import util.Vector;


/**
 * @invar | getColor() .equals( Color.GREEN )
 */
public class NeuralNetworkBehavior extends Behavior	{
	/**
	 * @representationObject
	 * neuralNetwork != null
	 */
    private final NeuralNetwork neuralNetwork;

    public NeuralNetworkBehavior(Chromosome chromosome)
    {
    	super(chromosome);
        this.neuralNetwork = NeuralNetwork.fromChromosome(chromosome);
    }
    
    
    @Override
	public Color getColor() {
		return Color.GREEN;
	}
    

    @Override
    public void applyBehavior(World world, Creature creature)
    {
    	if (!world.isLimPos(creature.getPosition())) {
    		processForwardMovement(world, creature);
    		processTurning(world, creature);
    	}
    }

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
	public NeuralNetworkBehavior copyWithChromosome(Chromosome chromosome)
	{
		return new NeuralNetworkBehavior(chromosome);
	}
}
