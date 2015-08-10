package operator;

public class ContextWindow implements Operator {
	
	String context;
	
	public ContextWindow (String con) {		
		context = con;
	}
	
	public static ContextWindow parse(String s) {		
		
		String context = s.substring(3); // Skip "CW "				
		return new ContextWindow(context);
	}

	public boolean omittable (Operator neighbor) {		
		return this.equals(neighbor);	
	}
	
	public int getCost() {
		return 1;
	}
	
	public boolean equals(Operator operator) {
		
		if (!(operator instanceof ContextWindow)) return false;				
		ContextWindow other = (ContextWindow) operator;	
		return context.equals(other.context);
	}
	
	public String toString() {
		return "CW " + context;
	}
}
