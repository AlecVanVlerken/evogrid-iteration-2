package sim.neuralnet;

/**
 * @immutable
 */
public class VerticalOrientationSensorNeuron extends OrientationSensorNeuron
{
    @Override
    public int north()
    {
        return -1000;
    }

    @Override
    public int northEast()
    {
        return -500;
    }

    @Override
    public int east()
    {
        return 0;
    }

    @Override
    public int southEast()
    {
        return 500;
    }

    @Override
    public int south()
    {
        return 1000;
    }

    @Override
    public int southWest()
    {
        return 500;
    }

    @Override
    public int west()
    {
        return 0;
    }

    @Override
    public int northWest()
    {
        return -500;
    }
}
