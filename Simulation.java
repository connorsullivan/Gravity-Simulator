
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;
import java.awt.Color;
import java.util.LinkedList;

// *****************************************************************************
// *****************************************************************************

// Class:          Simulation
// Description:    Object class, representing a "simulation" instance
//                  as used by the Gravity Simulation program

class Simulation {
    
    KeyboardInputClass kb;
    
    // Global list of entities in the simulation
    List<Entity> entities;
    
    // Dimensions of the visible simulation space
    int rows = 800;
    int cols = 800;
    int area = rows*cols;
    
    int scale = 1;
    
    // Number of iterations performed before collisions are computed
    int grace = 0;
    
    // Hard limits on data
    final double r_Min = 0.5, r_Max = 100;
    final double x_Min = -cols / 2, x_Max = cols / 2;
    final double y_Min = -rows / 2, y_Max = rows / 2;
    final double v_Min = -100, v_Max = 100;
    
    // Soft limits on data (for random generation)
    double r_LowerBound = 5, r_UpperBound = 25;
    double x_LowerBound = -300, x_UpperBound = +300;
    double y_LowerBound = -300, y_UpperBound = +300;
    double v_LowerBound = -10, v_UpperBound = +10;
    
    // Default values
    double r_Default = 10;
    double x_Default = 0, y_Default = 0;
    double v_Default = 0;
    
    int i_default = 1;
    
    double z_Default = 2.00;
    double p_Default = 1.00;
    
    // Delay for smoother graphics
    int graphicsDelay = 0;
    
    // Constants for the simulation computations
    static double G = 1.00;
    
    static double PI = Math.PI;
    
    // Display entities as a solid color vs just an outline
    boolean fillEntities = false;
    
    ImageConstruction display;
    
    // Display collision data on each iteration
    boolean showCollisionOutput = false;
    
    // *************************************************************************
    
    // Method:          Simulation
    
    // Description:     Default constructor for the class
    
    // Parameters:      None
    
    // Returns:         A new instance of the class
    
    // Calls:           KeyboardInputClass
    
    // Globals:         kb
    //                  entities
    //                  rows, cols, area, scale
    //                  grace
    
    Simulation() {
        
        kb = new KeyboardInputClass();
        
        entities = new ArrayList<>();
        
        // Get new display parameters from the user:
        if (kb.getCharacter(true, 'N', "YN", 1, "\nChange default window size? (default = N):") == 'Y') {
            
            rows = kb.getInteger(true, rows, 1, 4096, "\nRows? (default = " + rows + "):");
            cols = kb.getInteger(true, cols, 1, 4096, "\nCols? (default = " + cols + "):");
            area = rows*cols;
            
            scale = kb.getInteger(true, scale, 0, 100, "\nScale? (default = " + scale + "):");
            
        }
        
        grace = kb.getInteger(true, grace, 0, 1000000, "\nNumber of time steps to pass before collisions? (default = " + grace + "):");
        
    }
    
    // *************************************************************************
    
    // Method:          addBodies
    
    // Description:     Adds entities to the simulation
    
    // Parameters:      None  
    // Returns:         Nothing
    
    // Calls:           KeyboardInputClass
    //                  Entity
    //                  updateDisplay
    
    // Globals:         kb
    //                  entities
    //                  global vector parameters
    
