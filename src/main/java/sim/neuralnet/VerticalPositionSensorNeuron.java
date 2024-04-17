package sim.neuralnet;

import sim.Creature;
import sim.World;

/**
 * @immutable
 */
public class VerticalPositionSensorNeuron extends SensorNeuron
{
    @Override
    public int computeOutput(World world, Creature creature)
    {
    	float step = (world.getHeight()- 1)/2000.0f;
        float float_points = (creature.getPosition().getY()/step) - 1000;
        int int_points = Math.round(float_points);
    	return int_points;
    }
}
