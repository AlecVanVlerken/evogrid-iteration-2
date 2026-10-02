package util;

/**
 * @immutable
 * 
 * LEGIT
 */
public class Vector
{
    private final int x;
    private final int y;

    /**
     * Initializes a new Vector object.
     *
     * @post | this.getX() == x
     * @post | this.getY() == y
     */
    public Vector(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns this Vector's x coordinate.
     */
    public int getX() { return this.x; }

    /**
     * Returns this Vector's y coordinate.
     */
    public int getY() { return this.y; }
    
    /**
     * @post | result != null
     * @post | result.getX() == this.getX() * fac
     * @post | result.getY() == this.getY() * fac
     */
    public Vector scaleWith(int fac) {
    	return new Vector( fac * x , fac * y);
    }
    
    /**
     * @post | result != null
     * @post | result.getX() == this.getX() + other.getX()
     * @post | result.getY() == this.getY() + other.getY()
     */
    public Vector plus(Vector other) {
    	return new Vector( x + other.getX(), y + other.getY());
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Vector v)
        {
            return this.x == v.x && this.y == v.y;
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
        return String.format("Vector(%d, %d)", this.x, this.y);
    }
}
