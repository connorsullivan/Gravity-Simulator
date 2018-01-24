
import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

// *****************************************************************************
// *****************************************************************************

// Class:           RandomGenerator
// Description:     Class for generating random values in a specified range

class RandomGenerator {
    
    static Random r = new Random();
    
    // *************************************************************************
    
    // Method:          generateRandomInteger
    
    // Description:     Generates a random Integer value
    //                      in the range [min, max]
    
    // Parameters:      min - minimum value (inclusive)
    //                  max - maximum value (inclusive)
    
    // Returns:         Randomly generated Integer value
    
    // Calls:           Nothing
    
    // Globals:         r

    static int generateRandomInteger (int min, int max) {
        
        int range = max - min + 1;
        
        return r.nextInt(range) + min;
        
    }
    
    // *************************************************************************
    
    // Method:          generateRandomDouble
    
    // Description:     Generates a random Double value
    //                      in the range [min, max]
    //                      with the specified amount of precision
    
    // Parameters:            min - minimum value (inclusive)
    //                        max - maximum value (inclusive)
    //                  precision - # of desired decimal places
    
    // Returns:         Randomly generated Double value
    
    // Calls:           Nothing
    
    // Globals:         r
    
    static double generateRandomDouble (double min, double max, int precision) {
        
        double range = max - min;
        
        double value = ( range * r.nextDouble() ) + min;
        
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
     
        return bd.doubleValue();
        
    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
