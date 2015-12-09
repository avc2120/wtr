package wtr.g0;

import wtr.sim.Point;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

public class Player implements wtr.sim.Player {

    public static final double MIN_DISTANCE = 1.0;
    // your own id
    private int self_id = -1;
    private int turns_waited = 0;
    // random generator
    private Random random = new Random();
    private boolean canGatherAllWisdom = false;

    StateInfo state;
    private int unExploredWisdom = 0;
    private int notMetStrangersCount = 0;

    // init function called once
    public void init(int id, int[] friend_ids, int strangers) {
        canGatherAllWisdom = (1800 * 1.5 < (friend_ids.length * 50 + 400 + strangers * 10));
        self_id = id;
        state=new StateInfo();
        state.addFriends2WisdomInfo(friend_ids);
        unExploredWisdom = 400 + 10 * strangers;
    }

    // play function
    public Point play(Point[] players, int[] chat_ids,
                      boolean wiser, int more_wisdom) {
        
    	// 1. who am I and where am I 
        int i = 0, j = 0;
        while (players[i].id != self_id) i++; //i = index of self in players...
        while (players[j].id != chat_ids[i]) j++; //j = index of our chat player other side
        Point self = players[i];
        Point chat = players[j];
        //2.  reset wait chances...
        if (chat.id != state.getLast_chat())        turns_waited = 0;
        
        //3. rest player Info
        state.reset(players,chat_ids);

        //4. update wisdom of person we are talking with in our Analytics
        updateWisdomInfo(chat,   wiser,   more_wisdom) ;

        //5. should we wait or move on ??
        if (wiser==false){ //currently not wise
        	//but has wisdom for sharing in future , then wait
        	if(state.hasMoreWisdomForShare(chat.id)) {
        		turns_waited=turns_waited+1;
        		int maxThreshold=interfereThreshold(chat.id);
	            if ( maxThreshold < turns_waited  ) {
	                state.updateSkip_player(chat.id);
	            }
        	}
        }
        //6. if person is wiser.. continue chat.. GO close if needed 
        if (wiser) {
            state.setLast_chat(chat.id);
            if (Math.sqrt(distance_squared(self, chat)) > MIN_DISTANCE) {
                return comeCloseAndTalk(self, chat, chat.id);//TODO : self.id
            }

            return new Point(0.0, 0.0, chat.id);
        }
        // 6. Find new person, if we have already waited too long for the current chat player
        //   goto nearest person.
    	// try to initiate New chat if previously not chatting
        if (i == j || state.shouldSkipCurrentPlayer()) {
            Point candidate=findOptions(self,  players,  chat_ids,
                     wiser,  more_wisdom);
            if(candidate!=null) 
            	return candidate;
        }
        // return a random move
        return randomMove(players, self);
    }
    
	Point findOptions(Point self, Point[] players, int[] chat_ids,
            boolean wiser, int more_wisdom){

	    
	    Point closestTarget = state.nearest(players, self);
		
	    if (closestTarget != null) {
	        return closestTarget;
	    } 
		   
	    //if no player in our range .. then what ?
	    Point maxWisdomTarget = state.greedyOnWisdom(self_id, players, chat_ids, self);
	    if (maxWisdomTarget != null) {
	        if (Math.sqrt(distance_squared(self, maxWisdomTarget)) < MIN_DISTANCE) {
	            return maxWisdomTarget;
	        } else {
	            return comeCloseAndTalk(self, maxWisdomTarget, maxWisdomTarget.id); 
	        }
	    } 
	    return null;
    }
 
    public void updateWisdomInfo(Point chat,  boolean wiser, int more_wisdom){
    	   if (!state.getWisdomMap().containsKey(chat.id)) {
               state.getWisdomMap().put(chat.id, new WisdomPoint(chat.id, more_wisdom));
               unExploredWisdom = unExploredWisdom - more_wisdom;
               notMetStrangersCount--;
           } else {
               state.getWisdomMap().get(chat.id).setWisdomRemaining(more_wisdom);
           }
    }
    /**
     * get nearer...
     */   
	public Point comeCloseAndTalk2(Point self, Point chat,   int id)
	{
		double slope = Math.atan2(chat.y - self.y, chat.x - self.x);
		double xOffset = (0.52) * Math.cos(slope);
		double yOffset = (0.52) * Math.sin(slope);
		return new Point(((chat.x - xOffset) - self.x), ((chat.y - yOffset) - self.y), id);		
	}
    public Point comeCloseAndTalk(Point self, Point target, int id) {
        double targetDis = 0.52;
        double dis = Math.sqrt(distance_squared(self, target));
        double x = (dis - targetDis) * (target.x - self.x) / dis;
        double y = (dis - targetDis) * (target.y - self.y) / dis;
        return new Point(x, y, id);
    }

