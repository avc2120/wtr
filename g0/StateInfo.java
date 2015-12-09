package wtr.g0;

import java.util.HashMap;

import wtr.sim.Point;

public class StateInfo {

    private HashMap<Integer, Point> id_2_point_map;
    private HashMap<Integer, Point> chats;

    private HashMap<Integer, WisdomPoint> wisdomMap;
    // last chat id
    int last_chat = -1;
    // player we don't want to talk to right now due to interruptions
    private int skip_player = -1;

    public StateInfo(){
    	wisdomMap = new HashMap<>();
        resetChatsAndPlayers();
    }
    public void addFriends2WisdomInfo(int[] friend_ids){
	    for (int friend_id : friend_ids) {
	    	addFriend2WisdomInfo(friend_id );
	    }
	}
    public void addFriend2WisdomInfo(int playerId){
    	wisdomMap.put(playerId, new WisdomPoint(playerId, 50));
    }
    
    public void addNearbyPlayers2PointMap(Point[] players){
	    for (Point player : players) {
	    	addPlayerInfo(player );
	    }
	}
    public void addPlayerInfo(Point player){
    	 id_2_point_map.put(player.id, player);
    }
    
    void resetChatsAndPlayers(){
        id_2_point_map = new HashMap<Integer, Point>();
        chats = new HashMap<Integer, Point>();
        skip_player = -1;
    }
    void updateSkip_player(int id){
    	skip_player=id;
    }
    boolean shouldSkipCurrentPlayer(){
    	return skip_player >= 0;
    }
    boolean shouldSkipCurrentPlayer(int id ){
    	return skip_player ==  id;
    }
  
 boolean hasMoreWisdomForShare(int id){
     if (getWisdomMap().containsKey (id) 
     		&& getWisdomMap().get( id).wisdomLeft <= 0) {
    	 return false;
     }
     return true;
 }
    public void addChatInfo(Point player,    	Point partner){
    	chats.put(player.id, partner);  
    }
	public HashMap<Integer, Point> getId_2_point_map() {
		return id_2_point_map;
	}
	public void setId_2_point_map(HashMap<Integer, Point> id_2_point_map) {
		this.id_2_point_map = id_2_point_map;
	}
	public HashMap<Integer, Point> getChats() {
		return chats;
	}
	public void setChats(HashMap<Integer, Point> chats) {
		this.chats = chats;
	}
	public HashMap<Integer, WisdomPoint> getWisdomMap() {
		return wisdomMap;
	}
	public void setWisdomMap(HashMap<Integer, WisdomPoint> wisdomMap) {
		this.wisdomMap = wisdomMap;
	}
    
    /**
     * has wisdom ?? Don't know about stranger then he has wise point
     */
    public boolean playerHasWisdom(int playerID) {
        return !this.getWisdomMap().containsKey(playerID) ||
                (this.getWisdomMap().containsKey(playerID) && this.getWisdomMap().get(playerID).wisdomLeft >0 );
    }
	   /**
     *  pick nearest player within our range.. and start chat
     */
    public Point nearest(Point[] players, Point self) {

        double minDist = Double.MAX_VALUE;
        int targetId = -1;

        for (Point p : players) {
            if (p.id == self.id || p.id == skip_player)
                continue;
            // distance ^2 ?
            double dd = distance_squared(self, p) ;
            if (dd < 0.25 && dd > 0) {
                return null;//if some player already too close to us... *** ?
            }
            if (dd >= 0.25 && dd <= 4.0 && dd < minDist) {
                targetId = p.id;
                minDist = dd;
            }
        }
        if (targetId != -1 && playerHasWisdom(targetId)) {
            last_chat = targetId;
            return new Point(0.0, 0.0, targetId);
        }

        return null;
    }
    
    public boolean canWeChat(int id, int self_id) {
    	//player already chatting with us
    	if(getChats().get(id).id == self_id){
    		return true;
    	}else if(getChats().get(id).id == id  ) {  
    		//or player chatting with no one
            return true;
        }
        return false;
    }

    public int getLast_chat() {
		return last_chat;
	}
	public void setLast_chat(int last_chat) {
		this.last_chat = last_chat;
	}
	
	 public Point greedyOnWisdom(int self_id, Point[] players, int[] chat_ids, Point self) {

	        int maxWisdom = 0;
	        Point target = null;

	        for (int i = 0; i < players.length; i++) {

	            Point p = players[i];
	            // compute squared distance
	            double dx = self.x - p.x;
	            double dy = self.y - p.y;
	            double dd = dx * dx + dy * dy;
	            if (dd < 0.25 && dd > 0) {
	                return null;
	            }

	            if (players[i].id != chat_ids[i] ||  shouldSkipCurrentPlayer(players[i].id ) || ! canWeChat(players[i].id , self_id))
	                continue;

	            if ( getWisdomMap().containsKey(players[i].id) && getWisdomMap().get(players[i].id).wisdomLeft > maxWisdom) {
	                maxWisdom =  getWisdomMap().get(players[i].id).wisdomLeft;
	                target = players[i];
	            }
	        }
	        return target;
	    }
	 
	/**
     *  distance
     */
	private double distance_squared(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return dx * dx + dy * dy;
	}
	public void addChatInfo2Map(Point[] players, int[] chat_ids) {
        for (int k = 0; k < players.length; k++) {
        	Point partner=getId_2_point_map().get(chat_ids[k]);//TODO: chat_ids[i]
        	addChatInfo(players[k], partner);
        }
	}
	public void reset(Point[] players, int[] chat_ids) {
        this.resetChatsAndPlayers();
        this.addNearbyPlayers2PointMap(players);
        this.addChatInfo2Map(players, chat_ids);
		
	}
 }
