package sim.neuralnet;

import sim.Creature;
import sim.World;

/**
 * @immutable
 */
public abstract class BinarySensorNeuron extends SensorNeuron
{
    @Override
    /**
     * Based on detect it will compute a value.
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     * @post | (detect(world, creature) && result == 750) || (!detect(world, creature) && result == -750)
     */
    public int computeOutput(World world, Creature creature)
    {
		if (detect(world, creature)) {
        	return 750;
        } else {
        	return -750;
        }
    }

    /**
     * Detects if the space is free in the given Orientations north, northwest and northeast from the creaturs point of view.
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     */
    public abstract boolean detect(World world, Creature creature);
}
