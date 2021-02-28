package bgu.spl.mics.application.passiveObjects;

import com.google.gson.*;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets;
	private HashMap <String,Boolean> gadgetsAvailable;

	/**
	 * Retrieves the single instance of this class.
	 */
	public Inventory() {
		gadgets = new LinkedList<String>();
	}
	private static class instanceHolder {
		private static Inventory inventoryInstance = new Inventory();
	}
	public static Inventory getInstance() {
		return instanceHolder.inventoryInstance;
	}


	/**
	 * Initializes the inventory. This method adds all the items given to the gadget
	 * inventory.
	 * <p>
	 * @param inventory 	Data structure containing all data necessary for initialization
	 * 						of the inventory.
	 */
	public void load (String[] inventory) {
		for(String gadget:inventory) {
			gadgets.add(gadget);
		}

	}

	/**
	 * acquires a gadget and returns 'true' if it exists.
	 * <p>
	 * @param gadget 		Name of the gadget to check if available
	 * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
	 */
	public boolean getItem(String gadget) {
		boolean exist = gadgets.contains(gadget);
		if(exist) gadgets.remove(gadget);
		return exist;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgeds.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) {
		JsonArray gadgets_json = new JsonArray();
		for(String gadget:gadgets)
			gadgets_json.add(gadget);
		try {
			FileWriter file = new FileWriter(filename);
			file.write(String.valueOf(gadgets_json));
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
