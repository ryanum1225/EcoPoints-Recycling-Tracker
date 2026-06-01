import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main app to run the Eco-Points Recycling Tracker.
 */
public class EcoPointsRecyclingTracker {
    private static Map<String, Household> households = new HashMap<>();
    private static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        // First, load households from file.
        loadHouseholdsFromFile();

        boolean running = true;
        while (running) {
            System.out.println("\n=== Eco-Points Recycling Tracker ===");
            System.out.println("1. Register Household");
            System.out.println("2. Log Recycling Event");
            System.out.println("3. Display Households");
            System.out.println("4. Display Household Recycling Events");
            System.out.println("5. Generate Reports");
            System.out.println("6. Save and Exit");
            System.out.print("Choose an option: ");

            String choice = scan.nextLine();

			
            switch(choice)
            {
                case "1":
                    registerHousehold();
                    break;
                case "2":
                    logRecyclingEvent();
                    break;
                case "3":
                    displayHouseholds();
                    break;
                case "4":
                    displayHouseholdEvents();
                    break;
                case "5":
                    generateReports();
                    break;
                case "6":
                    saveHouseholdsToFile();
                    running = false;
                    continue;
                default:
                    System.out.println("Invalid choice. Please select 1-6.");
            }
		}
	}

    /**
     * Method to register a new household to the app. When the user enters their household information,
     * the household is added to the HashMap.
     */
    public static void registerHousehold()
    {
        // Prompt the user to enter a unique household ID
        System.out.print("Enter household ID: ");
        String houseId = scan.nextLine().trim();

        // Check if a household with this ID already exists in the map, and return if it does.
        if (households.containsKey(houseId))
        {
            System.out.println("Error: Household ID already exists.");
			return;
        }

        // Prompt the user to enter the household's name
        System.out.print("Enter household name: ");
        String houseName = scan.nextLine().trim();


        // Prompt the user to enter the household's address
        System.out.print("Enter household address: ");
        String houseAddr = scan.nextLine().trim();

        // Create a new Household object using the provided details
        Household newHousehold = new Household(houseId, houseName, houseAddr);

        // Add the new household to the households map (using ID as the key)
        households.put(houseId, newHousehold);

        // Confirm to the user that the household was registered successfully
        System.out.println("Household registered successfully on " + newHousehold.getJoinDate());
    }

    /**
     * Method to log a new Recycling Event for an existing household. The user inputs the material type,
     * and the weight, which is then added to their household as a new event.
     */
    public static void logRecyclingEvent()
    {
        // Ask the user for the household ID
        System.out.print("Enter household ID: ");
        String houseId = scan.nextLine().trim();

        // Look up the household in the map by ID
        Household household = households.get(houseId);

        // If household not found, show error and exit
        if (household == null)
        {
            System.out.println("Error: Household ID not found.");
            return;
        }

        // Ask the user for the material type they recycled
		System.out.print("Enter material type (plastic/glass/metal/paper): ");
		String material = scan.nextLine().trim();

        double weight = 0.0;

        // Loop until a valid weight is entered
        while (true)
        {
            try
            {
                System.out.print("Enter weight in kilograms: ");
				weight = Double.parseDouble(scan.nextLine());       // Convert string input to double.

                if (weight <= 0) throw new IllegalArgumentException();

                break;
            }
            catch (NumberFormatException num)
            {
                System.out.println("Invalid weight. Must be a positive number.");
            }
            catch (IllegalArgumentException arg)
            {
                System.out.println("Invalid weight. Must be a positive number.");
            }
        }

        // Create a new RecyclingEvent using the material and weight
        RecyclingEvent recyclingEvent = new RecyclingEvent(material, weight);

        // Add the new event to the household and update points
        household.addEvent(recyclingEvent);

        // Show success message with points earned
        System.out.println("Recycling event logged! Points earned: " + recyclingEvent.getEcoPoints());
    }

    /**
     * Method that displays a list of all registered households. If no households exist, then the method
     * displays a message and exits.
     */
    public static void displayHouseholds()
    {
        // Check if households map is empty.
        if (households.isEmpty())
        {
            System.out.println("No households registered.");
            return;
        }

        // If there are households, print a header first.
        System.out.println("\nRegistered Households:");

        // Loop through each household in the map and print its details
		for (Household h : households.values()) 
        {
			System.out.println("ID: " + h.getId() +
							   ", Name: " + h.getName() +
							   ", Address: " + h.getAddress() +
							   ", Joined: " + h.getJoinDate());
        }
    }

    /**
     * Method that displays all recylcing events for a specific household. Also displays the total weight
     * and points earned.
     */
    public static void displayHouseholdEvents()
    {
        // Ask the user for the household ID
        System.out.print("Enter household ID: ");
        String houseId = scan.nextLine().trim();

        // Look up the household in the map by ID
        Household household = households.get(houseId);

        // If household not found, show error and exit
        if (household == null)
        {
            System.out.println("Error: Household ID not found.");
            return;
        }

        // Print a header with the household's name
		System.out.println("\nRecycling Events for " + household.getName() + ":");

        // Check if the household has any recycling events
		if (household.getEvents().isEmpty()) 
        {
			System.out.println("No events logged.");
		} 
        else 
        {
			// Loop through all recycling events and print each one
			for (RecyclingEvent e : household.getEvents()) {
				//Print the stringified version of the event
				System.out.println(e + "\n");
			}

			// After listing events, show the total weight recycled by this household
			System.out.println("\nTotal Weight: " + household.getTotalWeight() + " kg");

			// Show the total eco points earned by this household
			System.out.println("Total Points: " + household.getTotalPoints() + " pts");
		}
    }

    /**
     * Method that displays the household with the highest total eco points, and the total community
     * recycling weight.
     */
    public static void generateReports()
    {
        // Check if there are any households registered, and return if there are no households stored.
		if (households.isEmpty()) {
			System.out.println("No households registered.");
			return;
		}

        // Find the household with the hightest total eco points.
        Household topHousehold = null;
        for (Household h : households.values())
        {
            // If topHousehold is still null, or the iterated household has more points than topHousehold,
            // then set h as the new topHousehold.
            if (topHousehold == null || h.getTotalPoints() > topHousehold.getTotalPoints())
            {
                topHousehold = h;
            }
        }

        // Print details of the top household.
		System.out.println("\nHousehold with Highest Points:");
		System.out.println("ID: " + topHousehold.getId() +
						   ", Name: " + topHousehold.getName() +
						   ", Points: " + topHousehold.getTotalPoints());

        // Find the total community recycling weight.
        double totalWeight = 0.0;
        for (Household h : households.values())
        {
            totalWeight += h.getTotalWeight();
        }

        // Print total community weight
		System.out.println("Total Community Recycling Weight: " + totalWeight + " kg");
    }

    /**
     * Writes all the households to a file named "households.ser". This allows data to persist, even
     * when the program is closed.
     */
    public static void saveHouseholdsToFile()
    {
        try
        {
            // Create a FileOutputStream to write to the file named "households.ser"
			ObjectOutputStream out = new ObjectOutputStream(
				new FileOutputStream("households.ser")
			);
			// Write the entire households map to the file
			out.writeObject(households);

            // Close ObjectOutputStream to prevent resource leak.
            out.close();

            // If successful, print confirmation message.
            System.out.println("Data successfully saved.");
        }
        catch (IOException e)
        {
            // If something goes wrong while saving, print an error message
			System.out.println("Error saving data: " + e.getMessage());
        }
    }

    /**
     * Method that loads all household data from "households.ser". The method has error handling for
     * if the file does not exist, or if the file is unreadable or corrupted.
     */
    @SuppressWarnings("unchecked") // Suppresses unchecked cast warning when reading the object
    public static void loadHouseholdsFromFile()
    {
        // Create a FileInputStream to read from a file named "households.ser"
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("households.ser")))
        {
            // Read the object from the file and cast it back to the correct type.
            households = (Map<String, Household>) in.readObject();

            // Print a confirmation message to let users know data was loaded.
            System.out.println("Household data loaded.");
        }
        catch (FileNotFoundException e)
        {
            // Display message if file does not exist yet.
            System.out.println("No saved data found. Starting fresh.");
        }
        catch (IOException | ClassNotFoundException e)
        {
            // Handle other errors, like if the file is corrupted or unreadable.
            System.out.println("Error loading data: " + e.getMessage());
        }
    }


}
