package sim.neuralnet;

import sim.Creature;
import sim.World;

/**
 * @immutable
 */
public class VerticalPositionSensorNeuron extends SensorNeuron
{
    @Override
    /**
     * Lets creatures sense their relative vertical position in the world,
     * 
     * @inspects | world
     * @inspects | creature
     * @pre | world != null
     * @pre | creature != null
     * @post | ((result >= -1000) && (result <= 1000))
     */
    public int computeOutput(World world, Creature creature)
    {
    	float step = (world.getHeight()- 1)/2000.0f;
        float float_points = (creature.getPosition().getY()/step) - 1000;
        int int_points = Math.round(float_points);
    	return int_points;
    }
}
