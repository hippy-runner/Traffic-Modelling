package algorithm;

/**
 * Encapsulates a collection of different road types that have traffic and fixed 
 * test parameters to simulate the flow of traffic based on a rule.
 * 
 * NOTE: this class is designed to be used in a console application where the 
 * console application simply initializes an instance of this object and then 
 * calls the "Run()" method.
 * 
 * @author Nathan Minor
 */

public class Simulation {
    // private instance variables
    private Map roadNetwork;        // stores an instance of a map of roads
    private Rules roadRule;         // stores the over-arching rule of the road
    private Position nextPosition;  // stores the next position of a car, we need this to pass this information between private methods
    private int iterationCount;     // stores the number of iterations to compute
//    private int slowDownCount;      // stores the number of slow downs that occured (per iteration)
//    private int laneChangeCount;    // stores the number of lane changes that occured (per iteration)
//    private int decisionCount;      // stores the number of decisions that were made (per iteration)
    private int simulationId;       // stores the ID for this simulation
    private long startMilliSec;     // stores the starting millisecond count, used for calculating the duration of the simulation
    private SummaryData[][] iterationData;
    private SummaryData[] simulationData;
    
    /**
     * Simulation class constructor initializes the private instance variables
     * for a new simulation.
     * @param id
     * @param numRoads
     * @param roadLength
     * @param numIterations
     * @param rule 
     */
    public Simulation(int id, int numRoads, int roadLength, int numIterations, Rules rule) {
        this.roadNetwork = new Map(numRoads, roadLength);
        this.roadRule = rule;
        this.iterationCount = numIterations;
//        this.slowDownCount = 0;
//        this.laneChangeCount = 0;
//        this.decisionCount = 0;
        this.simulationId = id;
        this.startMilliSec = System.currentTimeMillis();
        this.iterationData = new SummaryData[numIterations][numRoads];
        this.simulationData = new SummaryData[numRoads];
    }
    
    /**
     * Returns the number of iterations this simulation will compute.
     * @return 
     */
    public int getIterationCount() { return this.iterationCount; }
    
    /**
     * Returns the ID for the current simulation.
     * @return 
     */
    public int getId() { return this.simulationId; }
    
    /**
     * Returns the current duration of the simulation.
     * @return 
     */
    public long duration() { return (System.currentTimeMillis() - this.startMilliSec); }
    
    /**
     * Returns the rule being implemented in this simulation.
     * @return 
     */
    public Rules getRule() { return this.roadRule; }
    
    /**
     * Returns the map of roads used in the simulation.
     * @return 
     */
    public Map getRoads() { return this.roadNetwork; }
    
    /**
     * This is where the simulation actually runs.
     */
    public void Run() {
        // local variables
        DataPersistence simData = new DataPersistence(this.roadNetwork.getRoadCount()); // used to store our simulation data in an XML file
        
        // generate a random network of roads
        this.roadNetwork.genrateRoads();
        
        // iterate based on the number of iterations parameters
        for (int i = 0; i < this.iterationCount; i++) {
            
            // calculate the next iteration
            this.calculateNext(i); 
        }
        
        // calculate the ovarall stats for the simulation 
        this.calculateStats();
        
        // add calculated stats to data persistence object
        simData.addData(this.simulationData);
        
        // save the data
        simData.saveData(this);
    }
    