    void addBodies() {
        
        System.out.println("\nChoose one of the following options for entity setup:");
        System.out.println("1. Randomly configure a collection of N entities");
        System.out.println("2. Specify radius, position, and velocity parameters for one or more entites");
        System.out.println("3. Pre-configured conditions");
        int entitySetup = kb.getInteger(true, 1, 1, 3, "\nChoice? (default = random):");
        
        // Quick setup for collision testing:
        if (entitySetup == 3) {
            
            System.out.println("\nPre-configured conditions:");
            System.out.println("1. Converging on origin from top");
            System.out.println("2. Converging on origin from bottom");
            System.out.println("3. Converging on origin from all sides");
            System.out.println("4. Smaller entity orbiting larger one");
            switch (kb.getInteger(true, 1, 1, 4, "\nChoice?")) {
                
                case 1:
                    entities.add(new Entity(20, -200, +200, +10, -10, this));
                    entities.add(new Entity(20, +200, +200, -10, -10, this));
                    updateDisplay();
                    return;
                case 2:
                    entities.add(new Entity(20, -200, -200, +10, +10, this));
                    entities.add(new Entity(20, +200, -200, -10, +10, this));
                    updateDisplay();
                    return;
                case 3:
                    entities.add(new Entity(20, -200, +200, +10, -10, this));
                    entities.add(new Entity(20, +200, +200, -10, -10, this));
                    entities.add(new Entity(20, -200, -200, +10, +10, this));
                    entities.add(new Entity(20, +200, -200, -10, +10, this));
                    updateDisplay();
                    return;
                case 4:
                    entities.add(new Entity(100, 0, 0, 0, 0, this));
                    entities.add(new Entity(20, -200, -200, +10, -5, this));
                    updateDisplay();
                    return;
            }
            
        }
        
        // Getting initial parameters for adding the bodies:
        int bodiesToAdd = kb.getInteger(true, 10, 0, 100, "\nNumber of entities? (0 to quit and return to simulation) (default = 10):");
        if (bodiesToAdd == 0) {
            updateDisplay();
            return;
        }
        
        // Define ranges for randomly generated data:
        if (entitySetup == 1) {
            
            // Radius
            r_LowerBound = kb.getDouble(true, r_LowerBound, r_Min, r_Max, "\nLower bound for randomly generated radii? (default = " + r_LowerBound + "):");
            r_UpperBound = kb.getDouble(true, r_UpperBound, r_LowerBound, r_Max, "\nUpper bound for randomly generated radii? (default = " + r_UpperBound + "):");
            
            // Position
            x_LowerBound = kb.getDouble(true, x_LowerBound, x_Min, x_Max, "\nLower X bound for position grouping? (default = " + x_LowerBound + "):");
            y_LowerBound = kb.getDouble(true, y_LowerBound, y_Min, y_Max, "\nLower Y bound for position grouping? (default = " + y_LowerBound + "):");
            x_UpperBound = kb.getDouble(true, x_UpperBound, x_LowerBound, x_Max, "\nUpper X bound for position grouping? (default = " + x_UpperBound + "):");
            y_UpperBound = kb.getDouble(true, y_UpperBound, y_LowerBound, y_Max, "\nUpper Y bound for position grouping? (default = " + y_UpperBound + "):");
            
            // Velocity
            v_LowerBound = kb.getDouble(true, v_LowerBound, v_Min, v_Max, "\nMinimum allowable velocity? (default = " + v_LowerBound + "):");
            v_UpperBound = kb.getDouble(true, v_UpperBound, v_LowerBound, v_Max, "\nMaximum allowable velocity? (default = " + v_UpperBound + "):");
            
        }
        
        // Adding the bodies to the list:
        for (int i = 0; i < bodiesToAdd; i++) {
            
            double R, x, y, vX, vY;
            
            switch (entitySetup) {
                
                // Randomly configured (within boundaries):
                case 1:
                    
                     R = RandomGenerator.generateRandomDouble(r_LowerBound, r_UpperBound, 1);
                    
                     x = RandomGenerator.generateRandomDouble(x_LowerBound, x_UpperBound, 1);
                     y = RandomGenerator.generateRandomDouble(y_LowerBound, y_UpperBound, 1);
                    
                    vX = RandomGenerator.generateRandomDouble(v_LowerBound, v_UpperBound, 1);
                    vY = RandomGenerator.generateRandomDouble(v_LowerBound, v_UpperBound, 1);
                    
                    break;
                    
                // User-specified configuration:
                case 2:

                    R = kb.getDouble(true, r_Default, r_Min, r_Max, "\nRadius for entity " + (i + 1) + "/" + bodiesToAdd + " ? (0 to quit and return to simulation) (default = " + r_Default + "):");
                    if (R == 0) {
                        updateDisplay();
                        return;
                    } else {
                        r_Default = R;
                    }

                    x = kb.getDouble(true, x_Default, x_Min, x_Max, "\nX position for entity " + (i + 1) + "/" + bodiesToAdd + " ? (default = " + x_Default + "):");
                    x_Default = x;
                    y = kb.getDouble(true, y_Default, y_Min, y_Max, "\nY position for entity " + (i + 1) + "/" + bodiesToAdd + " ? (default = " + y_Default + "):");
                    y_Default = y;

                    vX = kb.getDouble(true, v_Default, v_Min, v_Max, "\nX velocity for entity " + (i + 1) + "/" + bodiesToAdd + " ? (default = " + v_Default + "):");
                    v_Default = vX;
                    vY = kb.getDouble(true, v_Default, v_Min, v_Max, "\nY velocity for entity " + (i + 1) + "/" + bodiesToAdd + " ? (default = " + v_Default + "):");
                    v_Default = vY;

                    break;
                    
                default:
                    
                    R = r_Default;
                    
                    x = x_Default;
                    y = y_Default;
                    
                    vX = v_Default;
                    vY = v_Default;
                    
                    break;
                
            }
            
            entities.add(new Entity(R, x, y, vX, vY, this));
            
        } // end of adding bodies to the list!

        updateDisplay();
        
    }
    
