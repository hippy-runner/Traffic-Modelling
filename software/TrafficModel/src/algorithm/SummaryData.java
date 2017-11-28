package algorithm;

/**
 * Encapsulates the data used for analyzing the flow of traffic across different
 * road types and rule implementation.
 * @author Nathan Minor
 */
public class SummaryData {
    // private instance variables
    private int laneCount;
    private double density;
    private double avgSpeed;
    private double avgSlowDowns;
    private double avgLaneChanges;
    private double avgDecisions;
    
    /**
     * Initializes the private instance variables to parameters.
     */
    public SummaryData(
            int numLanes,
            double trafDensity,
            double avgS,
            double avgSlows,
            double avgChanges,
            double avgDecisions) {
        this.laneCount = numLanes;
        this.density = trafDensity;
        this.avgSpeed = avgS;
        this.avgSlowDowns = avgSlows;
        this.avgLaneChanges = avgChanges;
        this.avgDecisions = avgDecisions;
    }
    
    public int getLaneCount() { return this.laneCount; }
    public double getDensity() { return this.density; }
    public double getAvgSpeed() { return this.avgSpeed; }
    public double getAvgSlowDowns() { return this.avgSlowDowns; }
    public double getAvgLaneChanges() { return this.avgLaneChanges; }
    public double getAvgDecisions() { return this.avgDecisions; }
}
