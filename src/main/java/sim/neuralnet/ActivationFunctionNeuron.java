package sim.neuralnet;

import util.Pair;

import java.util.ArrayList;

import sim.Constants;

import sim.Creature;
import sim.World;

/**
 * The Neuron references should be freely accessible by the client
 */
public abstract class ActivationFunctionNeuron implements Neuron
{
	/**
	 * @representationObject
	 * @representationObjects
	 * 
	 * @invar | dependencies != null
	 *
	 */
    private ArrayList<Pair<Neuron, Integer>> dependencies;

    /**
	 * @invar | bias >= Constants.GENE_MIN && bias <= Constants.GENE_MAX
	 */
    private int bias;

    /**
     * @post | result != null
     */
    public ArrayList<Pair<Neuron, Integer>> getDependencies() {
    	return dependencies;
    }
    
    /**
     * @pre | deps.size() == 7
     * @pre | deps.stream().allMatch(pair -> pair != null)
     * @post | this.getDependencies() == deps
     */
    public void setDependencies(ArrayList<Pair<Neuron, Integer>> deps) {
    	dependencies = deps;
    }

    /**
     * Initializes with getBias = 0 and getDependencies is empty
     * 
     * @post | getDependencies().isEmpty()
     * @post | getBias() == 0
     */
    public ActivationFunctionNeuron()
    {
        this.dependencies = new ArrayList<>();
        bias = 0;
    }

    /**
     * If the connection should fail, do nothing and return false
     */
    public boolean connect(Neuron dependency, int weight)
    {
    	
    	if (dependencies.size() == 7) {
    		return false;
    	} else {
    		var p = new Pair<Neuron, Integer>(dependency, weight);
        	dependencies.add(p);
        	return true;
    	}
    	

    }

    /**
     * @pre | bias >= Constants.GENE_MIN && bias <= Constants.GENE_MAX
     * @post | this.getBias() == bias
     */
    public void setBias(int bias)
    {
        this.bias = bias;
    }
    
    /**
     * @post | result >= Constants.GENE_MIN && result <= Constants.GENE_MAX
     */
    public int getBias() {
    	return bias;
    }

    @Override
    public int computeOutput(World world, Creature creature)
    {
    	int total = 0;
    	for (Pair<Neuron, Integer> pair : this.getDependencies()) {
    		total += (pair.getFirst().computeOutput(world, creature) * pair.getSecond())/1000;
	    }
    	total += this.getBias();
    	return this.applyActivationFunction(total);
    }

    /**
     * It takes its parameter and clamps it to a specific interval.
     * 
     * @pre | input >= Integer.MIN_VALUE && input <= Integer.MAX_VALUE
     */
    public abstract int applyActivationFunction(int input);
}