    // *************************************************************************
    
    // Method:          simulationMenu
    
    // Description:     Displays the main menu for the simulation
    
    // Parameters:      None
    // Returns:         Nothing
    
    // Calls:           KeyboardInputClass
    //                  iterate
    //                  updateDisplay
    //                  addBodies
    //                  updateDisplay
    //                  ImageConstruction
    
    // Globals:         kb
    //                  i_default
    //                  z_default
    //                  p_default
    //                  G, fillEntities, graphicsDelay
    //                  entities
    //                  display
    
    void simulationMenu() {
        
        // Standard time step (default value = 1.0)
        double tS = 1.0;
        
        // Loop menu until user decides to exit
        while (true) {

            System.out.println("\nSimulation Menu:\n");

            System.out.println("Iterate     I");
            System.out.println("Zoom        ZI = Zoom In; ZO = Zoom Out");
            System.out.println("Pan         PU = Pan Up; PD = Pan Down; PL = Pan Left; PR = Pan Right; H = Home");
            System.out.println("Time        T");
            System.out.println("Change      C");
            System.out.println("Show        S");
            System.out.println("Restart     R");
            System.out.println("Exit        E");
            
            String choice = kb.getString("I", "\nChoice? (default = I):");
            choice = choice.toUpperCase();
            
            switch (choice) {
                
                case "I":
                    loop: while (true) {
                        i_default = kb.getInteger(true, i_default, 0, 1000000, "\nNumber of iterations? (0 to return to menu) (default = " + i_default + "):");
                        switch (i_default) {
                            case 0:
                                i_default = 1;
                                break loop;
                            case 1:
                                iterate(tS);
                                break;
                            default:
                                for (int i = 0; i < i_default; i++) {
                                    iterate(tS);
                                }
                                break;
                        }
                    }
                    break;
                    
                case "ZI": case "ZO":
                    
                    z_Default = kb.getDouble(true, z_Default, 1, 100, "\nZoom factor? (default = " + z_Default + "):");
                    
                    switch (choice) {
                        case "ZI":
                            display.xLeft /= z_Default;
                            display.xRight /= z_Default;
                            display.yBottom /= z_Default;
                            display.yTop /= z_Default;
                            break;
                        case "ZO":
                            display.xLeft *= z_Default;
                            display.xRight *= z_Default;
                            display.yBottom *= z_Default;
                            display.yTop *= z_Default;
                            break;
                    }
                    
                    // Recalculate image parameters:
                    display.xRange = display.xRight-display.xLeft;
                    display.yRange = display.yTop-display.yBottom;
                    
                    updateDisplay();
                    
                    break;
                    
                case "PU": case "PD": case "PL": case "PR":
                    
                    p_Default = kb.getDouble(true, p_Default, 0.1, 100, "\nPan factor? (default = " + p_Default + "):");
                    
                    double panX = display.xRange * p_Default;
                    double panY = display.yRange * p_Default;
                    
                    switch (choice) {
                        case "PU":
                            display.yBottom += panY;
                            display.yTop += panY;
                            break;
                        case "PD":
                            display.yBottom -= panY;
                            display.yTop -= panY;
                            break;
                        case "PL":
                            display.xLeft -= panX;
                            display.xRight -= panX;
                            break;
                        case "PR":
                            display.xLeft += panX;
                            display.xRight += panX;
                            break;
                    }
                    
                    updateDisplay();
                    
                    break;
                    
                case "H":
                    
                    display.xLeft = -display.xRange/2;
                    display.xRight = display.xRange/2;
                    display.yBottom = -display.yRange/2;
                    display.yTop = display.yRange/2;
                    
                    updateDisplay();
                    
                    break;
                    
                case "T":
                    
                    System.out.println("\nCurrent standard time step is " + tS);
                    System.out.println("");
                    System.out.println("1. Double the current time step");
                    System.out.println("2. Half the current time step");
                    System.out.println("3. Go back");
                    switch (kb.getInteger(true, 3, 1, 3, "\nChoice? (default = go back):")) {
                        case 1:
                            tS = tS*2;
                            System.out.println("\nStandard time step is now " + tS);
                            break;
                        case 2:
                            tS = tS/2;
                            System.out.println("\nStandard time step is now " + tS);
                            break;
                        case 3:
                            break;
                    }
                    
                    break;
                    
                case "C":
                    
                    System.out.println("\nParameter editing menu:");
                    System.out.println("1. Create additional entities");
                    System.out.println("2. Change value of G");
                    System.out.println("3. Switch between solid colored or outlined entities");
                    System.out.println("4. Specify delay time between graphics updates");
                    System.out.println("5. Go back");
                    switch (kb.getInteger(true, 5, 1, 5, "\nChoice? (default = go back):")) {
                        case 1:
                            addBodies();
                            break;
                        case 2:
                            G = kb.getDouble(true, G, 0, 1000000, "\nValue for G? (default = " + G + ")");
                            break;
                        case 3:
                            if (fillEntities == false) {
                                fillEntities = true;
                                System.out.println("\nAll entities set to solid colored.");
                            } else {
                                fillEntities = false;
                                System.out.println("\nAll entities set to outline only.");
                            }
                            updateDisplay();
                            break;
                        case 4:
                            graphicsDelay = kb.getInteger(true, graphicsDelay, 0, 1000, "\nSpecify graphics update delay in milliseconds (default = " + graphicsDelay + "):");
                            break;
                        case 5:
                            break;
                    }
                    
                    break;
                    
                case "S":
                    
                    System.out.println("\nThere are " + entities.size() + " entities in the simulation.");
                    System.out.println("\n X       Y        vX      vY       aX      aY     R");
                    
                    for (Entity e : entities) {
                        System.out.printf("%6.1f, %6.1f, %6.1f, %6.1f, %6.1f, %6.1f, %6.1f\n", e.position.getX(), e.position.getY(), e.velocity.getX(), e.velocity.getY(), e.acceleration.getX(), e.acceleration.getY(), e.radius);
                    }
                    
                    if (kb.getCharacter(true, 'N', "YN", 1, "\nDisplay collision information on each iteration? (Y/N, default = N):") == 'Y') {
                        showCollisionOutput = true;
                    } else {
                        showCollisionOutput = false;
                    }
                    
                    break;
                    
                case "R":
                    if (kb.getCharacter(true, 'N', "YN", 1, "\nAre you sure you want to restart the simulation? (Y/N, default = N):") == 'Y') {
                        display.closeDisplay();
                        return;
                    }
                    break;
                    
                case "E":
                    if (kb.getCharacter(true, 'N', "YN", 1, "\nAre you sure you want to exit the simulation? (Y/N, default = N):") == 'Y') {
                        display.closeDisplay();
                        System.out.println("\nExiting program...\n");
                        System.exit(0);
                    }
                    break;
                    
                default:
                    System.out.println("\nInput not recognized! Please select one of the options in brackets.");
                    break;
                    
            }

        }
        
    }
    
