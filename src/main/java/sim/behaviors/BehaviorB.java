package sim.behaviors;

import sim.Chromosome;
import sim.Creature;
import sim.World;
import util.Color;
import util.RandomUtil;
import util.Vector;
import static util.Logic.*;

/**
 * @immutable
 * @invar | getColor().equals( Color.BLUE )
 *
 */
public class BehaviorB extends Behavior
{
	/**
     * Initializes a new BehaviorB object.
     *
     * @throws IllegalArgumentException | chromosome == null
     * @post | this.getChromosome() == chromosome
     */
	public BehaviorB(Chromosome chromosome)
	{
		super(chromosome);
	}

	
	@Override
	/**
	 * @post | result != null
	 */
	public Color getColor() {
		return Color.BLUE;
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
        
        //creatures that are not touching a wall are not done
        if (!world.isLimPos(position)) {
        	creature.moveForward(world, new Vector(0,0));
        	//if it has not moved, it turns randomly
        	if (creature.getPosition().equals(position)) {
            	boolean turnleft = RandomUtil.bool();
            	if (turnleft) { creature.turnCounterclockwise(); }
            	else {creature.turnClockwise(); }
        	}
        }
    }
	

	@Override
	/**
     * @creates | result
     * @pre | chromosome != null
     * @post | result.getChromosome() == chromosome
     */
	public BehaviorB copyWithChromosome(Chromosome chromosome)
    {
    	return new BehaviorB(chromosome);
    }
    

}