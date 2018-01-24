
import java.util.List;
import java.util.ArrayList;

// *****************************************************************************
// *****************************************************************************

// Class:           CollisionEvent
// Description:     Object class, representing a "collision event"
//                      between entities in a simulation

class Collision implements Comparable<Collision> {
    
    // List of entities involved in the collision:
    List<Entity> entities = new ArrayList<>();
    
    // x & y coordinates of the collision point:
    double xC, yC;
    
    // Time until the collision occurs:
    double tC;
    
    // *************************************************************************
    
    // Method:          Collision
    
    // Description:     Default constructor for the class
    
    // Parameters:      e1, e2 - Entities that are colliding
    //                  xC, yC - x & y coordinates of collision
    //                      tC - Time at which collision will occur
    
    // Returns:         A new instance of the class
    
    // Calls:           Vector
    
    // Globals:         entities
    //                  xC, yC, tC
    
    Collision (Entity e1, Entity e2, double xC, double yC, double tC) {
        
        entities.add(e1);
        entities.add(e2);
        
        this.xC = xC;
        this.yC = yC;
        
        // Round the collision time to the nearest ms:
        this.tC = Vector.roundDouble(tC, 3);
        
    }
    
    // *************************************************************************
    
    // Method:          computeCollisionTime
    
    // Description:     Computes the collision time from the given
    //                      acceleration, velocity, and distance values
    
    //                  Returns the value of the smallest, positive root,
    //                      or -1 if there is none
    
    // Parameters:      a - Acceleration
    //                  v - Velocity
    //                  d - Distance
    
    // Returns:         The value of the smallest, positive root,
    //                      or -1 if there is none
    
    // Calls:           Vector
    
    // Globals:         None
    
    static double computeCollisionTime (double a, double v, double d) {
        
        // Round inputs to the nano scale:
        a = Vector.roundDouble(a, 9);
        v = Vector.roundDouble(v, 9);
        
        // If the distance is 0:
        if (d == 0) {
            
            // Then no time is needed to travel the distance
            return 0;
            
        // If the acceleration is 0:
        } else if (a == 0) {
            
            // If the velocity is 0:
            if (v == 0) {
                
                // Then the entity is not moving
                return -1;
                
            // If the velocity is non-zero:
            } else {
                
                // Then the quadratic formula is not required:
                return d/v;
                
            }
        
        // Otherwise, use the quadratic formula:
        } else {
            
            // Do not take the square root of a negative number:
            double sqrt = v*v - 4*a*d;
            
            // If the value under the square root is negative:
            if (sqrt < 0) {
                
                // Return failure:
                return -1;
                
            // Otherwise:
            } else {

                // Find both roots
                double t1 = (-v + Math.sqrt(sqrt)) / (2*a);
                double t2 = (-v - Math.sqrt(sqrt)) / (2*a);

                // If both roots are negative:
                if (t1 < 0 && t2 < 0) {
                    return -1;
                }
                
                // If both roots are positive, return the smallest:
                if (t1 > 0 && t2 > 0) {
                    return Math.min(t1, t2);
                }
                
                // Otherwise, return the positive root:
                return Math.max(t1, t2);

            }
            
        }
        
    }
    
    // *************************************************************************
    
    // Method:          compareTo
    
    // Description:     Implementation of Comparable defining natural order
    
    // Parameters:      o - Collision being compared with this one
    
    // Returns:         -1: This object should be sorted earlier than o
    //                  +1: This object should be sorted later than o
    //                   0: This object is equal to o (in ordering)
    
    // Calls:           Nothing
    
    // Globals:         tC
    
    @Override
    public int compareTo(Collision o) {

        /* Sort by increasing collisionTime */
        
        if (tC < o.tC) {
            return -1;
        }
        
        if (tC > o.tC) {
            return +1;
        }
        
        return 0;
        
    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
