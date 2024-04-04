package sim.neuralnet;

/**
 * LEGIT
 */
public class RectifiedLinearUnitFunctionNeuron extends ActivationFunctionNeuron
{
    @Override
    public int applyActivationFunction(int input)
    {
        return Math.min(Math.max(-500, input), 1000);
    }
}
