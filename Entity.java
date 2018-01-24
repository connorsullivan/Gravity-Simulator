
import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

// *****************************************************************************
// *****************************************************************************

// Class:           Entity
// Description:     Object class, representing a single "entity" in a run

class Entity {
    
    double mass, radius;
    
    Vector position, futurePosition, velocity, futureVelocity, acceleration;
    
    Simulation simulation;
    
    // The maximum distance the entity could travel in the next time step
    Vector maxDistance;
    
    // *************************************************************************
    
    // Method:          Entity
    
    // Description:     Default constructor for the class
    
    // Parameters:       r - Radius
    //                   x - X coordinate
    //                   y - Y coordinate
    //                  vX - X velocity
    //                  vY - Y velocity
    //                   s - Simulation containing the entity
    
    // Returns:         A new instance of the class
    
    // Calls:           Vector
    
    // Globals:         radius, mass
    //                  position, velocity, acceleration
    //                  futurePosition, futureVelocity
    //                  simulation
    
    Entity (double r, double x, double y, double vX, double vY, Simulation s) {
        
        radius = r;
        
        mass = Simulation.PI * radius * radius;

        position = new Vector(x, y);
        velocity = new Vector(vX, vY);
        acceleration = new Vector(0, 0);
        
        futurePosition = new Vector(position);
        futureVelocity = new Vector(velocity);
        
        simulation = s;
        
    }
    
    // *************************************************************************
    
    // Method:          getOtherEntities
    
    // Description:     Returns a list of all other entities in a run
    //                      (excluding itself)
    
    // Returns:         otherEntities
    
    // Calls:           Nothing
    
    // Globals:         simulation
    
    List<Entity> getOtherEntities() {
        
        ArrayList<Entity> otherEntities = new ArrayList<>();
        
        for (Entity e : simulation.entities) {
            if (!e.equals(this)) {
                otherEntities.add(e);
            }
        }
        
        return otherEntities;
        
    }
    
    // *************************************************************************
    
    // Method:          updateAcceleration
    
    // Description:     Computes and updates the entity's acceleration
    
    // Parameters:      None
    // Returns:         Nothing
    
    // Calls:           Vector
    //                  getOtherEntities
    
    // Globals:         acceleration
    //                  position, mass
    //                  simulation
    
    void updateAcceleration() {
        
        // Reset this entity's acceleration vector:
        acceleration.updateComponents(0, 0);
        
        // Get this entity's position:
        double x1 = position.getX();
        double y1 = position.getY();
        
        // Keep track of the new acceleration:
        double aX = 0;
        double aY = 0;
        
        // Consider all bodies in the system (besides this one):
        for (Entity e : getOtherEntities()) {
            
            // Get some parameters from the other entity:
            double x2 = e.position.getX();
            double y2 = e.position.getY();
            double m2 = e.mass;
            
            // Get the distance between the two entities:
            double r = Vector.computeDistance(x1, y1, x2, y2);
            
            // Get the magnitude and direction of the vector:
            double magnitude = (simulation.G * m2) / (r * r);
            double direction = Vector.computeVectorAngle(x1, y1, x2, y2);
            
            // Create the vector:
            Vector v = new Vector(magnitude, direction, 0);
            
            // Add the components to the entity's acceleration vector:
            aX += v.getX();
            aY += v.getY();
            
        }
        
        if (Double.isNaN(aX)) {
            aX = 0;
        }
        
        if (Double.isNaN(aY)) {
            aY = 0;
        }
        
        // Round the acceleration values:
        aX = Vector.roundDouble(aX, 9);
        aY = Vector.roundDouble(aY, 9);
        
        // Update the acceleration of the entity:
        acceleration.updateComponents(aX, aY);
        
    }
    
    // *************************************************************************
    
    // Method:          updateVelocity
    
    // Description:     Computes and updates the entity's futureVelocity
    //                  (actual velocity must be updated elsewhere)
    
    // Parameters:      t - Elapsed time (for the calculations)
    
    // Returns:         Nothing
    
    // Calls:           Vector
    
    // Globals:         futureVelocity
    //                  velocity, acceleration
    
