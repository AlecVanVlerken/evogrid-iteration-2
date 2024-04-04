package sim;

import java.util.Arrays;

import util.Point;



public class World
{

    private final int width;


    private final int height;

    /**
     * @representationObject
     * @representationObjects
     * @invar | population != null
     * @invar each creature is in the field
     *   | Arrays.stream(population).allMatch(c -> c != null && Point.isWithin(c.getPosition(), width, height))
     */
    private final Creature[] population;



    public World(int width, int height, Creature[] pop)
    {
        this.width = width;
        this.height = height;
        this.population = new Creature[pop.length];
        for (int i = 0 ; i < pop.length ; i++) {
        	this.population[i] = pop[i].giveCopy();
        }
    }


    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public Creature[] getPopulation()
    {
        return null;
    }



    /**
     * LEGIT
     * 
     * @pre | position != null
     * @post | result == (0 <= position.getX() && position.getX() < getWidth() && 0 <= position.getY() && position.getY() < getHeight())
     */
    public boolean isInside(Point position)
    {
    	return Point.isWithin(position, width, height);
    }

    /**
     * LEGIT
     * 
     * Returns true iff pos is 1 (simulation) pixel away from a wall (and inside the world)
     *
     * @pre | pos != null
     * @post | result == (isInside(pos) &&
     *       | (0 == pos.getX() || pos.getX() == getWidth() -1 || pos.getY() == 0 || pos.getY() == getHeight()-1))
     */
    public boolean isLimPos(Point pos) {
    	return isInside(pos) &&
    		(0 == pos.getX() || pos.getX() == getWidth() - 1 || pos.getY() == 0 || pos.getY() == getHeight()-1);
    }

    /**
     * LEGIT
     * 
     * @pre | array1 != null && array2 != null
     * @pre | array1.length == array2.length
     */
    public static boolean areEqualCreatureArrays(Creature[] array1, Creature[] array2) {
    	boolean res = true;
    	for (int i = 0 ; i < array1.length ; i++) {
    		res = res && array1[i].isEqual(array2[i]);
    	}
    	return res;
    }

    /**
     * true iff position is inside the world and no creature sits there
     *
     * @pre | position != null
     */
    public boolean isFree(Point position)
    {
        if ( !this.isInside(position) )
        {
            return false;
        }

        return !Arrays.stream(this.population).anyMatch(c -> c.getPosition() == position) ;
    }

    /**
     * Performs the action of each creature
     * Exceptionally, you may ignore further specifying this method.
     *
     * @mutates | getPopulation()
     */
    public void step()
    {
    	
    }
}