    /**
     *  distance
     */
	private double distance_squared(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return dx * dx + dy * dy;
	}
 
    public int interfereThreshold(int id) {
    	//Dont wait for strangers  
        if (canGatherAllWisdom && !state.getWisdomMap().get(id).isFriend) {
        	 return 0;
        }

        return state.getWisdomMap().get(id).wisdomLeft / 10;
    }
    
	private boolean has_players_within_radius(Point player, Point[] players, Point self, double r) {
		for (Point p : players) {
			if (p == player || p == self) {
				continue;
			}
			if (distance_squared(player, p) <= r*r) {
				return true;
			}
		}
		return false;
	}
 

    private Point randomMove2(Point[] players, Point self) {
        double dir = random.nextDouble() * 2 * Math.PI;
        double dx = 6 * Math.cos(dir);
        double dy = 6 * Math.sin(dir);
        Point randomJumpToCircumference = new Point(dx, dy, self_id);

        PriorityQueue<Point> candidateTomove = new PriorityQueue<Point>(
                new Comparator<Point>() {
                    public int compare(Point x, Point y) {
                    	int guessedWisdomForStranger=unExploredWisdom / notMetStrangersCount ;
                        int vx = (!state.getWisdomMap().containsKey(x.id)) ?guessedWisdomForStranger : state.getWisdomMap().get(x.id).wisdomLeft;
                        int vy = (!state.getWisdomMap().containsKey(y.id)) ? guessedWisdomForStranger: state.getWisdomMap().get(y.id).wisdomLeft;
                        return (vy - vx);
                    }
                });
        int i, j;
        for (i = 0; i < players.length; i++) {
        	//skip if no wisdom
        	if(! state.hasMoreWisdomForShare(players[i].id))
            	continue;

            double minDistance = Double.MAX_VALUE;
            for (j = 0; j < players.length; j++) {
                if (players[j].id == self_id || i == j) continue;
                double curDist = Math.sqrt(distance_squared( players[i],  players[j]));
                if (curDist < minDistance)
                    minDistance = curDist;
            }
            if (minDistance > 0.5)
                candidateTomove.offer(players[i]);
        }

        if (candidateTomove.size() == 0)
            return randomJumpToCircumference;
       
        randomJumpToCircumference = comeCloseAndTalk(self,candidateTomove.peek(), candidateTomove.peek().id );
        return randomJumpToCircumference;
    }
    
    private Point randomMove(Point[] players, Point self) {
    double dir = random.nextDouble() * 2 * Math.PI;
    double dx = 6 * Math.cos(dir);
    double dy = 6 * Math.sin(dir);
    Point position = new Point(dx, dy, self_id);

 
    PriorityQueue<Point> candidateTomove = new PriorityQueue<Point>(
            new Comparator<Point>() {
                public int compare(Point x, Point y) {
                	int guessedWisdomForStranger=unExploredWisdom / notMetStrangersCount ;
                    int vx = (!state.getWisdomMap().containsKey(x.id)) ?guessedWisdomForStranger : state.getWisdomMap().get(x.id).wisdomLeft;
                    int vy = (!state.getWisdomMap().containsKey(y.id)) ? guessedWisdomForStranger: state.getWisdomMap().get(y.id).wisdomLeft;
                    return (vy - vx);
                }
            });
    int i, j;
    for (i = 0; i < players.length; i++) {
    	//skip if no wisdom
    	if(! state.hasMoreWisdomForShare(players[i].id))
        	continue;
        double minDistance = Double.MAX_VALUE;
        for (j = 0; j < players.length; j++) {
            if (players[j].id == self_id || i == j) continue;
            dx = players[i].x - players[j].x;
            dy = players[i].y - players[j].y;
            double dd = Math.sqrt(dx * dx + dy * dy);
            if (dd < minDistance)
                minDistance = dd;
        }
        if (minDistance > 0.5)
            candidateTomove.offer(players[i]);
    }
    double expectedDistance;
    if (candidateTomove.size() == 0)
        return position;
    else
        expectedDistance = 0.5;
    dx = candidateTomove.peek().x - self.x;
    dy = candidateTomove.peek().y - self.y;
    double distanceTotarget = Math.sqrt(dx * dx + dy * dy);
    position = new Point(dx * (distanceTotarget - expectedDistance) / distanceTotarget,
            dy * (distanceTotarget - expectedDistance) / distanceTotarget, self_id);
    return position;
    }
}
