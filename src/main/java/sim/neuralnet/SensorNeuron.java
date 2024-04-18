package sim.neuralnet;

import sim.Creature;
import sim.World;

/**
 * @immutable
 */
public abstract class SensorNeuron implements Neuron {

	@Override
	/**
     * Computes the output of the input neurons.
     * 
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     */
	public abstract int computeOutput(World world, Creature creature);

}
