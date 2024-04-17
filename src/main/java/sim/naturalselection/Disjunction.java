package sim.naturalselection;

import sim.World;
import util.Point;

public class Disjunction implements NaturalSelection
{
	private final NaturalSelection area1;
	private final NaturalSelection area2;
	
	public Disjunction(NaturalSelection area1, NaturalSelection area2)
	{

		this.area1 = area1;
		this.area2 = area2;
	}
	
	public boolean survives(World world, Point position)
	{
		if (area1.survives(world, position) || area2.survives(world, position)) {
			return true;
		}
		return false;
	}
}
