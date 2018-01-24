
import java.math.BigDecimal;
import java.math.RoundingMode;

// *****************************************************************************
// *****************************************************************************

// Class:           Vector
// Description:     Object class representing a vector

class Vector {
    
    // r = Magnitude of vector
    // t = Direction of vector
    // s = Slope of vector
    private double r, t, x, y, s;
    
    // *************************************************************************
    
    // Method:          Vector
    
    // Description:     Constructor for the class
    
    // Parameters:      magnitude
    //                  direction - (in radians)
    //                  dummy - dummy value to allow for extra constructor
    
    // Returns:         A new instance of the class

    // Calls:           computeSlope
    
    // Globals:         r, t, x, y, s
    
    Vector (double magnitude, double direction, double dummy) {
       
        r = magnitude;
        t = direction;
        
        x = magnitude * Math.cos(direction);
        y = magnitude * Math.sin(direction);
        
        s = computeSlope(x, y);
        
    }
    
    // *************************************************************************
    
    // Method:          Vector
    
    // Description:     Constructor for the class
    
    // Parameters:      x, y - Components of the vector
    
    // Returns:         A new instance of the class

    // Calls:           computeVectorMagnitude
    //                  computeSlope
    
    // Globals:         r, t, x, y, s
    
    Vector (double x, double y) {
        
        r = computeVectorMagnitude(x, y);
        t = Math.atan2(y, x);
        
        this.x = x;
        this.y = y;
        
        s = computeSlope(x, y);
        
    }
    
    // *************************************************************************
    
    // Method:          Vector
    
    // Description:     Constructor for the class
    
    // Parameters:      v - Vector to copy into the new instance
    
    // Returns:         A new instance of the class

    // Calls:           Nothing
    
    // Globals:         r, t, x, y, s
    
    Vector (Vector v) {
        
        r = v.r;
        t = v.t;
        
        x = v.x;
        y = v.y;
        
        s = v.s;
        
    }
    
    // *************************************************************************
    
    // Method:          updateComponents
    
    // Description:     Updates the components of the vector and recalculates
    //                      other parameters
    
    // Parameters:      xNew, yNew
    
    // Returns:         Nothing

    // Calls:           computeVectorMagnitude
    //                  computeSlope
    
    // Globals:         r, t, x, y, s
    
    void updateComponents (double xNew, double yNew) {
        
        x = xNew;
        y = yNew;
        
        r = computeVectorMagnitude(x, y);
        t = Math.atan2(y, x);
        
        s = computeSlope(x, y);
        
    }
    
    // *************************************************************************
    
    // Method:          getMagnitude
    
    // Description:     Getter for the vector magnitude
    
    // Parameters:      Nothing
    
    // Returns:         r

    // Calls:           Nothing
    
    // Globals:         r
    
    double getMagnitude() {
        return r;
    }
    
    // *************************************************************************
    
    // Method:          getDirection
    
    // Description:     Getter for the vector direction
    
    // Parameters:      Nothing
    
    // Returns:         t

    // Calls:           Nothing
    
    // Globals:         t
    
    double getDirection() {
        return t;
    }
    
    // *************************************************************************
    
    // Method:          getX
    
    // Description:     Getter for the vector x component
    
    // Parameters:      Nothing
    
    // Returns:         x

    // Calls:           Nothing
    
    // Globals:         x
    
    double getX() {
        return x;
    }
    
    // *************************************************************************
    
    // Method:          getY
    
    // Description:     Getter for the vector y component
    
    // Parameters:      Nothing
    
    // Returns:         y

    // Calls:           Nothing
    
    // Globals:         y
    
    double getY() {
        return y;
    }
    
    // *************************************************************************
    
    // Method:          getSlope
    
    // Description:     Getter for the vector slope
    
    // Parameters:      Nothing
    
    // Returns:         s

    // Calls:           Nothing
    
    // Globals:         s
    
    double getSlope() {
        return s;
    }
    
    // *************************************************************************
    
    // Method:          computeDistance
    
    // Description:     Computes the distance between the two point sets
    
    // Parameters:      x1, y1 - The 1st point
    //                  x2, y2 - The 2nd point
    
    // Returns:         The distance between the points
    
    // Calls:           Nothing
    // Globals:         None
    
    static double computeDistance (double x1, double y1, double x2, double y2) {
        
        double dX = x2 - x1;
        double dY = y2 - y1;
        
        return Math.sqrt((dX * dX) + (dY * dY));

    }
    
    // *************************************************************************
    
    // Method:          computeSlope
    
    // Description:     Computes slope from x & y values
    
    // Parameters:      x, y
    
    // Returns:         slope

    // Calls:           Nothing
    // Globals:         None
    
    static double computeSlope (double x, double y) {
        
        if (y == 0) {
            return 0;
        }
        
        if (x == 0) {
            return Math.tan(Simulation.PI/2);
        }
        
        double slope = y/x;
        
        if (Math.abs(slope) < 1E-3) {
            slope = 0;
        }
        
        return slope;
        
    }
    
    // *************************************************************************
    
    // Method:          computeVectorAngle (Steve Donaldson)
    
    // Description:     Computes the angle a vector makes with the x-axis (0-2*PI)
    //                  assuming a counter-clockwise (positive) rotation.
    //                  The Math.atan() method computes an angle (-PI/2-PI/2)
    //                  whereas what is frequently needed is the full angle of rotation.
    
    // Parameters:      tailX, tailY, headX, headY - endpoints of the vector
    
    // Returns:         vectorAngle - angle the vector makes with the x-axis
    
    // Calls:           Nothing
    // Globals:         None
    
    static double computeVectorAngle (double tailX, double tailY, double headX, double headY) {
        
        double vectorAngle = Math.atan((headY - tailY) / (headX - tailX));
        
        // If vector is in the 1st or 4th quadrant
        if (headX >= tailX) {
            
            // If vector is in the 4th quadrant
            if (headY < tailY) {
                
                // Angle returned by Math.atan() was negative in this case
                vectorAngle = 2.0 * Math.PI + vectorAngle;
            }
            
        // headX < tailX (vector is in 2nd or 3rd quadrant)
        } else {
            
            // Angle returned by Math.atan() could be positive or negative
            vectorAngle += Math.PI;
        }
        
        return vectorAngle;
        
    }
    
    // *************************************************************************
    
    // Method:          computeVectorMagnitude
    
    // Description:     Computes vector magnitude from the components
    
    // Parameters:      x, y
    
    // Returns:         The magnitude of the vector
    
    // Calls:           Nothing
    // Globals:         None
    
    static double computeVectorMagnitude (double x, double y) {
        return Math.sqrt( (x*x) + (y*y) );
    }
    
    // *************************************************************************
    
    // Method:          roundDouble
    
    // Description:     Rounds a double value to the given precision
    
    // Parameters:      value
    //                  precision - The number of decimal places to round to
    
    // Returns:         The rounded value
    
    // Calls:           Nothing
    // Globals:         None
    
    static double roundDouble (double value, int precision) {
        
        BigDecimal b = new BigDecimal(value).setScale(precision, RoundingMode.HALF_UP);
        
        return b.doubleValue();
        
    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