    // *************************************************************************
    
    // Method:          updateDisplay
    
    // Description:     Updates the graphics display with the latest data
    //                      from the simulation
    
    // Parameters:      None
    // Returns:         Nothing
    
    // Calls:           ImageConstruction
    //                  Entity
    
    // Globals:         display
    //                  entities
    //                  mass, area, radius
    //                  Vector
    //                  graphicsDelay
    
    void updateDisplay() {
        
        // If the display hasn't been set up yet:
        if (display == null) {
            display = new ImageConstruction(rows+1, cols+1, -cols/2, cols/2, -rows/2, rows/2, scale);
            display.displayImage(true, "Gravity Simulation", false);
        // Otherwise, wipe the old image:
        } else {
            display.clearImage(0, 0, 0);
        }
        
        // For all entities in the simulation:
        for (Entity e : entities) {
            
            // Update velocity and position with future values:
            e.confirmVelocity();
            e.confirmPosition();
            
            // Get the appropriate color for this entity:
            Color c = Entity.getColorValue(e.mass, area);
            
            // Insert this entity into the image:
            display.insertCircle(e.position.getX(), e.position.getY(), e.radius, c.getRed(), c.getGreen(), c.getBlue(), fillEntities);
            
        }
        
        // Overwrite the new pixel values in the RGB arrays:
        display.setPixelValues();
        
        // Wait a moment for smoother graphics:
        try {
            Thread.sleep(graphicsDelay);
        } catch (Exception e) {
            // Don't do anything
        }
        
    }
    
