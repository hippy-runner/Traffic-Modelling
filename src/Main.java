/**
 * Encapsulates the functionality for creating a user interface (via a 
 * console application) for running simulations.
 * @author Nathan Minor
 */
import java.util.Scanner;
import algorithm.*;

public class Main {
    
    public static void main(String[] args) {
        // local variables
        Scanner keyboard = new Scanner(System.in);  // Scanner class instance used for getting user input
        Simulation sim;                      // Simulation class instance used to run a traffic simulation
        Rules simRule;                              // stores the enum version of our over-arching rule to use in the simulation 
        int simCount;                               // stores the number of simulations to run
        int simRuleInt;                             // stores the integer version of our over-arching rule to use in the simulation
        int numRoads;                               // stores the number of roads to model in the simulation
        int roadLength;                             // stores the length for each road in the simulation
        int iterationCount;                         // stores the number of iterations the simulation will compute
        
        // prompt the user for how many simulations to run
        System.out.print("Enter the number of simulations to run: ");
        simCount = keyboard.nextInt();
        
        // prompt the user for which rule to use
        System.out.println();
        System.out.print("Enter the rule number to use (1-4): ");
        simRuleInt = keyboard.nextInt();
        
        // convert the integer version of the rule to the enum version
        simRule = Rules.values()[simRuleInt-1];
        
        // prompt the user for the number of roads to simulate over
        System.out.println();
        System.out.print("Enter the number of roads to simulate: ");
        numRoads = keyboard.nextInt();
        
        // prompt the user for the length of each road
        System.out.println();
        System.out.print("Enter the length to use for each road: ");
        roadLength = keyboard.nextInt();
        
        // prompt the user for the number of iterations to compute
        System.out.println();
        System.out.print("Enter the number of iterations to compute: ");
        iterationCount = keyboard.nextInt();
        
        // construct an array full of instances of the Simulation class
        for (int i = 0; i < simCount; i++) {
            System.out.println();
            System.out.println("Initializing simulation " + (i + 1) + "...");
            // reset the simulation instance object
            sim = new Simulation((i + 1), numRoads, roadLength, iterationCount, simRule);
            System.out.println("Simulation " + (i + 1) + " initialized.");
            
            System.out.println("Running simulation " + (i + 1) + "...");
            
            // now we get to run the simulation
            sim.Run();           
            
            System.out.println("Finished simulation " + (i + 1) + ".");
        }
    }
}
