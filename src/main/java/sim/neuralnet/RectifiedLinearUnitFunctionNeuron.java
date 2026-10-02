package sim.neuralnet;

/**
 * LEGIT
 */
public class RectifiedLinearUnitFunctionNeuron extends ActivationFunctionNeuron
{
    /**
     * Retains the inherited signed forward clamp despite the class name.
     * Only strictly positive outputs request forward movement.
     *
     * @post | result == Math.min(Math.max(-500, input), 1000)
     */
    @Override
    public int applyActivationFunction(int input)
    {
        return Math.min(Math.max(-500, input), 1000);
    }
}