    // *************************************************************************
    
    // Method:          iterate
    
    // Description:     Performs one iteration of the simulation
    
    // Parameters:      tS - The value of the standard time step
    
    // Returns:         Nothing
    
    // Calls:           Entity
    //                  updateDisplay
    
    // Globals:         entities
    //                  grace
    
    void iterate (double tS) {
        
        // Set the working time step equal to the standard time step:
        double tW = tS;
        
        // Compute acceleration, futureVelocity, and futurePosition
        for (Entity e : entities) {
            e.updateAcceleration();
            e.updateVelocity(tW);
            e.updatePosition(tW);
        }
        
        // Check for grace period before computing collisions:
        if (grace == 0) {

            // Compute all collisions (handshake problem):
            List<Collision> collisions = new LinkedList<>();
            for (int i = 0; i < entities.size(); i++) {
                Entity currentEntity = entities.get(i);
                for (int j = i + 1; j < entities.size(); j++) {
                    Collision currentCollision = currentEntity.computeCollision(entities.get(j), tW);
                    if (currentCollision != null) {
                        collisions.add(currentCollision);
                    }
                }
            }
            
            if (showCollisionOutput) {
                System.out.println("\nThere were " + collisions.size() + " collisions to process in the last time step.");
                if (!collisions.isEmpty()) {
                    System.out.println("");
                    for (Collision c: collisions) {
                        System.out.println("\nCollision at " + c.xC + ", " + c.yC + " with time " + c.tC);
                    }
                }
            }

            // If there were collisions:
            if (!collisions.isEmpty()) {

                // Sort them and only process the ones occurring soonest:
                Collections.sort(collisions);
                double shortestCollisionTime = collisions.get(0).tC;
                for (Collision c : new ArrayList<>(collisions)) {
                    if (c.tC > shortestCollisionTime) {
                        collisions.remove(c);
                    }
                }

                // Keep track of the entities that were collided:
                List<Entity> newEntites = processCollisions(collisions);

                // Change the duration of the working time step:
                tW = shortestCollisionTime;

                // For the new working time step, re-compute vectors:
                for (Entity e : entities) {
                    
                    // Only for entities not involved in collisions
                    if (!newEntites.contains(e)) {
                        e.updateVelocity(tW);
                        e.updatePosition(tW);
                    }
                }

            }

        } else {
            
            // Decrement grace period by elapsed time
            grace--;
            
        }
        
        // Update the simulation:
        updateDisplay();
        
    }
    
