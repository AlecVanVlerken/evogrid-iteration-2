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
	 * @invar | dependencies.size() <= 7
	 * @invar | dependencies.stream().allMatch(p -> p != null && p.getFirst() != null && p.getSecond() != null)
	 *
	 */
    private ArrayList<Pair<Neuron, Integer>> dependencies;

    /**
	 * @invar | bias >= Constants.GENE_MIN && bias <= Constants.GENE_MAX
	 */
    private int bias;

    /**
     * Returns a snapshot of the connections, copying the list and pairs but sharing neurons.
     *
     * @creates | result
     * @post | result != null
     * @post | result.size() == dependencies.size()
     */
    public ArrayList<Pair<Neuron, Integer>> getDependencies() {
        return copyDependencies(dependencies);
    }
    
    /**
     * Copies connection containers while retaining editable neuron references.
     *
     * @inspects | deps
     * @throws IllegalArgumentException | deps == null || deps.size() > 7
     * @throws IllegalArgumentException | deps.stream().anyMatch(p -> p == null || p.getFirst() == null || p.getSecond() == null)
     * @post | getDependencies().size() == deps.size()
     * @post | java.util.stream.IntStream.range(0, deps.size()).allMatch(i ->
     *       | getDependencies().get(i).getFirst() == deps.get(i).getFirst() &&
     *       | getDependencies().get(i).getSecond().equals(deps.get(i).getSecond()))
     */
    public void setDependencies(ArrayList<Pair<Neuron, Integer>> deps) {
        if (deps == null || deps.size() > 7 || deps.stream().anyMatch(p ->
                p == null || p.getFirst() == null || p.getSecond() == null)) {
            throw new IllegalArgumentException();
        }
        dependencies = copyDependencies(deps);
    }

    /**
     * Copies connection structure without copying the referenced neurons.
     *
     * @pre | deps != null
     * @creates | result
     * @post | result.size() == deps.size()
     */
    private static ArrayList<Pair<Neuron, Integer>> copyDependencies(ArrayList<Pair<Neuron, Integer>> deps) {
        var copy = new ArrayList<Pair<Neuron, Integer>>();
        for (var pair : deps) {
            copy.add(new Pair<>(pair.getFirst(), pair.getSecond()));
        }
        return copy;
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
     *
     * @pre | dependency != null
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
        for (Pair<Neuron, Integer> pair : this.dependencies) {
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