    /**
     * This is core of our simulation algorithm. 
     * This is where the magic happens.
     */
    private void calculateNext(int iterationNum) {
        // local variables
        Road currentRoad = null;                            // stores the current road (in the iteration)
        Car currentCar = null;                              // stores the current car (in the iteration)
        int roadCount = this.roadNetwork.getRoadCount();    // stores the number of roads in the network
        int roadLength = 0;                                 // stores the length of the current road (in the iteration)
        int laneCount = 0;                                  // stores the number of lanes on the current road (in the iteration)
        boolean isPathClear = true;                         // stores a flag used for checking the route a car takes to its next position
        double density = 0.0;
        
        // iterate through all the possible grid locations
        for (int i = 0; i < roadCount; i++) {
                        
            // get the current road
            currentRoad = this.roadNetwork.getRoad(i);

            // we only care about roads that actually exist...
            if (currentRoad != null) {
                
                // reset the counters for decisions, slow downs, and lane changes
                currentRoad.resetDecisionCount();
                currentRoad.resetLaneChangeCount();
                currentRoad.resetSlowDownCount();

                // get the important properties of the road
                roadLength = currentRoad.getRoadLength();
                laneCount = currentRoad.getLaneCount();

                // iterate through the different locations on the road
                for (int slot = 0; slot < roadLength; slot++) {
                    for (int lane = 0; lane < laneCount; lane++) {

                        // we only care about locations that actually have a car...
                        if (currentRoad.isOccupied(lane, slot)) {

                            // get the car at the current location
                            currentCar = currentRoad.getCar(lane, slot);
                            
                            // reset the current car's speed back to the car's start speed
                            // this models the decision to speed back up after slowing down
                            currentCar.setCurrentSpeed(currentCar.getStartSpeed());

                            //--------------------------------------
                            //----------BEGIN THE MAGIC-------------
                            //--------------------------------------
                            // Q0: are you rule 2 or 3?
                            if ((this.roadRule == Rules.SINGLE_PASSING)
                                    || (this.roadRule == Rules.SINGLE_DRIVING)) {

                                // A1: yes
                                // Q1.1: are you passing?
                                if (currentCar.isPassing()) {

                                    // A1.1: yes 
                                    // Q1.2: is the lane to the right open in the next position?
                                    // to we need to check all of the spaces/tiles 
                                    // between the current and next positions

                                    // use our sophisticated method for checking the clearness of our path
                                    isPathClear = this.isPathClear(
                                            true,                   // in this branch of our algorithm tree, we are passing
                                            false,                  // in this branch of our algorithm tree, we are passing on the right
                                            currentCar, 
                                            new Position(lane,slot), 
                                            currentRoad);

                                    // NOW, we can check the isPathClear flag to answer Q1.2
                                    if (isPathClear) {

                                        // A1.2: yes
                                        
                                        // increment the decision counter
                                        //this.decisionCount++;
                                        currentRoad.incrementDecisions();
                                        
                                        // Q1.3: are you rule 2 (single pass lane)
                                        if(this.roadRule == Rules.SINGLE_PASSING) {

                                            // A1.3: yes
                                            // reset the passing flag because this car is no longer passing
                                            currentCar.setPassingState(false);
                                        }
                                        else {
                                            // A1.3: no
                                            // Q1.4: are you in the driving lane?
                                            if ((lane+1) == (laneCount -1)) {
                                                // A1.4: yes
                                                // reset the passing flag because this car is no longer passing
                                                currentCar.setPassingState(false);
                                            }
                                            // else A1.4: no; means we leave the passing state alone (i.e. still true)
                                        }

                                        // yippie, we get to move there!
                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                        currentRoad.setCar(lane, slot, null);
                                        
                                        // a lane change occurred successfully, so increment the lane change counter
                                        //this.laneChangeCount++;
                                        currentRoad.incrementLaneChanges();
                                    }                                    
                                    else {
                                        // A1.2: no
////////////////////////////////////////// REPEAT BRANCH A0: NO ///////////////////////////////////////////////
                                        // Q2.1: is the next position in front of you open?
                                        // first, make sure the path is clear
                                        isPathClear = this.isPathClear(
                                                false, false,           // in this branch of our algorithm tree, we are not switching lanes
                                                currentCar, 
                                                new Position(lane,slot), 
                                                currentRoad);

                                        // now we can check the isPathClear flag for Q2.1
                                        if (isPathClear) {

                                            // A2.1: yes    
                                            // yippie, we get to move there!
                                            currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                            currentRoad.setCar(lane, slot, null);
                                        }
                                        else {

                                            // A2.1: no
                                            // Q2.2: are you rule 4 (no passing)?
                                            if (this.roadRule == Rules.NO_PASSING) {

                                                // A2.2: yes
                                                // oh no, we have to slow down!

                                                // we are making a decision, increment the counter
                                                //this.decisionCount++;
                                                currentRoad.incrementDecisions();

                                                // reset isPathClear flag
                                                isPathClear = false;                                                    

                                                // we slow down until we no longer have a collision 
                                                while (!isPathClear) {  

                                                    // decrease speed
                                                    currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                    // check the path again
                                                    isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                }

                                                // now move the car based on the new speed
                                                currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                currentRoad.setCar(lane, slot, null); 

                                                // the car successfully slowed down, increment counter
                                                //this.slowDownCount++;
                                                currentRoad.incrementSlowDowns();
                                            }
                                            else {

                                                // A2.2: no

                                                // a decision needs to made, increment counter
                                                //this.decisionCount++;
                                                currentRoad.incrementDecisions();

                                                // Q2.3: is the lane to the left open in the next position?
                                                // first, check the path
                                                isPathClear = this.isPathClear(true, true, currentCar, new Position(lane,slot), currentRoad);
                                                if (isPathClear) {

                                                    // A2.3: yes
                                                    // Q2.4: are you rule 1 (free passing)?
                                                    if (this.roadRule == Rules.FREE_PASSING) {
                                                        // A2.4: yes

                                                        // now we can move the car to it's new position
                                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                        currentRoad.setCar(lane, slot, null);

                                                        // a car successfully switched lanes, increment counter
                                                        //this.laneChangeCount++;
                                                        currentRoad.incrementLaneChanges();
                                                    }
                                                    else {
                                                        // A2.4: no
                                                        // Q2.5: are you rule 2?
                                                        if (this.roadRule == Rules.SINGLE_PASSING) {

                                                            // A2.5: yes
                                                            // if you made it to this point, congratulations, but we need to check
                                                            // whether or not your new position is in the passing lane
                                                            // Q2.6: are you in the passing lane?
                                                            if (this.nextPosition.getLane() == 0) {

                                                                // A2.6: yes
                                                                // alrighty, so you are passing
                                                                currentCar.setPassingState(true);
                                                            }
                                                            else {

                                                                // A2.6: no
                                                                // alrighty, so you are NOT passing
                                                                currentCar.setPassingState(false);
                                                            }                                                                
                                                        }
                                                        else {

                                                            // A2.5: no
                                                            // sweet, so you are passing
                                                            currentCar.setPassingState(true);
                                                        }

                                                        // now we can move the car to it's new position
                                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                        currentRoad.setCar(lane, slot, null);

                                                        // a car successfully changed lanes, increment counter
                                                        //this.laneChangeCount++;
                                                        currentRoad.incrementLaneChanges();
                                                    }                                                       
                                                }
                                                else {

                                                    // A2.3: no
                                                    // Q2.7: are you rule 1 (free passing)
                                                    if (this.roadRule == Rules.FREE_PASSING) {

                                                        // A2.7: yes
                                                        // awesome, since the free passing rule is in effect,
                                                        // we are allowed to check for passing on the right...
                                                        // Q2.8: is the lane the right open in the next position?
                                                        isPathClear = this.isPathClear(true, false, currentCar, new Position(lane,slot), currentRoad);
                                                        if (isPathClear) {

                                                            // A2.8: yes
                                                            // excellent, we get to mover there!
                                                            currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                            currentRoad.setCar(lane, slot, null);

                                                            // the car successfully changed lanes, increment counter
                                                            //this.laneChangeCount++;
                                                            currentRoad.incrementLaneChanges();
                                                        }
                                                        else {

                                                            // A2.8: no
                                                            // bummer, this means we need to slow down

                                                            // reset isPathClear flag
                                                            isPathClear = false;                                                    

                                                            // we slow down until we no longer have a collision 
                                                            while (!isPathClear) {  

                                                                // decrease speed
                                                                currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                                // check the path again
                                                                isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                            }

                                                            // now move the car based on the new speed
                                                            currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                            currentRoad.setCar(lane, slot, null);  

                                                            // a car slowed down successfully, increment
                                                            //this.slowDownCount++;
                                                            currentRoad.incrementSlowDowns();
                                                        }
                                                    }
                                                    else {

                                                        // A2.7: no
                                                        // well shucks, I guess this means we have to slow down

                                                        // reset isPathClear flag
                                                        isPathClear = false;                                                    

                                                        // we slow down until we no longer have a collision 
                                                        while (!isPathClear) {  

                                                            // decrease speed
                                                            currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                            // check the path again
                                                            isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                        }

                                                        // now move the car based on the new speed
                                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                        currentRoad.setCar(lane, slot, null);

                                                        // the car slowed down successfully, increment the counter
                                                        //this.slowDownCount++;
                                                        currentRoad.incrementSlowDowns();
                                                    }
                                                }
                                            }
                                        }
//////////////////////////////////////// END REPEAT BRANCH A0: NO /////////////////////////////////////////////
                                        
                                    }
                                }                                
                                else { 
                                    // A1.1: no
////////////////////////////////////////// REPEAT BRANCH A0: NO ///////////////////////////////////////////////
                                    // Q2.1: is the next position in front of you open?
                                    // first, make sure the path is clear
                                    isPathClear = this.isPathClear(
                                            false, false,           // in this branch of our algorithm tree, we are not switching lanes
                                            currentCar, 
                                            new Position(lane,slot), 
                                            currentRoad);

                                    // now we can check the isPathClear flag for Q2.1
                                    if (isPathClear) {

                                        // A2.1: yes    
                                        // yippie, we get to move there!
                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                        currentRoad.setCar(lane, slot, null);
                                    }
                                    else {

                                        // A2.1: no
                                        // Q2.2: are you rule 4 (no passing)?
                                        if (this.roadRule == Rules.NO_PASSING) {

                                            // A2.2: yes
                                            // oh no, we have to slow down!

                                            // we are making a decision, increment the counter
                                            //this.decisionCount++;
                                            currentRoad.incrementDecisions();

                                            // reset isPathClear flag
                                            isPathClear = false;                                                    

                                            // we slow down until we no longer have a collision 
                                            while (!isPathClear) {  

                                                // decrease speed
                                                currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                // check the path again
                                                isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                            }

                                            // now move the car based on the new speed
                                            currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                            currentRoad.setCar(lane, slot, null); 

                                            // the car successfully slowed down, increment counter
                                            //this.slowDownCount++;
                                            currentRoad.incrementSlowDowns();
                                        }
                                        else {

                                            // A2.2: no

                                            // a decision needs to made, increment counter
                                            //this.decisionCount++;
                                            currentRoad.incrementDecisions();

                                            // Q2.3: is the lane to the left open in the next position?
                                            // first, check the path
                                            isPathClear = this.isPathClear(true, true, currentCar, new Position(lane,slot), currentRoad);
                                            if (isPathClear) {

                                                // A2.3: yes
                                                // Q2.4: are you rule 1 (free passing)?
                                                if (this.roadRule == Rules.FREE_PASSING) {
                                                    // A2.4: yes

                                                    // now we can move the car to it's new position
                                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                    currentRoad.setCar(lane, slot, null);

                                                    // a car successfully switched lanes, increment counter
                                                    //this.laneChangeCount++;
                                                    currentRoad.incrementLaneChanges();
                                                }
                                                else {
                                                    // A2.4: no
                                                    // Q2.5: are you rule 2?
                                                    if (this.roadRule == Rules.SINGLE_PASSING) {

                                                        // A2.5: yes
                                                        // if you made it to this point, congratulations, but we need to check
                                                        // whether or not your new position is in the passing lane
                                                        // Q2.6: are you in the passing lane?
                                                        if (this.nextPosition.getLane() == 0) {

                                                            // A2.6: yes
                                                            // alrighty, so you are passing
                                                            currentCar.setPassingState(true);
                                                        }
                                                        else {

                                                            // A2.6: no
                                                            // alrighty, so you are NOT passing
                                                            currentCar.setPassingState(false);
                                                        }                                                                
                                                    }
                                                    else {

                                                        // A2.5: no
                                                        // sweet, so you are passing
                                                        currentCar.setPassingState(true);
                                                    }

                                                    // now we can move the car to it's new position
                                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                    currentRoad.setCar(lane, slot, null);

                                                    // a car successfully changed lanes, increment counter
                                                    //this.laneChangeCount++;
                                                    currentRoad.incrementLaneChanges();
                                                }                                                       
                                            }
                                            else {

                                                // A2.3: no
                                                // Q2.7: are you rule 1 (free passing)
                                                if (this.roadRule == Rules.FREE_PASSING) {

                                                    // A2.7: yes
                                                    // awesome, since the free passing rule is in effect,
                                                    // we are allowed to check for passing on the right...
                                                    // Q2.8: is the lane the right open in the next position?
                                                    isPathClear = this.isPathClear(true, false, currentCar, new Position(lane,slot), currentRoad);
                                                    if (isPathClear) {

                                                        // A2.8: yes
                                                        // excellent, we get to mover there!
                                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                        currentRoad.setCar(lane, slot, null);

                                                        // the car successfully changed lanes, increment counter
                                                        //this.laneChangeCount++;
                                                        currentRoad.incrementLaneChanges();
                                                    }
                                                    else {

                                                        // A2.8: no
                                                        // bummer, this means we need to slow down

                                                        // reset isPathClear flag
                                                        isPathClear = false;                                                    

                                                        // we slow down until we no longer have a collision 
                                                        while (!isPathClear) {  

                                                            // decrease speed
                                                            currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                            // check the path again
                                                            isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                        }

                                                        // now move the car based on the new speed
                                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                        currentRoad.setCar(lane, slot, null);  

                                                        // a car slowed down successfully, increment
                                                        //this.slowDownCount++;
                                                        currentRoad.incrementSlowDowns();
                                                    }
                                                }
                                                else {

                                                    // A2.7: no
                                                    // well shucks, I guess this means we have to slow down

                                                    // reset isPathClear flag
                                                    isPathClear = false;                                                    

                                                    // we slow down until we no longer have a collision 
                                                    while (!isPathClear) {  

                                                        // decrease speed
                                                        currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                        // check the path again
                                                        isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                    }

                                                    // now move the car based on the new speed
                                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                    currentRoad.setCar(lane, slot, null);

                                                    // the car slowed down successfully, increment the counter
                                                    //this.slowDownCount++;
                                                    currentRoad.incrementSlowDowns();
                                                }
                                            }
                                        }
                                    }
//////////////////////////////////////// END REPEAT BRANCH A0: NO /////////////////////////////////////////////
                                }
                            }
                            else {
                                // A0: no
                                // Q2.1: is the next position in front of you open?
                                // first, make sure the path is clear
                                isPathClear = this.isPathClear(
                                        false, false,           // in this branch of our algorithm tree, we are not switching lanes
                                        currentCar, 
                                        new Position(lane,slot), 
                                        currentRoad);

                                // now we can check the isPathClear flag for Q2.1
                                if (isPathClear) {

                                    // A2.1: yes    
                                    // yippie, we get to move there!
                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                    currentRoad.setCar(lane, slot, null);
                                }
                                else {

                                    // A2.1: no
                                    // Q2.2: are you rule 4 (no passing)?
                                    if (this.roadRule == Rules.NO_PASSING) {

                                        // A2.2: yes
                                        // oh no, we have to slow down!

                                        // we are making a decision, increment the counter
                                        //this.decisionCount++;
                                        currentRoad.incrementDecisions();

                                        // reset isPathClear flag
                                        isPathClear = false;                                                    

                                        // we slow down until we no longer have a collision 
                                        while (!isPathClear) {  

                                            // decrease speed
                                            currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                            // check the path again
                                            isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                        }

                                        // now move the car based on the new speed
                                        currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                        currentRoad.setCar(lane, slot, null); 

                                        // the car successfully slowed down, increment counter
                                        //this.slowDownCount++;
                                        currentRoad.incrementSlowDowns();
                                    }
                                    else {

                                        // A2.2: no

                                        // a decision needs to made, increment counter
                                        //this.decisionCount++;
                                        currentRoad.incrementDecisions();

                                        // Q2.3: is the lane to the left open in the next position?
                                        // first, check the path
                                        isPathClear = this.isPathClear(true, true, currentCar, new Position(lane,slot), currentRoad);
                                        if (isPathClear) {

                                            // A2.3: yes
                                            // Q2.4: are you rule 1 (free passing)?
                                            if (this.roadRule == Rules.FREE_PASSING) {
                                                // A2.4: yes

                                                // now we can move the car to it's new position
                                                currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                currentRoad.setCar(lane, slot, null);

                                                // a car successfully switched lanes, increment counter
                                                //this.laneChangeCount++;
                                                currentRoad.incrementLaneChanges();
                                            }
                                            else {
                                                // A2.4: no
                                                // Q2.5: are you rule 2?
                                                if (this.roadRule == Rules.SINGLE_PASSING) {

                                                    // A2.5: yes
                                                    // if you made it to this point, congratulations, but we need to check
                                                    // whether or not your new position is in the passing lane
                                                    // Q2.6: are you in the passing lane?
                                                    if (this.nextPosition.getLane() == 0) {

                                                        // A2.6: yes
                                                        // alrighty, so you are passing
                                                        currentCar.setPassingState(true);
                                                    }
                                                    else {

                                                        // A2.6: no
                                                        // alrighty, so you are NOT passing
                                                        currentCar.setPassingState(false);
                                                    }                                                                
                                                }
                                                else {

                                                    // A2.5: no
                                                    // sweet, so you are passing
                                                    currentCar.setPassingState(true);
                                                }

                                                // now we can move the car to it's new position
                                                currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                currentRoad.setCar(lane, slot, null);

                                                // a car successfully changed lanes, increment counter
                                                //this.laneChangeCount++;
                                                currentRoad.incrementLaneChanges();
                                            }                                                       
                                        }
                                        else {

                                            // A2.3: no
                                            // Q2.7: are you rule 1 (free passing)
                                            if (this.roadRule == Rules.FREE_PASSING) {

                                                // A2.7: yes
                                                // awesome, since the free passing rule is in effect,
                                                // we are allowed to check for passing on the right...
                                                // Q2.8: is the lane the right open in the next position?
                                                isPathClear = this.isPathClear(true, false, currentCar, new Position(lane,slot), currentRoad);
                                                if (isPathClear) {

                                                    // A2.8: yes
                                                    // excellent, we get to mover there!
                                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                    currentRoad.setCar(lane, slot, null);

                                                    // the car successfully changed lanes, increment counter
                                                    //this.laneChangeCount++;
                                                    currentRoad.incrementLaneChanges();
                                                }
                                                else {

                                                    // A2.8: no
                                                    // bummer, this means we need to slow down

                                                    // reset isPathClear flag
                                                    isPathClear = false;                                                    

                                                    // we slow down until we no longer have a collision 
                                                    while (!isPathClear) {  

                                                        // decrease speed
                                                        currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                        // check the path again
                                                        isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                    }

                                                    // now move the car based on the new speed
                                                    currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                    currentRoad.setCar(lane, slot, null);  

                                                    // a car slowed down successfully, increment
                                                    //this.slowDownCount++;
                                                    currentRoad.incrementSlowDowns();
                                                }
                                            }
                                            else {

                                                // A2.7: no
                                                // well shucks, I guess this means we have to slow down

                                                // reset isPathClear flag
                                                isPathClear = false;                                                    

                                                // we slow down until we no longer have a collision 
                                                while (!isPathClear) {  

                                                    // decrease speed
                                                    currentCar.setCurrentSpeed(currentCar.getCurrentSpeed()-1);

                                                    // check the path again
                                                    isPathClear = this.isPathClear(false, false, currentCar, new Position(lane,slot), currentRoad);
                                                }

                                                // now move the car based on the new speed
                                                currentRoad.setCar(this.nextPosition.getLane(), this.nextPosition.getSlot(), currentCar);
                                                currentRoad.setCar(lane, slot, null);

                                                // the car slowed down successfully, increment the counter
                                                //this.slowDownCount++;
                                                currentRoad.incrementSlowDowns();
                                            }
                                        }
                                    }
                                }                                                
                            }
                            //--------------------------------------
                            //-----------END THE MAGIC--------------
                            //--------------------------------------
                        } // end car existence check
                    } // end iterating through lanes on road
                } // end iterating through slots on road
                
                if (i%2 == 0){
                    density = 0.25;
                }
                else {
                    density = 0.75;
                }
                
                this.iterationData[iterationNum][i] = new SummaryData(
                        currentRoad.getLaneCount(),
                        density,
                        currentRoad.averageCarSpeed(),
                        currentRoad.getSlowDownCount(),
                        currentRoad.getLaneChangeCount(),
                        currentRoad.getDecisionCount());
                
                // now that we have finished iterating through all the lane positions,
                // it's time to save the changed road back to the network
                this.roadNetwork.setRoad(i, currentRoad);

            } // end road existance check
        } // end iterating through grid (i)
        
    } // end calculateNext() method
    
