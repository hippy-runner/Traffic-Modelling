package algorithm;

/**
 * Encapsulates a position on a road by lane and length-wise slot or tile position.
 * @author Nathan Minor * 
 */
public class Position {    
    //private instance variables
    private int currentLane;    // stores the current lane
    private int currentSlot;    // stores the current position (0 to roadLength)
    
    /**
     * Position class constructor initializes 
     * instance variables to input parameters.
     * @param lane
     * @param slot 
     */
    public Position(int lane, int slot) {
        this.currentLane = lane;
        this.currentSlot = slot;
    }
    
    /**
     * returns the value of the current lane
     * @return 
     */
    public int getLane() { return this.currentLane; }
    
    /**
     * returns the value of the current slot (position)
     * @return 
     */
    public int getSlot() { return this.currentSlot; }
    
    /**
     * sets the value of the current lane to an input parameter
     * @param newLane 
     */
    public void setLane(int newLane) { this.currentLane = newLane; }
    
    /**
     * sets the value of the current slot (position) to an input parameter
     * @param newSlot 
     */
    public void setSlot(int newSlot) { this.currentSlot = newSlot; }
}
