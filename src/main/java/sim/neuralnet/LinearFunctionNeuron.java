package sim.neuralnet;

public class LinearFunctionNeuron extends ActivationFunctionNeuron
{
    @Override
    public int applyActivationFunction(int input)
    {
        return 0;
    }
    
    
    /**
     * @pre | 0 <= index
     * @pre | index < getDependencies().size()
     * To make a sensor Neuron have more impact on super.computeOutput we can link to it twice (with the same weight)
     * The additional reference is appended at the end of getDependencies().
     * 
     * Fails silently like super.connect
     */
    public void doubleSensor(int index) {
		var deps = getDependencies();
		var p = deps.get(index);
		deps.add(p);
		setDependencies(deps);
    }
    
    
}
