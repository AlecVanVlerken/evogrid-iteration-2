package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import util.Color;

/**
 * @immutable
 */
public abstract class Behavior
{
	/**
	 * @invar | chromosome != null
	 */
	private Chromosome chromosome;
	
	/**
     * Initializes a new Behavior object.
     *
     * @throws IllegalArgumentException | chromosome == null
     * @post | this.getChromosome() == chromosome
     */
	public Behavior(Chromosome chromosome)
	{
		if (chromosome == null) {throw new IllegalArgumentException(); }
		this.chromosome = chromosome;
	}
	
	/**
     * @post | result != null
     */
	public Chromosome getChromosome()
	{
		return this.chromosome;
	}
	
	/**
	 * Applies the specific Behavior of the given Creature in the given the given world.
	 * 
	 * @inspects | world
	 * @mutates | creature
	 * @pre | world != null
	 * @pre | creature != null
	 */
    public abstract void applyBehavior(World world, Creature creature);
    
    /**
     * LEGIT
     */
    public Color getColor() {
    	return Color.BLACK;
    }
    
    /**
     * @creates | result
     * @pre | chromosome != null
     * @post | result.getChromosome() == chromosome
     */
    public abstract Behavior copyWithChromosome(Chromosome chromosome);
}


