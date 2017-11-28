package algorithm;

/**
 * Encapsulates the functionality for writing data to an XML file for the 
 * persistence of our simulation data.
 * @author Nathan Minor
 */

// these imported packages are for writing data to an XML file
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// used for gettingand formatting the current date
import java.util.Date;
import java.text.SimpleDateFormat;

public class DataPersistence {
    
    // private instance variables
    private Document doc;
    private Element[] roadTypeData;
    
    /**
     * Creates a new instance of the DataPersistence class.
     */
    public DataPersistence(int numRoads) {
        // local varibales
        DocumentBuilderFactory docFactory;          // used to create the XML document builder
        DocumentBuilder docBuilder;                 // used to create the XML document
        
        try {
            // create the XML document builder
            docFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docFactory.newDocumentBuilder();

            // create the XML document
            doc = docBuilder.newDocument();
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        
        this.roadTypeData = new Element[numRoads];
    }
    
    public void addData (SummaryData[] data)  {
        // local varibales
        Element roadElement;                        // used to create and add a new road element to the iteration node
        Attr laneCountAttr;                         // used to create and add a lane count attribute to a road node
        Attr densityAttr;
        Attr avgSpeedAttr;
        Attr avgSlowsAttr;                         // used to create and add a slow down count attribute to the iteration node
        Attr avgChangesAttr;                       // used to create and add a lane change count attribute to the iteration node
        Attr avgDecisionsAttr;                         // used to create and add a decision count attribute to the iteration node

        // iterate through the roads, creating child elements to
        // add to the current iteration element
        for (int i = 0; i < data.length; i++) {
        
            this.roadTypeData[i] = this.doc.createElement("roadType");

            // create the lane count attribute
            laneCountAttr = doc.createAttribute("laneCount");
            laneCountAttr.setValue(Integer.toString(data[i].getLaneCount()));

            // create the decisions attribute
            densityAttr = doc.createAttribute("density");
            densityAttr.setValue(Double.toString(data[i].getDensity()));

            // create the decisions attribute
            avgSpeedAttr = doc.createAttribute("avgSpeed");
            avgSpeedAttr.setValue(Double.toString(data[i].getAvgSpeed()));

            // create the decisions attribute
            avgSlowsAttr = doc.createAttribute("avgSlows");
            avgSlowsAttr.setValue(Double.toString(data[i].getAvgSlowDowns()));

            // create the decisions attribute
            avgChangesAttr = doc.createAttribute("avgChanges");
            avgChangesAttr.setValue(Double.toString(data[i].getAvgLaneChanges()));

            // create the decisions attribute
            avgDecisionsAttr = doc.createAttribute("avgDecisions");
            avgDecisionsAttr.setValue(Double.toString(data[i].getAvgDecisions()));
            
            // add the attributes to the new road element
            this.roadTypeData[i].setAttributeNode(laneCountAttr);
            this.roadTypeData[i].setAttributeNode(densityAttr);
            this.roadTypeData[i].setAttributeNode(avgSpeedAttr);
            this.roadTypeData[i].setAttributeNode(avgSlowsAttr);
            this.roadTypeData[i].setAttributeNode(avgChangesAttr);
            this.roadTypeData[i].setAttributeNode(avgDecisionsAttr);
        }
    }
    
    /**
     * Saves the data collected during an iteration of a simulation to an XML file.
     * The file name has the format: "rule[id]-simulation[id]-iteration[id].xml"
     * and is stored in C:\\database\\mcm\\traffic-model\\data\\
     * @param sim The current simulation.
     * @param iterationNum The current iteration number.
     */
    public void saveData(Simulation sim) {
        // local varibales
        TransformerFactory transformerFactory;      // used for converting the class structure into actual XML
        Transformer transformer;                    // used for converting the class structure into actual XML
        DOMSource source;                           // used for converting the class structure into actual XML
        StreamResult result;                        // used for streaming the data into the XML file
        Element rootElement;                        // used for creating the root element of the document
        Attr idAttr;                                // used for creating a date attribute to add to the root element
        Attr dateAttr;                              // used for creating a date attribute to add to the root element
        Attr iterCountAttr;                         // used for creating an iterationCount attribute to add to the root element
        Attr ruleAttr;                              // used for creating a rule attribute to add to the root element
        Attr durationAttr;                          // used for creating a duration attribut to add to the root element
        Date currentDate = new Date();              // gets the current date
        SimpleDateFormat xmlDateFormat;             // used to format the current date stored in the XML file                       // used to create and add a start speed attribute to a car node
        
        // setup the date format
        xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // create the root element: <simulation></simulation>
        rootElement = doc.createElement("simulation");

        // create the date attribute
        dateAttr = doc.createAttribute("date");
        dateAttr.setValue(xmlDateFormat.format(currentDate));

        // create the id attribute
        idAttr = doc.createAttribute("id");
        idAttr.setValue(Integer.toString(sim.getId()));

        // create the rule attribute
        ruleAttr = doc.createAttribute("rule");
        ruleAttr.setValue(sim.getRule().toString());

        // create the iteration count attribute
        iterCountAttr = doc.createAttribute("iterations");
        iterCountAttr.setValue(Integer.toString(sim.getIterationCount()));

        // create the duration attribute
        durationAttr = doc.createAttribute("duration");
        durationAttr.setValue(Long.toString(sim.duration()));

        // add the attributes to the root element
        rootElement.setAttributeNode(dateAttr);
        rootElement.setAttributeNode(idAttr);
        rootElement.setAttributeNode(ruleAttr);
        rootElement.setAttributeNode(iterCountAttr);
        rootElement.setAttributeNode(durationAttr);
        
        // iterate through our array of iteration nodes, adding each one to the root node
        for (int i = 0; i < this.roadTypeData.length; i++) {
            // add the new iteration element to the root node
            rootElement.appendChild(this.roadTypeData[i]);
            
        }

        // add the root element to the document
        doc.appendChild(rootElement);

        // now we write all the data to an XML file...
        try {
            // first, setup the objects need to convert the class structure to XML
            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            source = new DOMSource(doc);

            // setup the output stream to the XML file
            // where the file name is:
            //      rule[id]-simulation[id]-iteration[id].xml
            result = new StreamResult(
                new File("C:\\database\\mcm\\traffic-model\\data\\" +
                    "rule" + Integer.toString(sim.getRule().ordinal() + 1) + "-" +
                    "simulation" + Integer.toString(sim.getId()) + ".xml"));

            // save the XML to the file
            transformer.transform(source, result);

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
}