    void updateVelocity (double t) {
        
        double vX_final = velocity.getX() + acceleration.getX() * t;
        double vY_final = velocity.getY() + acceleration.getY() * t;

        // Round the velocity values:
        vX_final = Vector.roundDouble(vX_final, 9);
        vY_final = Vector.roundDouble(vY_final, 9);
        
        // Update the future velocity of the entity:
        futureVelocity.updateComponents(vX_final, vY_final);
        
    }
    
    // *************************************************************************
    
    // Method:          updatePosition
    
    // Description:     Computes and updates the entity's futurePosition
    //                  (actual position must be updated elsewhere)
    
    //                  Also updates the entity's maxDistance
    
    // Parameters:      t - Elapsed time (for the calculations)
    
    // Returns:         Nothing
    
    // Calls:           Vector
    
    // Globals:         futurePosition
    //                  position, futureVelocity, acceleration
    //                  maxDistance
    
    void updatePosition (double t) {
        
        double x = position.getX();
        double y = position.getY();
        
        double vX = futureVelocity.getX();
        double vY = futureVelocity.getY();
        
        double aX = acceleration.getX();
        double aY = acceleration.getY();
        
        double xF = x + vX*t + 0.5*aX*t*t;
        double yF = y + vY*t + 0.5*aY*t*t;
        
        // Round the position values:
        xF = Vector.roundDouble(xF, 3);
        yF = Vector.roundDouble(yF, 3);
        
        // Update the future position of the entity:
        futurePosition.updateComponents(xF, yF);
        
        // Update the max distance of the entity:
        maxDistance = new Vector(Vector.computeDistance(x, y, xF, yF), Vector.computeVectorAngle(x, y, xF, yF), 0);
        
    }
    
    // *************************************************************************
    
    // Method:          confirmVelocity
    
    // Description:     Overwrites entity's futureVelocity into velocity
    
    // Parameters:      None
    // Returns:         Nothing
    
    // Calls:           Vector
    
    // Globals:         velocity, futureVelocity
    
    void confirmVelocity() {
        velocity.updateComponents(futureVelocity.getX(), futureVelocity.getY());
    }
    
    // *************************************************************************
    
    // Method:          confirmPosition
    
    // Description:     Overwrites entity's futurePosition into position
    
    // Parameters:      None
    // Returns:         Nothing
    
    // Calls:           Vector
    
    // Globals:         position, futurePosition
    
    void confirmPosition() {
        position.updateComponents(futurePosition.getX(), futurePosition.getY());
    }
    
    // *************************************************************************
    
    // Method:          computeCollisions
    
    // Description:     Determines if two entities will collide
    //                      in the given amount of time
    
    // Parameters:      otherEntity - The other entity in the equation
    //                  timeLimit - The allowed time limit for a collision
    
    // Returns:         Collision instance if the two entities collide,
    //                      otherwise returns null
    
    // Calls:           alternateCollision
    //                  Collision
    //                  Vector
    
    // Globals:         position, velocity, acceleration
    //                  radius, maxDistance
    