    /**
     * This method is used to check ALL of the tiles/spaces a car would travel through if
     * it were changing lanes. A flag (isLeft) is used to distinguish between passing on the
     * right versus passing on the left.
     * @param isPassingLeft
     * @param currentCar
     * @param currentPosition
     * @param currentRoad
     * @return 
     */
    private boolean isPathClear(boolean isPassing, boolean isPassingLeft, Car currentCar, Position currentPosition, Road currentRoad) {
        // local variables
        boolean isPathClear = true;                     // stores the flag that this method returns
        int adjustedNextSlot = 0;                       // stores an adjusted next slot value, used for handling negative values
        int adjustedNextLane = 0;                       // stores an adjusted next lane value, used for handling out of bounds exceptions
        int laneChangeDir = 0;                          // store either -1 (pasing right), 0 (not passing), or 1 (passing left) based on the two passing flags
        int currentLane = currentPosition.getLane();    // 
        int currentSlot = currentPosition.getSlot();    // 
        
        // first, determine which direction we are changing lanes
        if (isPassing) {
            if (isPassingLeft) {

                // designates passing on the left
                laneChangeDir = 1;
            } 
            else {

                // designates passing on the right
                laneChangeDir = -1;
            }            
        }
        // else we are not passing so leave lane dir equal to zero
                                                
        // now, iterate through the tiles between the current position
        // and the next position
        for (int g = 1; g <= currentCar.getCurrentSpeed(); g++) {

            // first, we need to check for negative next position values
            if ((currentSlot-g) < 0) {

                // add negative values to road length
                adjustedNextSlot = currentRoad.getRoadLength() + (currentSlot-g);
            }
            else {

                // use positive values "as-is"
                adjustedNextSlot = currentSlot - g;
            }
            
            // verify that the next lane is properly within the bounds of the array of cars in the Road class
            if (((currentLane+laneChangeDir) >= currentRoad.getLaneCount())
                    || ((currentLane+laneChangeDir) < 0)) {
                adjustedNextLane = currentLane;
            }
            else {
                adjustedNextLane = currentLane + laneChangeDir;
            }

            // check to see if the space is occupied. if the tile/space is 
            // occupied, then the path is not clear and so we set the flag to 
            // false and break out of our search loop
            if (currentRoad.isOccupied(adjustedNextLane, adjustedNextSlot)) {
                isPathClear = false;
                break;
            }
        }
        
        // assign the calculated next position to the private instance variable
        this.nextPosition = new Position(adjustedNextLane, adjustedNextSlot);
        
        return isPathClear;
    }
    
