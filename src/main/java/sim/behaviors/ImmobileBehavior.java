package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import util.Color;


/**
 * @invar | getColor() .equals( Color.WHITE )
 * 
 */
public class ImmobileBehavior extends Behavior
{
	public ImmobileBehavior(Chromosome chromosome)
	{
		super(chromosome);
	}
	
	
	@Override
	public Color getColor() {
		return Color.WHITE;
	}
	
	
    @Override
    public void applyBehavior(World world, Creature creature)
    {
        // NOP
    }
    

    @Override
    public ImmobileBehavior copyWithChromosome(Chromosome chromosome)
    {
    	return new ImmobileBehavior(chromosome);
    }
}
