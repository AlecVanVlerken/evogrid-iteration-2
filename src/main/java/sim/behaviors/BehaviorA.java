package sim.behaviors;


import util.Color;
import util.Orientation;
import util.RandomUtil;
import static util.Logic.*;

import sim.Chromosome;
import sim.Constants;
import sim.Creature;
import sim.World;

/**
 * @immutable
 * @invar | getColor().equals( Color.RED)
 *
 */
public class BehaviorA extends Behavior
{
	/**
     * Initializes a new BehaviorA object.
     *
     * @throws IllegalArgumentException | chromosome == null
     * @post | this.getChromosome() == chromosome
     */
	public BehaviorA(Chromosome chromosome)
	{
		super(chromosome);
	}
	
	@Override
	/**
	 * @post | result != null
	 */
    public Color getColor() {
    	return Color.RED;
    }
	
	
	/**
	 * Applies the specific Behavior of the given Creature in the given the given world.
	 * 
	 * @inspects | world
	 * @mutates | creature
	 * @pre | world != null
	 * @pre | creature != null
	 * @post If the creature is located at the edge of the world, it does not change its position.
	 * 	| implies(world.isLimPos(old(creature.getPosition())), old(creature.getPosition()).equals(creature.getPosition()))
	 * @post If the creature is located at the edge of the world, it does not change its orientation. 
	 * | implies(world.isLimPos(old(creature.getPosition())), old(creature.getOrientation()).equals(creature.getOrientation()))
	 * @post If the creature is not located at the edge of the world, it either moves or it turns.
	 * | implies(!world.isLimPos(old(creature.getPosition())), iff(old(creature.getPosition()).equals(creature.getPosition()), !old(creature.getOrientation()).equals(creature.getOrientation())))
	 */
	@Override
    public void applyBehavior(World world, Creature creature)
    {
        var position = creature.getPosition();
        var drift = computeFavoriteOrientation().toVector();

        if (!world.isLimPos(position)) {
        	creature.moveForward(world,  drift );
        	//if it has not moved, it turns randomly
        	if (creature.getPosition().equals(position)) {
            	boolean turnleft = RandomUtil.bool();
            	if (turnleft) { creature.turnCounterclockwise(); }
            	else {creature.turnClockwise(); }
        	}
        }
    }
    

    
	/**
	 * LEGIT
	 * 
	 * The fav. orientation of a creature depends on its 3 first genes.
	 * 
	 */
    private Orientation computeFavoriteOrientation() {
    	
    	int mid = Constants.GENE_MIN + ((Constants.GENE_MAX - Constants.GENE_MIN)/2);
    	
    	int bit0 = (getChromosome().getGene(0) <= mid) ? 0 : 1;
    	int bit1 = (getChromosome().getGene(1) <= mid) ? 0 : 1;
    	int bit2 = (getChromosome().getGene(2) <= mid) ? 0 : 1;
    	
    	int res = (bit2 << 2) | (bit1 << 1) | bit0;
    	if (! (0 <= res && res  <= 7)) {
    		throw new ArithmeticException("Average abs. gene value not in the expected range");
    	}
    	
    	return Orientation.orientations().get(res);
    }
    
    @Override
    /**
     * @creates | result
     * @pre | chromosome != null
     * @post | result.getChromosome() == chromosome
     */
    public BehaviorA copyWithChromosome(Chromosome chromosome)
    {
    	return new BehaviorA(chromosome);
    }
}