    // *************************************************************************
    
    // Method:          processCollisions
    
    // Description:     Translates a list of recorded collision events
    //                      into actual collisions by colliding the entities
    //                      into the resulting new entities and updating
    //                      the simulation accordingly
    
    // Parameters:      collisions - The collision event(s) to process
    
    // Returns:         A list of the newly created entities
    
    // Calls:           combineEntities
    
    // Globals:         entities
    
    List<Entity> processCollisions (List<Collision> collisions) {
        
        // Keep a list of new entities that are created:
        List<Entity> newEntities = new ArrayList<>();
        
        // Keep looping until all collisions are processed:
        while (!collisions.isEmpty()) {
            
            // Set of entities to be combined into a single entity:
            Set<Entity> toBeCombined = new LinkedHashSet<>();
            
            // List of x & y collision points for each individual collision:
            List<Vector> collisionPoints = new ArrayList<>();
            
            for (Collision c: new ArrayList<>(collisions)) {
                
                boolean foundMatch = false;
                for (Entity e: c.entities) {
                    if (toBeCombined.contains(e)) {
                        foundMatch = true;
                    }
                }

                if (foundMatch || toBeCombined.isEmpty()) {

                    // Add all of the entities in this collision:
                    for (Entity e: c.entities) {
                        toBeCombined.add(e);
                    }

                    // Add the x & y coordinate of the collision:
                    collisionPoints.add(new Vector(c.xC, c.yC));
                    
                    // Remove the collision:
                    collisions.remove(c);

                }
                
            }
            
            // Combine the entities and add the new entity to the list:
            newEntities.add(combineEntities(toBeCombined, collisionPoints));
            
        }
        
        // Return the list of newly created entities:
        return newEntities;
        
    }
    
    // *************************************************************************
    
    // Method:          combineEntities
    
    // Description:     Helper method for processCollisions, which takes in
    //                      a list of entities and collision points and
    //                      combines the entities into one new entity
    
    // Parameters:      toBeCombined - The entities to be combined
    //                  collisionPoints - The points at which the entities collided
    
    // Returns:         The new entity resulting from the collision of all entities
    //                  in the passed in list
    
    // Calls:           Entity
    
    // Globals:         entities
    //                  mass, velocity, PI
    
    Entity combineEntities(Set<Entity> toBeCombined, List<Vector> collisionPoints) {

        // Get the TOTAL mass and momentum of the system:
        double M = 0, pX = 0, pY = 0;
        for (Entity e : toBeCombined) {
            
            M += e.mass;
            
            pX += e.mass * e.velocity.getX();
            pY += e.mass * e.velocity.getY();
            
        }

        // Get the radius of the resulting entity:
        double R = Math.sqrt(M/PI);

        // Center of resulting entity:
        double xC = 0;
        double yC = 0;
        for (Vector v: collisionPoints) {
            xC += v.getX();
            yC += v.getY();
        }
        xC /= collisionPoints.size();
        yC /= collisionPoints.size();
        
        // Velocity of the resulting entity:
        double vX = pX / M;
        double vY = pY / M;

        // Remove the old entities and add the new one to the global list:
        for (Entity e : toBeCombined) {
            entities.remove(e);
        }
        
        Entity newEntity = new Entity(R, xC, yC, vX, vY, this);
        entities.add(newEntity);

        // Return the new entity:
        return newEntity;

    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
