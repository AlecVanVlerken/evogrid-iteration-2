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

	private Chromosome chromosome;
	
	public Behavior(Chromosome chromosome)
	{
		this.chromosome = chromosome;
	}
	
	public Chromosome getChromosome()
	{
		return this.chromosome;
	}
		
    public abstract void applyBehavior(World world, Creature creature);
    
    /**
     * LEGIT
     */
    public Color getColor() {
    	return Color.BLACK;
    }
    
    /**
     * post: the copy has the specified chromosome
     * @creates | result
     */
    public abstract Behavior copyWithChromosome(Chromosome chromosome);
}


