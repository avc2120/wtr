package wtr.g1;
import wtr.g1.WisdomPoint;
import wtr.sim.Point;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.*;
public class Player implements wtr.sim.Player {

	// your own id
	private int self_id = -1;

	// the remaining wisdom per player
	private int[] W = null;

	// random generator
	private Random random = new Random();
	private ArrayList<Integer> friends = new ArrayList<Integer>();
	private ArrayList<Point> friends_in_zone = new ArrayList<Point>();
	private ArrayList<Integer> wisdom_queue = new ArrayList<Integer>();
	private PriorityQueue<WisdomPoint> wisdomQ = new PriorityQueue<WisdomPoint>(new Comparator<WisdomPoint>(){
	
		public int compare(WisdomPoint a,WisdomPoint b)
		{
			return b.wisdom - a.wisdom;
		}
	});


	// init function called once
	public void init(int id, int[] friend_ids, int strangers)
	{
		for (int x=0; x < friend_ids.length; x++)
			friends.add(friend_ids[x]);
		// friends = new ArrayList<Integer>(Arrays.asList(friend_ids));
		self_id = id;
		// initialize the wisdom array
		int N = friend_ids.length + strangers + 2;
		W = new int [N];
		for (int i = 0 ; i != N ; ++i)
			W[i] = i == self_id ? 0 : -1;
		for (int friend_id : friend_ids)
			W[friend_id] = 50;
		// wisdom_queue.addAll(Arrays.asList(W));
	}

	// play function
	public Point play(Point[] players, int[] chat_ids,
	                  boolean wiser, int more_wisdom)
	{
		friends_in_zone.clear();
		for(Point player: players)
		{
			if(friends.contains(player.id))
			{
				friends_in_zone.add(player);
			}
		}
		// find where you are and who you chat with
		int i = 0, j = 0;
		while (players[i].id != self_id) 
		{
			i++;
		}
		//who im chatting with
		while (players[j].id != chat_ids[i]) 
		{
			j++;
		}
		Point self = players[i];
		Point chat = players[j];
		// record known wisdom
		W[chat.id] = more_wisdom;
		wisdomQ.add(new WisdomPoint(chat, W[chat.id]));
		// attempt to continue chatting if there is more wisdom
		if (wiser) 
			return new Point(0.0, 0.0, chat.id);
		// try to initiate chat if previously not chatting
		double mindist = Double.MAX_VALUE;
		Point next_friend = null;
		if (i == j)
			for (Point p : friends_in_zone) {
				// skip if no more wisdom to gain
				if (W[p.id] == 0) continue;
				// compute squared distance
				double dd = distance(self, p);
				if(dd < mindist)
				{
					mindist = dd;
					next_friend = p;
				}
				// start chatting if in range
				// Iterator<WisdomPoint> it = wisdomQ.iterator();
				// if (dd >= 0.25 && dd <= 4.0 && W[p.id]>0 && !wisdomQ.contains(p.id))
				// {
				// 	// return new Point(0.0, 0.0, p.id);
				// 	wisdomQ.add(new WisdomPoint(p, W[p.id]));
				// }
				// else
				// {
				// 	while(it.hasNext())
				// 	{
				// 		WisdomPoint wp=it.next();
				// 		if (wp.id == p.id)
				// 		{
				// 			wp.setWisdom(W[p.id]);
				// 		}
				// 	}
				// }
			}
			if(mindist != Double.MAX_VALUE && next_friend!=null)
			{
				return new Point(0, 0, next_friend.id);
			}
			// if(!wisdomQ.isEmpty())
			// {
			// 	// System.out.println("POLLING");
			// 	System.out.println(wisdomQ);
			// 	W[wisdomQ.peek().id]-= 6;
			// 	return new Point(0.0, 0.0, wisdomQ.poll().id);
			// }
			for (Point p : players) {
				// skip if no more wisdom to gain
				if (W[p.id] == 0) continue;
				// compute squared distance
				double dd = distance(self, p);
				if(dd < mindist)
				{
					mindist = dd;
					next_friend = p;
				}
			}
			if(mindist != Double.MAX_VALUE && next_friend!=null)
			{
				return new Point(0, 0, next_friend.id);
			}
		// return a random move
		double dir = random.nextDouble() * 2 * Math.PI;
		double dx = 6 * Math.cos(dir);
		double dy = 6 * Math.sin(dir);
		return new Point(dx, dy, self_id);
	}

	public static double distance(Point p1, Point p2)
	{
		double dx = p1.x - p2.x;
		double dy = p1.y - p2.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
}
