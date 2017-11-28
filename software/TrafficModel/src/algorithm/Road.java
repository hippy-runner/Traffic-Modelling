package algorithm;

/** 
 * Encapsulates the number of lanes, the length of the road, 
 * the traffic (a collection of cars), and counts of the number 
 * of slows, changes, and decisions made during an iteration of our logic tree.
 * 
 * @author Nathan Minor
 */
import java.util.Random;

public class Road {
    // private instance variables
    private int laneCount;          // stores the number of lanes on the road
    private int roadLength;         // stores the length of the road
    private int initialCarCount;    // stores the number of cars to place on the road initially
    private Car[][] traffic;        // a 2-D array for storing the cars that constitute the traffic on the road
    private int decisionCount;      // stores the number of decisions made during an iterations
    private int slowDownCount;      // stores the number of slow downs that occurred during an iteration
    private int laneChangeCount;    // stores the number of lane changes that occured during an iteration
    
    
    /**
     * Road class constructor initializes
     * the private instance variables.
     * @param numLanes
     * @param length 
     * @param numCars
     */
    public Road(int numLanes, int length, int numCars) {
        this.laneCount = numLanes;
        this.roadLength = length;
        this.initialCarCount = numCars;
        this.traffic = new Car[numLanes][length];
    }
    
    /**
     * Returns the number of lanes on the road.
     * @return 
     */
    public int getLaneCount() { return this.laneCount; }
    
    /**
     * Returns the length of the road.
     * @return 
     */
    public int getRoadLength() { return this.roadLength; }
    
    /**
     * Returns the current car count based on a simple counting algorithm.
     * @return 
     */
    public int getCarCount() {
        int carCount = 0;
        
        for (int i = 0; i < laneCount; i++) {
            for (int j = 0; j < roadLength; j++) {
                if (this.isOccupied(i, j)) {
                    carCount++;
                }
            }
        }
        
        return carCount;
    }
    
    /**
     * @return Returns the number of decisions made during an iteration.
     */
    public int getDecisionCount() { return this.decisionCount; }
    
    /**
     * Increases the number of decisions made by 1.
     */
    public void incrementDecisions() { this.decisionCount++; }
    
    /**
     * Resets the number of decisions made to 0.
     */
    public void resetDecisionCount() { this.decisionCount = 0; }
    
    /**
     * @return Returns the number of slow downs that occurred during an iteration.
     */
    public int getSlowDownCount() { return this.slowDownCount; }
    
    /**
     * Increases the number of slow downs made by 1.
     */
    public void incrementSlowDowns() { this.slowDownCount++; }
    
    /**
     * Resets the number of slow downs to 0.
     */
    public void resetSlowDownCount() { this.slowDownCount = 0; }
    
    /**
     * @return Returns the number of lane changes that occurred during an iteration.
     */
    public int getLaneChangeCount() { return this.laneChangeCount; }
    
    /**
     * Increases the number of lane changes made by 1.
     */
    public void incrementLaneChanges() { this.laneChangeCount++; }
    
    /**
     * Resets the number of lane changes to 0.
     */
    public void resetLaneChangeCount() { this.laneChangeCount = 0; }
    
    public double averageCarSpeed() {
        // local variables
        double avgSpeed = 0.0;  // stores the calculated average speed of the cars on the road (per iteration)
        double totalSpeed = 0.0;
        double carCount = this.getCarCount();
        
        // iterate through the cars and sum their speeds
        for (int slot = 0; slot < this.roadLength; slot++) {
            for (int lane = 0; lane < this.laneCount; lane++) {
                
                // we only care about road positions that have a car
                if (this.isOccupied(lane, slot)) {
                    // add the current car's speed to the total used for calculating the average speed
                    totalSpeed += this.traffic[lane][slot].getCurrentSpeed();
                    
                }
            }
        }
        
        // calculate the average speed of the cars on the road (used per iteration)
        avgSpeed = totalSpeed / carCount;
        
        return avgSpeed;
    }
    
    /**
     * Returns whether or not a car occupies a specific location.
     * @param lane
     * @param slot
     * @return 
     */
    public boolean isOccupied(int lane, int slot) { return (this.traffic[lane][slot] != null); }
     
    /**
     * Returns the car at a specific location.
     * @param lane
     * @param slot
     * @return 
     */
    public Car getCar(int lane, int slot) { return this.traffic[lane][slot]; }
    
    /**
     * sets the car at a specific location
     * @param lane
     * @param slot
     * @param newCar 
     */
    public void setCar(int lane, int slot, Car newCar) { this.traffic[lane][slot] = newCar; }
    
    /**
     * populates the road full of null cars
     */
    public void initalize() {        
        // iterate over the lanes and slots/tiles
        for (int i = 0; i < laneCount; i++) {
            for (int j = 0; j < roadLength; j++) {
                this.traffic[i][j] = null;
            }
        }        
    }
    
    /**
     * Populates the road with cars.
     */
    public void generateCars() {
        // local variables
        Random rnd = new Random();  // Random class instance for generating random integer parameters
        boolean isOpen = false;     // stores a flag for checking the openness of a randomly generated position
        int rndLane = 0;            // stores the randomly generated lane value
        int rndSlot = 0;            // stores the randomly generated slot value
        int rndSpeed = 0;           // stores the randomly generated speed value
        double zScore = 0.0;        // stores a randomly generated zScore used to determine the rndSpeed
        
        // iterate over the number of cars, creating a random car
        // and adding that new car to the road
        for (int i = 0; i < this.initialCarCount; i++) {
                        
            // loop until an open slot on the road is found
            while (!isOpen) {
                // randomly generate lane and slot values
                rndLane = rnd.nextInt(this.laneCount);
                rndSlot = rnd.nextInt(this.roadLength);
                
                // check randomly generated lane/slot values
                isOpen = !this.isOccupied(rndLane, rndSlot);
            }
            
            // reset isOpen flag to false for next iteration
            isOpen = false;
            
            // randomly generate car speed where 
            // min = (6 tiles per iteration) = (54 mph), 
            // max = (10 tiles per iteration = (90 mph)
            zScore = rnd.nextGaussian();
            
            // if (z <= -1.96) then assign speed = 6
            if (zScore <= -1.96) {
                rndSpeed = 6;
            }
            
            // if (-1.96 < z < -1) then assign speed = 7
            if ((-1.96 < zScore)&&(zScore < -1)) {
                rndSpeed = 7;
            }
            
            // if (-1 <= z <= 1) then assign speed = 8
            if ((-1 <= zScore)&&(zScore <= 1)) {
                rndSpeed = 8;
            }
            
            // if (1 < z < 1.96) then assign speed = 9
            if ((1 < zScore)&&(zScore < 1.96)) {
                rndSpeed = 9;
            }
            
            // if (z >= 1.96) then assign speed = 10
            if (zScore >= 1.96) {
                rndSpeed = 10;
            }
            
            // put car on the road based on its position
            this.setCar(rndLane, rndSlot, new Car(rndSpeed));
        }
    }
}
