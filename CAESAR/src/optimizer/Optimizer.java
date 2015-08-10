package optimizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import operator.*;

public class Optimizer {
	
	static ArrayList<QueryPlan> omit (QueryPlan query_plan) {
		
		ArrayList<QueryPlan> new_query_plans = new ArrayList<QueryPlan>();
				
		for (int i=0; i<query_plan.operators.size(); i++) {
			
			Operator operator = query_plan.operators.get(i);
			boolean b;
			if (i-1>=0) {
				Operator before = query_plan.operators.get(i-1);
				b = operator.omittable(before);
			} else {
				b = false;
			}
			boolean a;
			if (i+1<query_plan.operators.size()) {
				Operator after = query_plan.operators.get(i+1);
				a = operator.omittable(after);
			} else {
				a = false;
			}			
			if (b || a) {
				LinkedList<Operator> new_ops = new LinkedList<Operator>();
				for (int j=0; j<query_plan.operators.size(); j++) {
					if (j!=i) new_ops.add(query_plan.operators.get(j));
				}
				QueryPlan new_query_plan = new QueryPlan(new_ops);	
				new_query_plans.add(new_query_plan);
			}
		}
		return new_query_plans;
	}
	
	static ArrayList<QueryPlan> omit (ArrayList<QueryPlan> qps) {
		
		ArrayList<QueryPlan> results = new ArrayList<QueryPlan>();
		
		for (QueryPlan qp : qps) {
			
			results.add(qp);
			
			// Base case: Omit operators in this query plan
			ArrayList<QueryPlan> new_query_plans = Optimizer.omit(qp);
			for (QueryPlan nqp : new_query_plans) {
				if (!nqp.contained(results)) results.add(nqp);
			}			
			// Recursive case: Omit operators in newly produced query plans
			ArrayList<QueryPlan> more_new_query_plans = Optimizer.omit(new_query_plans);
			for (QueryPlan nqp : more_new_query_plans) {
				if (!nqp.contained(results)) results.add(nqp);
			}
		}		
		return results;		
	}
	
	static void permute (LinkedList<Operator> arr, int k) {
        for(int i = k; i < arr.size(); i++){
            Collections.swap(arr, i, k);
            permute(arr, k+1);
            Collections.swap(arr, k, i);
        }
        if (k == arr.size()-1){
            System.out.println(Arrays.toString(arr.toArray()));
        }
    }
	
    public static void main(String[] args) {
    	
    	String query_plan_string = "FI x>3; CW c; PR x, y, z; ED a; FI x>10; CW c; PR x, y; ED b";
    	QueryPlan qp = QueryPlan.parse(query_plan_string);
    	System.out.println("Original query plan:\n" + qp.toString());
    	    	
    	ArrayList<QueryPlan> query_plans = new ArrayList<QueryPlan>();
    	query_plans.add(qp);
    			
    	ArrayList<QueryPlan> new_query_plans = Optimizer.omit(query_plans);
    	
    	System.out.println("After operator omission: ");
    	for (QueryPlan nqp : new_query_plans) {
    		System.out.println(nqp.toString() + " with cost " + nqp.getCost());
    	}
    }
}
