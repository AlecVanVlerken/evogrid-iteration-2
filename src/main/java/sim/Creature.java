package sim;

import util.Orientation;
import util.Point;
import util.Vector;
import static util.Logic.*;

import sim.behaviors.Behavior;


public class Creature
{
	/**
	 * @invar | position != null
	 * @invar | orientation != null
	 * @invar | behavior != null
	 */
	
    private Point position;
    private Orientation orientation;
    private final Behavior behavior;

    /**
     * @throws IllegalArgumentException | behavior == null || position == null || orientation == null
     * @post | this.getBehavior() == behavior
     * @post | this.getPosition().equals(position)
     * @post | this.getOrientation().equals(orientation)
     */
    public Creature(Behavior behavior, Point position, Orientation orientation)
    {
    	if (behavior == null || position == null || orientation == null) {
    		throw new IllegalArgumentException();
    	}
        this.behavior = behavior;
        this.position = position;
        this.orientation = orientation;
    }

    /**
     * @post | result != null
     */
    public Point getPosition()
    {
        return this.position;
    }

    /**
     * @post | result != null
     */
    public Orientation getOrientation() { return this.orientation; }
    
    /**
     * LEGIT
     */
    public Behavior getBehavior() {
    	return this.behavior;
    }
    

    public Chromosome getChromosome() {
    	return behavior.getChromosome();
    }

    /**
     * The current position is set (if there is room) to current pos + current orientation + drift.
     *
     * @inspects | world
     * @mutates | this
     * @pre | world != null
     * @pre | drift != null
     * @post If the destination is free, the position is set to the destination. 
     * | implies(old(world.isFree(destination(drift))), getPosition().equals(old(destination(drift))))
     * @post If the destination is not free, the position is left untouched.
     * | implies(!old(world.isFree(destination(drift))), getPosition().equals(old(getPosition())))
     * @post The orientation does not change
     * | old(getOrientation()).equals(getOrientation())
     * @post The behavior does not change
     * | old(getBehavior()) == getBehavior()
     */
    public void moveForward(World world, Vector drift)
    {
        var oldPosition = this.position;
        var newPosition = oldPosition
        		.move(this.orientation.toVector())
        		.move(drift);

        if ( world.isFree(newPosition) )
        {
            this.position = newPosition;
        }
    }
    
    /**
     * @pre | drift != null
     * @post | result != null
     * @post | result.equals(getPosition().move(getOrientation().toVector()).move(drift))
     */
    public Point destination(Vector drift)
    {
    	return position.move(orientation.toVector()).move(drift);
    }

    /**
     * @mutates | this
     * @post | getPosition().equals(old(getPosition()))
     * @post | getOrientation().isEqual(old(getOrientation().turnClockwise(1)))
     * @post | getBehavior() == old(getBehavior())
     */
    public void turnClockwise()
    {
        this.orientation = this.orientation.turnClockwise(1);
    }
    
    /**
     * @mutates | this
     * @post | getPosition().equals(old(getPosition()))
     * @post | getOrientation().isEqual(old(getOrientation().turnCounterclockwise(1)))
     * @post | getBehavior() == old(getBehavior()) 
     */
    public void turnCounterclockwise()
    {
        this.orientation = this.orientation.turnCounterclockwise(1);
    }

    /**
     * @mutates | this
     * @pre | world != null
     * @post | getBehavior() == old(getBehavior())
     */
    public void performAction(World world)
    {
        this.behavior.applyBehavior(world, this);
    }
    
    /**
     * true iff same position and orient and chromosome and behavior type
     * The getClass method can be used to retrieve the runtime type of an object.
     * 
     * @inspects | other
     */
    public boolean isEqual(Creature other) {
    	return (other != null) &&
    			(this.position.equals(other.getPosition()))
    			&& (this.orientation.isEqual(other.getOrientation()))
    			&& (this.getChromosome().isEqual(other.getChromosome()))
    			&& (this.getBehavior().getClass() == other.getBehavior().getClass());
    }
    
    /**
     * @creates | result
     * @post | result != null
     * @post | result.getBehavior() == getBehavior()
     * @post | result.getPosition() == getPosition()
     * @post | result.getOrientation() == getOrientation()
     * @post | result.getChromosome().isEqual(getChromosome())
     */
    public Creature giveCopy() {
    	return new Creature(this.behavior, this.position, this.orientation);
    }
}
