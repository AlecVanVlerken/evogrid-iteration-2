package sim.neuralnet;

import java.util.Arrays;

import sim.Chromosome;
import util.Orientation;

/**
 * Output neurons depend at least on all input neurons, if they depend on something.
 * All Neuron fields are freely accessible by the client.
 */
public class NeuralNetwork
{


	/**
	 * @representationObject 
	 */
    private final SensorNeuron[] inputLayerNeurons;



    private final ActivationFunctionNeuron moveForwardNeuron;

    private final ActivationFunctionNeuron turnClockwiseNeuron;
    
    private final ActivationFunctionNeuron turnCounterclockwiseNeuron;

    /**
     * Returns a NeuralNetwork with 7 input neurons and 3 output neurons, as described in assignment,
     * but with no connections.
     */
    public NeuralNetwork()
    {

    	//this block of code is LEGIT
        this.inputLayerNeurons = new SensorNeuron[] {
    		new FreePassageSensorNeuron(Orientation.north()),
    		new FreePassageSensorNeuron(Orientation.northWest()),
    		new FreePassageSensorNeuron(Orientation.northEast()),
    		new HorizontalOrientationSensorNeuron(),
            new VerticalOrientationSensorNeuron(),
            new HorizontalPositionSensorNeuron(),
            new VerticalPositionSensorNeuron()
        };
        this.moveForwardNeuron = new RectifiedLinearUnitFunctionNeuron();
        this.turnClockwiseNeuron = new LinearFunctionNeuron();
        this.turnCounterclockwiseNeuron = new LinearFunctionNeuron();
        

    }



	public SensorNeuron[] getInputNeurons()
    {
        return inputLayerNeurons;
    }

    public ActivationFunctionNeuron[] getOutputNeurons()
    {
        return null;
        //please use this specific order (different than the order of the fields above):
        // - moveForwardNeuron
        // - turnCounterclockwiseNeuron
        // - turnClockwiseNeuron
        
    }

    public ActivationFunctionNeuron getMoveForwardNeuron() { return this.moveForwardNeuron; }

    public ActivationFunctionNeuron getTurnClockwiseNeuron() { return this.turnClockwiseNeuron; }

    public ActivationFunctionNeuron getTurnCounterclockwiseNeuron() { return this.turnCounterclockwiseNeuron; }

    /**
     * @pre | chromosome != null
     * post:
     * - getInputNeurons() and the first 7 genes of chromosome are used as dependencies/weights of getMoveForwardNeuron()
     * - getInputNeurons() and the next 7 genes of chromosome are used as dependencies/weights of getTurnCounterclockwiseNeuron()
     * - getInputNeurons() and the next 7 genes of chromosome are used as dependencies/weights of getTurnClockwiseNeuron()
     * - The 3 remaining genes are used as biases of
     *   getMoveForwardNeuron(), getTurnCounterclockwiseNeuron(), getTurnClockwiseNeuron()
     */
    public static NeuralNetwork fromChromosome(Chromosome chromosome)
    {
        return null;
    }


}
