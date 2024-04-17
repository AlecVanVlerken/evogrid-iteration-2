package sim.neuralnet;

import sim.Creature;
import sim.World;


public class HorizontalPositionSensorNeuron extends SensorNeuron
{
    @Override
    public int computeOutput(World world, Creature creature)
    {
        float step = (world.getWidth()- 1)/2000;
        float float_points = (creature.getPosition().getX()/step) - 1000;
        int int_points = Math.round(float_points);
    	return int_points;
    }
}
