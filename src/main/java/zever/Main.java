package zever;

/*
 * This is the main point of entry into the zever program. It looks at the command line argument
 * and launches either CollectInverterData or PostToPVOutput. It also provides a logger and properties
 * to the sub-programs. 
 */

public class Main {

	public static void main(String[] args) {
		// This is just to see if it works
		Outputs o = new Outputs();
		
		o.collect();
		
/*		
		// This is real
		switch (args[1]) {
		case "CollectInverterData":
			Outputs.collect();
			break;
		case "PostToPVOutput":
			Outputs.postToPVOutput();
			break;
		}
*/
	}

}