    Collision computeCollision (Entity otherEntity, double timeLimit) {
        
        // If both entities are on top of one another:
        if (position.getX() == otherEntity.position.getX()) {
            if (position.getY() == otherEntity.position.getY()) {
                return new Collision(this, otherEntity, position.getX(), position.getY(), 0);
            }
        }
        
        // If the slopes of the entities are equal:
        if (velocity.getSlope() == otherEntity.velocity.getSlope()) {
            
            // and if the entities are both moving:
            if (maxDistance.getMagnitude() > 0 && otherEntity.maxDistance.getMagnitude() > 0) {
                
                // Go to collision case #2:
                return alternateCollision(otherEntity, timeLimit);
            }
        }
        
        // Label the two entities:
        Entity e1 = this;
        Entity e2 = otherEntity;
        
        // Get some information:
        double xA = e1.position.getX();
        double yA = e1.position.getY();
        double sA = e1.velocity.getSlope();
        
        double xB = e2.position.getX();
        double yB = e2.position.getY();
        double sB = e2.velocity.getSlope();

        // Find the intersection of the entities' trajectories:
        double xC = (sA * xA - sB * xB + yB - yA) / (sA - sB);
        double yC = sA * (xC - xA) + yA;

        // Distance of each entity from the collision point:
        Vector dAC = new Vector(Vector.computeDistance(xA, yA, xC, yC), Vector.computeVectorAngle(xA, yA, xC, yC), 0);
        Vector dBC = new Vector(Vector.computeDistance(xB, yB, xC, yC), Vector.computeVectorAngle(xB, yB, xC, yC), 0);
        
        // Distance of each entity from the collision box:
        Vector dAi = new Vector(dAC.getMagnitude() - e2.radius, dAC.getDirection(), 0);
        Vector dAo = new Vector(dAC.getMagnitude() + e2.radius, dAC.getDirection(), 0);
        
        Vector dBi = new Vector(dBC.getMagnitude() - e1.radius, dBC.getDirection(), 0);
        Vector dBo = new Vector(dBC.getMagnitude() + e1.radius, dBC.getDirection(), 0);
        
        // If both of the entities are already in the collision box:
        if (dAC.getMagnitude() <= e2.radius && dBC.getMagnitude() <= e1.radius) {
            return new Collision(e1, e2, xC, yC, 0);
        }

        // Time until entity A arrives at collision box:
        double tAi, tAo;
        
        double aValue, bValue, cValue;

        // If x velocity is greater:
        if (Math.abs(e1.velocity.getX()) >= Math.abs(e1.velocity.getY())) {
            
            aValue = 0.5 * e1.acceleration.getX();
            bValue = e1.velocity.getX();
            cValue = -dAi.getX();
            
            tAi = Collision.computeCollisionTime(aValue, bValue, cValue);
            
            cValue = -dAo.getX();
            
            tAo = Collision.computeCollisionTime(aValue, bValue, cValue);
        
        // If y velocity is greater:
        } else {
            
            aValue = 0.5 * e1.acceleration.getY();
            bValue = e1.velocity.getY();
            cValue = -dAi.getY();
            
            tAi = Collision.computeCollisionTime(aValue, bValue, cValue);
            
            cValue = -dAo.getY();
            
            tAo = Collision.computeCollisionTime(aValue, bValue, cValue);
            
        }

        // Time until entity B arrives at collision box:
        double tBi, tBo;

        // If x velocity is greater:
        if (Math.abs(e2.velocity.getX()) >= Math.abs(e2.velocity.getY())) {
            
            aValue = 0.5 * e2.acceleration.getX();
            bValue = e2.velocity.getX();
            cValue = -dBi.getX();
            
            tBi = Collision.computeCollisionTime(aValue, bValue, cValue);
            
            cValue = -dBo.getX();
            
            tBo = Collision.computeCollisionTime(aValue, bValue, cValue);
        
        // If y velocity is greater:
        } else {
            
            aValue = 0.5 * e2.acceleration.getY();
            bValue = e2.velocity.getY();
            cValue = -dBi.getY();
            
            tBi = Collision.computeCollisionTime(aValue, bValue, cValue);
            
            cValue = -dBo.getY();
            
            tBo = Collision.computeCollisionTime(aValue, bValue, cValue);
            
        }

        // Use the max of tAi and tBi as the collision time:
        double timeToCollide = Math.max(tAi, tBi);

        // Make sure all time values are positive:
        if (tAi >= 0 && tAo >= 0 && tBi >= 0 && tBo >= 0) {

            // Make sure the time values are close enough to each other:
            if (tAi >= tBi && tAo <= tBo || tBi >= tAi && tBo <= tAo) {

                // Make sure that the collision will happen in this time step:
                if (timeToCollide <= timeLimit) {

                    // Record the collision:
                    return new Collision(e1, e2, xC, yC, timeToCollide);
                }
            }
        }
        
        // If no collision occurs, return null:
        return null;

    }
    
    // *************************************************************************
    
    // Method:          alternateCollision
    
    // Description:     Alternate collision handler for the computeCollision
    //                      that handles entities with equal slopes
    
    // Parameters:      otherEntity - The other entity in the equation
    //                  timeLimit - The allowed time limit for a collision
    
    // Returns:         Collision instance if the two entities collide,
    //                      otherwise returns null
    
    // Calls:           Collision
    //                  Simulation
    //                  Vector
    
    // Globals:         position, velocity, acceleration
    //                  futurePosition
    //                  radius, maxDistance
    
