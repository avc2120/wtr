package wtr.g1;

import wtr.sim.Point;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.*;

public class WisdomPoint extends Point
{
	public int wisdom;
	public WisdomPoint(Point p, int wisdom)
	{
		super(p.x, p.y, p.id);
		this.wisdom = wisdom;
	}

	public void setWisdom(int wisdom)
	{
		this.wisdom = wisdom;
	}

	public String toString()
	{
		return id + " " + wisdom;
	}
}