package sim;

import java.util.Arrays;

import util.Point;



public class World
{
	
	/**
	 * @invar | width >= 0
	 */
    private final int width;

    /**
	 * @invar | height >= 0
	 */
    private final int height;

    /**
     * @representationObject
     * @representationObjects
     * @invar | population != null
     * @invar each creature is in the field
     *   | Arrays.stream(population).allMatch(c -> c != null && Point.isWithin(c.getPosition(), width, height))
     */
    private final Creature[] population;


    /**
     * Initializes a new World object.
     *
     * @inspects | pop
     * @throws IllegalArgumentException | width < 0
     * @throws IllegalArgumentException | height < 0
     * @throws IllegalArgumentException | pop == null || pop.length < 0
     * @throws IllegalArgumentException | Arrays.stream(pop).anyMatch(c -> c == null)
     * @throws IllegalArgumentException | Arrays.stream(pop).anyMatch(c -> !Point.isWithin(c.getPosition(), width, height))
     * @post | this.getWidth() == width
     * @post | this.getHeight() == height
     * @post | areEqualCreatureArrays(pop, getPopulation())
     */
    public World(int width, int height, Creature[] pop)
    {
    	if (pop == null || pop.length < 0 || width < 0 || height < 0 || Arrays.stream(pop).anyMatch(c -> c == null) || !Arrays.stream(pop).anyMatch(c -> Point.isWithin(c.getPosition(), width, height))) {throw new IllegalArgumentException(); }
        this.width = width;
        this.height = height;
        this.population = new Creature[pop.length];
        for (int i = 0 ; i < pop.length ; i++) {
        	this.population[i] = pop[i].giveCopy();
        }
    }


    /**
	 * @post | result >= 0
	 */
    public int getWidth() { return this.width; }

    /**
	 * @post | result >= 0
	 */
    public int getHeight() { return this.height; }

    /**
	 * @post | result != null
	 * @post | Arrays.stream(result).anyMatch(c -> c != null && Point.isWithin(c.getPosition(), getWidth(), getHeight()))
	 */
    public Creature[] getPopulation()
    {
    	Creature[] copyPopulation = new Creature[population.length];
    	for (int i = 0 ; i < population.length ; i++) {
        	copyPopulation[i] = population[i].giveCopy();
        }
        return copyPopulation;
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
     * @post | result == ( this.isInside(position) && (!Arrays.stream(getPopulation()).anyMatch(c -> c.getPosition().equals(position))))
     */
    public boolean isFree(Point position)
    {
        if ( !this.isInside(position) )
        {
            return false;
        }

        return !Arrays.stream(this.population).anyMatch(c -> c.getPosition().equals(position)) ;
    }

    /**
     * Performs the action of each creature
     * Exceptionally, you may ignore further specifying this method.
     *
     * @mutates | getPopulation()
     */
    public void step()
    {
    	for (int i = 0 ; i < population.length ; i ++)
        {
            population[i].performAction(this);
        }
    }
}