    Collision alternateCollision (Entity otherEntity, double timeLimit) {
        
        // Label the two entities:
        Entity e1 = this;
        Entity e2 = otherEntity;
        
        // Get some information about the entities:
        double xA = e1.position.getX();
        double yA = e1.position.getY();
        double sA = e1.velocity.getSlope();
        
        double xB = e2.position.getX();
        double yB = e2.position.getY();
        double sB = e2.velocity.getSlope();
        
        // Get the distance between the entities' trajectories:
        double line1x = xA;
        double line1y = yA;
        double line1s;
        if (sA == 0) {
            line1s = Math.tan(Simulation.PI/2);
        } else {
            line1s = -1/sA;
        }
        
        double xT = (line1s * line1x - sB * xB + yB - line1y) / (line1s - sB);
        double yT = line1s * (xT - line1x) + line1y;
        
        // Find the distance between the two trajectories:
        double trajectoryDistance = Vector.computeDistance(xA, yA, xT, yT);
        
        // Find dApart:
        Vector dApart = new Vector(Vector.computeDistance(xT, yT, xB, yB), Vector.computeVectorAngle(xT, yT, xB, yB), 0);
        
        // Check if the trajectories are close enough to each other:
        double criticalDistance = e1.radius + e2.radius;
        
        // If they are close enough:
        if (trajectoryDistance <= criticalDistance) {
            
            // Find out which entity is faster:
            Entity f, s;
            
            // If entity 1 is faster:
            if (e1.maxDistance.getMagnitude() > e2.maxDistance.getMagnitude()) {
                f = e1;
                s = e2;
                
            // If entity 2 is faster:
            } else {
                f = e2;
                s = e1;
            }
            
            // If the entities are moving in the same direction:
            if (Math.signum(f.maxDistance.getX()) == Math.signum(s.maxDistance.getX()) && Math.signum(f.maxDistance.getY()) == Math.signum(s.maxDistance.getY())) {
                
                // If the max distances are the same:
                if (f.maxDistance == s.maxDistance) {
                    
                    // Then no collision will occur
                    return null;
                }
                
                // Find the time until collision:
                double timeToCollide;
                
                double aXf = f.acceleration.getX();
                double aYf = f.acceleration.getY();
                
                double aXs = s.acceleration.getX();
                double aYs = s.acceleration.getY(); 
                
                double vXf = f.velocity.getX();
                double vYf = f.velocity.getY();
                
                double vXs = s.velocity.getX();
                double vYs = s.velocity.getY();
                
                if (Math.abs(dApart.getX()) > Math.abs(dApart.getY())) {
                    
                    // Use the x component for time calculations:
                    timeToCollide = Collision.computeCollisionTime(0.5 * (aXf - aXs), (vXf - vXs), -dApart.getX());
                    
                } else {
                    
                    // Use the y component for time calculations:
                    timeToCollide = Collision.computeCollisionTime(0.5 * (aYf - aYs), (vYf - vYs), -dApart.getY());
                }
                
                // Make sure the collision time is appropriate:
                if (timeToCollide >= 0 && timeToCollide < timeLimit) {
                    
                    // Find the positions along the trajectories where the entities meet:
                    double d_slowerToInt_X = s.velocity.getX() * timeToCollide + 0.5 * s.acceleration.getX() * timeToCollide * timeToCollide;
                    double d_slowerToInt_Y = s.velocity.getY() * timeToCollide + 0.5 * s.acceleration.getY() * timeToCollide * timeToCollide;
                    
                    double sXC = s.position.getX() + d_slowerToInt_X;
                    double sYC = s.position.getY() + d_slowerToInt_Y;
                    
                    double d_fasterToInt_X = d_slowerToInt_X + dApart.getX();
                    double d_fasterToInt_Y = d_slowerToInt_Y + dApart.getY();
                    
                    double fXC = f.position.getX() + d_fasterToInt_X;
                    double fYC = f.position.getY() + d_fasterToInt_Y;
                    
                    double collisionPointX = (sXC + fXC) / 2;
                    double collisionPointY = (sYC + fYC) / 2;
                    
                    // Return the collision event:
                    return new Collision(f, s, collisionPointX, collisionPointY, timeToCollide);
                    
                }
            
            // If the entities are moving in opposite directions:
            } else {
                
                Vector dApartStart = new Vector(dApart);
                
                // Determine if the entities will collide:
                line1x = e1.futurePosition.getX();
                line1y = e1.futurePosition.getY();
                
                xB = e2.futurePosition.getX();
                yB = e2.futurePosition.getY();
                
                xT = (line1s * line1x - sB * xB + yB - line1y) / (line1s - sB);
                yT = line1s * (xT - line1x) + line1y;

                Vector dApartEnd = new Vector(Vector.computeDistance(xT, yT, xB, yB), Vector.computeVectorAngle(xT, yT, xB, yB), 0);
                
                // If the entities are heading towards each other:
                if (dApartEnd.getMagnitude() < dApartStart.getMagnitude() + s.maxDistance.getMagnitude() + f.maxDistance.getMagnitude()) {
                    
                    // Find the time until collision:
                    double timeToCollide;

                    double aXf = f.acceleration.getX();
                    double aYf = f.acceleration.getY();

                    double vXf = f.velocity.getX();
                    double vYf = f.velocity.getY();
                    
                    double aXs = s.acceleration.getX();
                    double aYs = s.acceleration.getY();

                    double vXs = s.velocity.getX();
                    double vYs = s.velocity.getY();
                    
                    if (Math.abs(dApartStart.getX()) > Math.abs(dApartStart.getY())) {

                        // Use the x component for time calculations:
                        timeToCollide = Collision.computeCollisionTime(0.5 * (aXf + aXs), (vXf - vXs), -dApartStart.getX());

                    } else {

                        // Use the y component for time calculations:
                        timeToCollide = Collision.computeCollisionTime(0.5 * (aYf + aYs), (vYf - vYs), -dApartStart.getY());
                    }
                    
                    // Make sure the collision time is appropriate:
                    if (timeToCollide >= 0 && timeToCollide < timeLimit) {

                        // Find the positions along the trajectories where the entities meet:
                        double d_slowerToInt_X = s.velocity.getX() * timeToCollide + 0.5 * s.acceleration.getX() * timeToCollide * timeToCollide;
                        double d_slowerToInt_Y = s.velocity.getY() * timeToCollide + 0.5 * s.acceleration.getY() * timeToCollide * timeToCollide;

                        double sXC = s.position.getX() + d_slowerToInt_X;
                        double sYC = s.position.getY() + d_slowerToInt_Y;

                        double d_fasterToInt_X = d_slowerToInt_X + dApart.getX();
                        double d_fasterToInt_Y = d_slowerToInt_Y + dApart.getY();

                        double fXC = f.position.getX() + d_fasterToInt_X;
                        double fYC = f.position.getY() + d_fasterToInt_Y;

                        double collisionPointX = (sXC + fXC) / 2;
                        double collisionPointY = (sYC + fYC) / 2;

                        // Return the collision event:
                        return new Collision(f, s, collisionPointX, collisionPointY, timeToCollide);

                    }
                    
                }
                
            }
            
        }
        
        // No collision will occur
        return null;
        
    }
    
