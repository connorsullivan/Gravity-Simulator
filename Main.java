
// Program:         Gravity Simulation
// Course:          COSC 460

// Description:     This program simulates and graphically displays the
//                  interaction of entities under the influence of gravity
//                  in an infinite, two-dimensional space.

//                  (Assumes no other forces are present besides gravity)

// Author:          Connor Sullivan
// Revised:         December 2016

// Language:        Java
// IDE:             NetBeans 8.2

// *****************************************************************************
// *****************************************************************************

// Class:           Main
// Description:     Contains the main method for the program

class Main {
    
    static KeyboardInputClass kb;
    static Simulation s;
    
    // *************************************************************************
    
    // Method:          main
    
    // Description:     Main method of the program
    
    // Parameters:      args
    
    // Returns:         Nothing
    
    // Calls:           KeyboardInputClass, Simulation
    
    // Globals:         kb, s

    public static void main(String[] args) {
        
        System.out.println("\n\nGravity Simulator: by Connor Sullivan\n");
        
        kb = new KeyboardInputClass();
        
        while (true) {
            
            if (kb.getInteger(true, 1, 0, 1, "\nPress ENTER to launch new simulation (0 to exit program):") == 1) {
                
                s = new Simulation();
                
                s.addBodies();
                s.simulationMenu();
                
            } else {
                System.out.println("\nExiting program...\n");
                System.exit(0);
            }
            
        }
        
    }
    
    // *************************************************************************
    
}

// *****************************************************************************
// *****************************************************************************
