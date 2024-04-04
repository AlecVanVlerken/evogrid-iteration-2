package sim.neuralnet;

import sim.Creature;
import sim.World;


public abstract class BinarySensorNeuron extends SensorNeuron
{
    @Override
    public int computeOutput(World world, Creature creature)
    {
        return 0;
    }

    public abstract boolean detect(World world, Creature creature);
}
