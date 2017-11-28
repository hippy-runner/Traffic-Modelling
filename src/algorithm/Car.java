package algorithm;

/**
 * Encapsulates the initial speed, current speed, and 
 * passing state of a car on the road. 
 * @author Nathan Minor
 */
public class Car {
    // private instance variables
    private int startSpeed;     // stores the speed the car started at
    private int currentSpeed;   // stores the speed of the car for a given instance
    private boolean passing;    // stores the passing state of the car
    
    /**
     * Car class constructor initializes the private instance variables.
     * @param spd 
     */
    public Car(int spd){
        this.startSpeed = spd;
        this.currentSpeed = spd;
        this.passing = false;      // default passing state is false (i.e., not passing) 
    }
    
    /**
     * Returns the starting speed of the car.
     * @return 
     */
    public int getStartSpeed() { return this.startSpeed; }
    
    /**
     * Returns the current speed of the car.
     * @return 
     */
    public int getCurrentSpeed() { return this.currentSpeed; }
    
    /**
     * Changes the current speed of the car to a new value.
     * @param newSpeed 
     */
    public void setCurrentSpeed(int newSpeed) { this.currentSpeed = newSpeed; }
    
    /**
     * Returns whether or not the current speed is slower than the starting speed
     * @return 
     */
    public boolean isSlower() { return (this.currentSpeed < this.startSpeed); }
    
    /**
     * Returns the current passing state of the car.
     * @return 
     */
    public boolean isPassing() { return this.passing; }
    
    /**
     * Sets the passing state of the car to a given value.
     * @param passState 
     */
    public void setPassingState(boolean passState) { this.passing = passState; }
}
