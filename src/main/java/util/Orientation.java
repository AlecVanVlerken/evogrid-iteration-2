package util;

import java.util.List;

/**
 * LEGIT
 * 
 * Represents one of the eight cardinal (N, S, W, E) and intercardinal (NE, NW, SE, SW) directions.
 * https://en.wikipedia.org/wiki/Cardinal_direction
 *
 *
 * @immutable
 */
public class Orientation
{
    /**
     * @post | orientations().contains(result)
     */
    public static Orientation createRandom()
    {
        var index = RandomUtil.integer(orientationTable.length);

        return orientationTable[index];
    }

    /**
     * @invar | 0 <= orientationIndex && orientationIndex < 8
     */
    private final int orientationIndex;

    private final static int NORTH = 0;
    private final static int NORTH_EAST = 1;
    private final static int EAST = 2;
    private final static int SOUTH_EAST = 3;
    private final static int SOUTH = 4;
    private final static int SOUTH_WEST = 5;
    private final static int WEST = 6;
    private final static int NORTH_WEST = 7;

    private final static Orientation[] orientationTable = new Orientation[] {
        new Orientation(NORTH),
        new Orientation(NORTH_EAST),
        new Orientation(EAST),
        new Orientation(SOUTH_EAST),
        new Orientation(SOUTH),
        new Orientation(SOUTH_WEST),
        new Orientation(WEST),
        new Orientation(NORTH_WEST)
    };

    private final static Vector[] vectorTable = new Vector[] {
        new Vector(0, -1),
        new Vector(1, -1),
        new Vector(1, 0),
        new Vector(1, 1),
        new Vector(0, 1),
        new Vector(-1, 1),
        new Vector(-1, 0),
        new Vector(-1, -1)
    };

    private final static String[] nameTable = new String[] {
            "north",
            "north east",
            "east",
            "south east",
            "south",
            "south west",
            "west",
            "north west"
    };


    public static Orientation north()
    {
        return orientationTable[NORTH];
    }


    public static Orientation northEast()
    {
        return orientationTable[NORTH_EAST];
    }


    public static Orientation east()
    {
        return orientationTable[EAST];
    }


    public static Orientation southEast()
    {
        return orientationTable[SOUTH_EAST];
    }


    public static Orientation south()
    {
        return orientationTable[SOUTH];
    }


    public static Orientation southWest()
    {
        return orientationTable[SOUTH_WEST];
    }


    public static Orientation west()
    {
        return orientationTable[WEST];
    }


    public static Orientation northWest()
    {
        return orientationTable[NORTH_WEST];
    }

    /**
     * orientation should be 0 <= _ < 8
     */
    public Orientation(int orientation)
    {
    	if (orientation < 0 || orientation >= 8) {throw new IllegalArgumentException(); }
        this.orientationIndex = orientation;
    }


    public Orientation turnClockwise(int increments)
    {
        return fromIndex(this.orientationIndex + increments);
    }

    public Orientation turnCounterclockwise(int increments)
    {
        return turnClockwise(-increments);
    }


    public Orientation compose(Orientation other)
    {
        return fromIndex(this.orientationIndex + other.orientationIndex);
    }


    /**
     * Returns an unmodifiable catalog of the eight canonical directions.
     *
     * @post | result != null && result.size() == 8
     * @post | result.stream().allMatch(o -> o != null)
     */
    public static List<Orientation> orientations()
    {
        return List.of(orientationTable);
    }


    private static Orientation fromIndex(int index)
    {
        return orientationTable[Math.floorMod(index, orientationTable.length)];
    }


    public Vector toVector()
    {
        return vectorTable[this.orientationIndex];
    }


    @Override
    public String toString()
    {
        return nameTable[this.orientationIndex];
    }
    
    public boolean isEqual(Orientation other) {
    	return (other != null) && (this.orientationIndex == other.orientationIndex);
    }
}