    /**
     * Calculates the summary data for the simulation after it completes
     * all the iterations specified by the user (via the constructor). This is
     * the final data that will be saved in the XML file for this simulation.
     */
    private void calculateStats() {
        
        int laneCount = 0;
        double density = 0.0;
        double totalAvgSpeed = 0.0;
        double totalAvgSlows = 0.0;
        double totalAvgChanges = 0.0;
        double totalAvgDecisions = 0.0;
        
        for (int road = 0; road < this.roadNetwork.getRoadCount(); road++) {
            laneCount = this.iterationData[0][road].getLaneCount();
            density = this.iterationData[0][road].getDensity();
            
            totalAvgSpeed = 0.0;
            totalAvgSlows = 0.0;
            totalAvgChanges = 0.0;
            totalAvgDecisions = 0.0;
            
            for (int iter = 0; iter < this.iterationCount; iter++) {
                totalAvgSpeed += this.iterationData[iter][road].getAvgSpeed();
                totalAvgSlows += this.iterationData[iter][road].getAvgSlowDowns();
                totalAvgChanges += this.iterationData[iter][road].getAvgLaneChanges();
                totalAvgDecisions += this.iterationData[iter][road].getAvgDecisions();
            }
            
            this.simulationData[road] = new SummaryData(
                    laneCount,
                    density,
                    totalAvgSpeed / this.iterationCount,
                    totalAvgSlows,
                    totalAvgChanges,
                    totalAvgDecisions);
        }
    }
}
