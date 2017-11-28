package algorithm;

/**
 * Encapsulates a collection of roads where each road represents a different 
 * type of road (i.e., defined by lane count and traffic density) that the
 * simulation class will test.
 * 
 * @author Nathan Minor
 */

public class Map {    
    // private instance variables
    private int roadCount;      // stores the number of roads in the road network
    private int roadLength;     // stores the length used for each road
    private Road[] roadArray;   // stores the network of roads in a 4-D array
    
    /**
     * Map class constructor initializes the road graph 
     * with height and width parameters for the "grid" of roads.
     * @param numRoads 
     */
    public Map (int numRoads, int roadLen) {
        this.roadCount = numRoads;
        this.roadLength = roadLen;
        this.roadArray = new Road[numRoads];
    }
    
    /**
     * Returns the number of roads (which is used to create the grid of nodes)
     * @return 
     */
    public int getRoadCount() { return this.roadCount; }
    
    /**
     * Returns the road at the specified location.
     * @param i
     * @return 
     */
    public Road getRoad(int i) { return this.roadArray[i]; }
    
    /**
     * Sets the specified location to the new (input) road.
     * @param i
     * @param r 
     */
    public void setRoad(int i, Road r) { this.roadArray[i] = r;}
    
    /**
     * This method is designed to randomly generate a network of roads.
     */
    public void genrateRoads() {
        // local variables
        int laneTypeCount = 0;  // keeps track of how many lanes an index should have
        int laneCount = 0;      // stores the number of lanes
        int carCount = 0;       // stores the number cars (calculated based on traffic density)
        
        // iterate through the network of (soon-to-be) roads
        for (int i = 0; i < this.roadCount; i++) {
                        
            // even indices have low density traffic
            if ((i%2)==0) {
                laneTypeCount++;
                laneCount = laneTypeCount + 1;
                carCount = this.roadLength * laneCount / 4;
            }
            // odd indices have high density trafic
            else {
                laneCount = laneTypeCount + 1;
                carCount = this.roadLength * laneCount * 3 / 4;
            }
            
            this.roadArray[i] = new Road(laneCount, this.roadLength, carCount);
            this.roadArray[i].generateCars();
        }
    }
}
