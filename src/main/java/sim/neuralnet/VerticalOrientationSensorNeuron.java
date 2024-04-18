package sim.neuralnet;

/**
 * @immutable
 */
public class VerticalOrientationSensorNeuron extends OrientationSensorNeuron
{
    @Override
    /**
     * Returns the orientation value representing north.
     * 
     * @post | result == -1000
     */
    public int north()
    {
        return -1000;
    }

    @Override
    /**
     * Returns the orientation value representing northeast.
     * 
     * @post | result == -500
     */
    public int northEast()
    {
        return -500;
    }

    @Override
    /**
     * Returns the orientation value representing east.
     * 
     * @post | result == 0
     */
    public int east()
    {
        return 0;
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
     * @post | result == 1000
     */
    public int south()
    {
        return 1000;
    }

    @Override
    /**
     * Returns the orientation value representing southwest.
     * 
     * @post | result == 500
     */
    public int southWest()
    {
        return 500;
    }

    @Override
    /**
     * Returns the orientation value representing west.
     * 
     * @post | result == 0
     */
    public int west()
    {
        return 0;
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