    // *************************************************************************
    
    // Method:          getColorValue
    
    // Description:     Computes the appropriate color for the given
    //                      entity mass and simulation area
    
    // Parameters:      entityMass - The mass of the entity to color
    //                  simulationArea - The original area of the simulation
    
    // Returns:         color - The color for the given parameters
    
    // Calls:           Nothing
    // Globals:         None
    
    static Color getColorValue (double entityMass, double simulationArea) {
        
        Color color;
        
        // Color is dependent on ratio of Entity mass to Simulation area
        double ratio = entityMass/simulationArea;
        
        if (ratio <= 0.00001) {
            color = new Color(255,255,255);     // white
        } else if (ratio <= 0.00004) {
            color = new Color(0,255,255);       // cyan
        } else if (ratio <= 0.00016) {
            color = new Color(30,144,255);      // medium blue
        } else if (ratio <= 0.00064) {
            color = new Color(0,0,255);         // blue
        } else if (ratio <= 0.00256) {
            color = new Color(138,43,226);      // violet
        } else if (ratio <= 0.01024) {
            color = new Color(192,192,192);     // silver
        } else if (ratio <= 0.04096) {
            color = new Color(0,255,0);         // green
        } else if (ratio <= 0.16384) {
            color = new Color(244,164,96);      // light brown
        } else if (ratio <= 0.65536) {
            color = new Color(255,0,0);         // red
        } else {
            color = new Color(255,255,0);       // yellow
        }
        
        return color;
        
    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
