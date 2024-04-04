package sim.neuralnet;

import util.Pair;

import java.util.ArrayList;

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
	 */
    private ArrayList<Pair<Neuron, Integer>> dependencies;

    private int bias;

    public ArrayList<Pair<Neuron, Integer>> getDependencies() {
    	return dependencies;
    }
    
    /**
     * pre: deps has correct length
     */
    public void setDependencies(ArrayList<Pair<Neuron, Integer>> deps) {
    	dependencies = deps;
    }

    /**
     * Initializes with getBias = 0 and getDependencies is empty
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
    		dependencies.remove(6);
    		dependencies.add(new Pair<Neuron, Integer>(dependency , weight));
    		return true;
    	}
    	var p = new Pair<Neuron, Integer>(dependency, weight);
    	dependencies.add(p);
    	return true;

    }

    public void setBias(int bias)
    {
        this.bias = bias;
    }
    
    public int getBias() {
    	return bias;
    }

    @Override
    public int computeOutput(World world, Creature creature)
    {
    	return 0;
    }

    public abstract int applyActivationFunction(int input);
}
