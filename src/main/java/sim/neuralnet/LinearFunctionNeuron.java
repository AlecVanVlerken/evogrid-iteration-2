package sim.neuralnet;

import util.Pair;

/**
 */
public class LinearFunctionNeuron extends ActivationFunctionNeuron
{
    @Override
    /**
     * It takes its parameter and clamps it to the interval [-1000, 1000].
     * 
     * @pre | input >= Integer.MIN_VALUE && input <= Integer.MAX_VALUE
     * @post | result <= 1000 && result >= -1000

     */
    public int applyActivationFunction(int input)
    {
    	return Math.min(Math.max(-1000, input), 1000);
    }
    
    
    /**
     * @mutates | this
     * @pre | 0 <= index
     * @pre | index < getDependencies().size()
     * To make a sensor Neuron have more impact on super.computeOutput we can link to it twice (with the same weight)
     * The additional reference is appended at the end of getDependencies().
     * 
     * Fails silently like super.connect
     */
    public void doubleSensor(int index) {
		var deps = getDependencies();
		if (deps.size() < 7 && index < deps.size()) {
			var p = deps.get(index);
			deps.add(p);
			setDependencies(deps);
    	}		
    }
    
    
}
