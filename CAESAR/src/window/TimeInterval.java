package window;

//import java.util.ArrayList;

public class TimeInterval {
	
	public double start;
	public double end;
	public int query_number;
	
	public TimeInterval (double s, double e) {
		start = s;
		end = e;		
	}
	
	public TimeInterval (double s, double e, int q) {
		start = s;
		end = e;
		query_number = q;
	}
	
	public boolean contains (double n) {
		return start <= n && n <= end;
	}
	
	/*public boolean contains (ArrayList<TimeInterval> expensive_windows) {
		for (TimeInterval i : expensive_windows) {
			if ((start <= i.start && i.start <= end) || (start <= i.end && i.end <= end)) {
				return true;
			}
		}		
		return false;
	}*/
	
	public String toString() {
		return "[" + new Double(start).intValue() + "," + new Double(end).intValue() + "]:" + query_number + " ";
	}
}
