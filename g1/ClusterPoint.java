package wtr.g1;

import wtr.sim.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClusterPoint extends Point implements Comparable<ClusterPoint>{

	public ClusterPoint(double x, double y, int id) {
		super(x, y, id);
	}
	
	public ClusterPoint(double x, double y, int id, int cluster_number) {
		super(x, y, id);
		this.cluster_number= cluster_number;
	}
	
	 private int cluster_number = 0;
 
	    public double getX()  {
	        return this.x;
	    }
	    
 
	    
	    public double getY() {
	        return this.y;
	    }
	    
	    public void setCluster(int n) {
	        this.cluster_number = n;
	    }
	    
	    public int getCluster() {
	        return this.cluster_number;
	    }
	    
	    //Calculates the distance between two points.
	    protected static double distance(ClusterPoint p, ClusterPoint centroid) {
	        return Math.sqrt(Math.pow((centroid.getY() - p.getY()), 2) + Math.pow((centroid.getX() - p.getX()), 2));
	    }
	    
	    // //Creates random point
	    // protected static ClusterPoint getMidPoint(int min, int max , int points) {
	    // 	Random r = new Random();
	    // 	double x = min + (max - min) * r.nextDouble();
	    // 	double y = min + (max - min) * r.nextDouble();
	    // 	return new ClusterPoint(x,y, 0);
	    // }


		@Override
		public String toString() {
			return "ClusterPoint [cluster_number=" + cluster_number + ", ("
					+ x + "," + y + ")";
		}

		public static List<ClusterPoint> pointToClusterPoint(Point[] rats) {
			List<ClusterPoint> cpList=new ArrayList<ClusterPoint>(); 
			for(Point r :rats){
				cpList.add(new ClusterPoint(r.x,r.y, r.id));
			}
			return cpList;
		}

 		public int compareTo(ClusterPoint p2) {
			return Double.compare(ClusterPoint.distance(KMeans.myPosition,this),
					ClusterPoint.distance(KMeans.myPosition,p2));
		}

}
