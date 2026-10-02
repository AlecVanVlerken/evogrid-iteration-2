package util;

/**
 * LEGIT
 * 
 * @immutable
 */
public class Point
{
    /**
     * @post | result != null
     * @post ! 0 <= result.getX() && result.getX() < maxX
     * @post ! 0 <= result.getY() && result.getY() < maxY
     */
    public static Point createRandom(int maxX, int maxY)
    {
        var x = RandomUtil.integer(maxX);
        var y = RandomUtil.integer(maxY);

        return new Point(x, y);
    }

    private final int x;

    private final int y;

    /**
     * Initializes a new Point object.
     *
     * @post | this.getX() == x
     * @post | this.getY() == y
     */
    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the X-coordinate of the Point.
     */
    public int getX()
    {
        return this.x;
    }

    /**
     * Returns the Y-coordinate of the Point.
     */
    public int getY()
    {
        return this.y;
    }

    /**
     * @pre | displacement != null
     * @post | result.getX() == this.getX() + displacement.getX()
     * @post | result.getY() == this.getY() + displacement.getY()
     */
    public Point move(Vector displacement)
    {
        int x = this.x + displacement.getX();
        int y = this.y + displacement.getY();

        return new Point(x, y);
    }

    /**
     * @pre | other != null
     * @post | result == (this.getX() - other.getX()) * (this.getX() - other.getX()) + (this.getY() - other.getY()) * (this.getY() - other.getY())
     */
    public int distanceSquared(Point other)
    {
        var dx = other.getX() - this.getX();
        var dy = other.getY() - this.getY();

        return dx * dx + dy * dy;
    }



    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Point p)
        {
            return this.x == p.x && this.y == p.y;
        } else
        {
            return false;
        }
    }

    /**
     * Equal coordinate values have equal hash codes.
     *
     * @post | result == 31 * getX() + getY()
     */
    @Override
    public int hashCode()
    {
        return 31 * x + y;
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d)", this.x, this.y);
    }
    
    /**
     * Determines whether pos is a point belonging to a rectangle with sides width, height
     * 
     * @pre | pos != null
     * @pre | width >= 0
     * @pre | height >= 0
     * @post | result == (0 <= pos.getX() && pos.getX() < width && 0 <= pos.getY() && pos.getY() < height)
     */
    public static boolean isWithin(Point pos, int width, int height) {
    	return 0 <= pos.getX() && pos.getX() < width && 0 <= pos.getY() && pos.getY() < height;
    }
}
