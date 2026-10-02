package sim.neuralnet;

import sim.Creature;
import sim.World;
import util.Orientation;

/**
 * @immutable
 * LEGIT
 */
public abstract class OrientationSensorNeuron extends SensorNeuron
{
    /**
     * Senses the heading value, including independently constructed orientations.
     *
     * @inspects | world, creature
     * @pre | world != null && creature != null
     */
    @Override
    public int computeOutput(World world, Creature creature)
    {
        var orientation = creature.getOrientation();

        if ( orientation.isEqual(Orientation.north()) )
        {
            return this.north();
        }
        else if ( orientation.isEqual(Orientation.northEast()) )
        {
            return this.northEast();
        }
        else if ( orientation.isEqual(Orientation.east()) )
        {
            return this.east();
        }
        else if ( orientation.isEqual(Orientation.southEast()) )
        {
            return this.southEast();
        }
        else if ( orientation.isEqual(Orientation.south()) )
        {
            return this.south();
        }
        else if ( orientation.isEqual(Orientation.southWest()) )
        {
            return this.southWest();
        }
        else if ( orientation.isEqual(Orientation.west()) )
        {
            return this.west();
        }
        else
        {
            return this.northWest();
        }
    }

    public abstract int north();

    public abstract int northEast();

    public abstract int east();

    public abstract int southEast();

    public abstract int south();

    public abstract int southWest();

    public abstract int west();

    public abstract int northWest();
}
