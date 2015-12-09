package wtr.g0;

import wtr.sim.Point;

import java.util.*;

public class WisdomPoint  
{
	public int wisdom;
	int id;
	public WisdomPoint(int id_, int wisdom)
	{
		id= id_;
	    this.wisdom =   wisdom / 10  * 10 ;
        this.wisdomLeft = this.wisdom;

	        if(wisdom >= 50){
	            isFriend = true;
	        } else {
	            isFriend = false;
	        }
	}
 
	public String toString()
	{
		return id + " "+ wisdomLeft + " from " + wisdom  ;
	}
	 
    public int wisdomLeft;
    public boolean isFriend;
 
    public void setWisdomRemaining(int wisdomRemaining){
        this.wisdomLeft = wisdomRemaining;
    }
 
}