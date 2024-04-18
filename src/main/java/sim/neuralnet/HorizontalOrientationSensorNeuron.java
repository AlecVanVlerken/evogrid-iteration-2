package sim.neuralnet;

/**
 * @immutable
 */
public class HorizontalOrientationSensorNeuron  extends OrientationSensorNeuron
{
    @Override
    /**
     * Returns the orientation value representing north.
     * 
     * @post | result == 0
     */
    public int north()
    {
        return 0;
    }

    @Override
    /**
     * Returns the orientation value representing northeast.
     * 
     * @post | result == 500
     */
    public int northEast()
    {
        return 500;
    }

    @Override
    /**
     * Returns the orientation value representing east.
     * 
     * @post | result == 1000
     */
    public int east()
    {
        return 1000;
    }

    @Override
    /**
     * Returns the orientation value representing southeast.
     * 
     * @post | result == 500
     */
    public int southEast()
    {
        return 500;
    }

    @Override
    /**
     * Returns the orientation value representing south.
     * 
     * @post | result == 0
     */
    public int south()
    {
        return 0;
    }

    @Override
    /**
     * Returns the orientation value representing southwest.
     * 
     * @post | result == -500
     */
    public int southWest()
    {
        return -500;
    }

    @Override
    /**
     * Returns the orientation value representing west.
     * 
     * @post | result == -1000
     */
    public int west()
    {
        return -1000;
    }

    @Override
    /**
     * Returns the orientation value representing northwest.
     * 
     * @post | result == -500
     */
    public int northWest()
    {
        return -500;
    }
}
