package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import util.Color;


/**
 * @immutable
 * @invar | getColor().equals( Color.WHITE )
 * 
 */
public class ImmobileBehavior extends Behavior
{
	/**
     * Initializes a new ImmobileBehavior object.
     *
     * @throws IllegalArgumentException | chromosome == null
     * @post | this.getChromosome() == chromosome
     */
	public ImmobileBehavior(Chromosome chromosome)
	{
		super(chromosome);
	}
	
	
	@Override
	/**
	 * @post | result != null
	 */
	public Color getColor() {
		return Color.WHITE;
	}
	
	
    @Override
    /**
     * Applies the specific Behavior of the given Creature in the given the given world.
     * 
	 * @inspects | world
	 * @mutates | creature
	 * @pre | world != null
	 * @pre | creature != null
	 * @post | creature == old(creature)
	 */
    public void applyBehavior(World world, Creature creature)
    {
        // NOP
    }
    

    @Override
    /**
     * @creates | result
     * @pre | chromosome != null
     * @post | result.getChromosome() == chromosome
     */
    public ImmobileBehavior copyWithChromosome(Chromosome chromosome)
    {
    	return new ImmobileBehavior(chromosome);
    }
}
