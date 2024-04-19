package sim.neuralnet;

import sim.Creature;
import sim.World;

/**
 * @immutable
 */
public class HorizontalPositionSensorNeuron extends SensorNeuron
{
    @Override
    /**
     * Lets creatures sense their relative horizontal position in the world,
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     * @post | ((result >= -1000) && (result <= 1000))
     */
    public int computeOutput(World world, Creature creature)
    {
        float step = (world.getWidth()- 1)/2000.0f;
        float float_points = ((creature.getPosition().getX())/step) - 1000;
        int int_points = Math.round(float_points);
    	return int_points;
    }
}
