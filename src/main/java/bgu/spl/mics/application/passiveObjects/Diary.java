package bgu.spl.mics.application.passiveObjects;

import com.google.gson.*;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
@SuppressWarnings( "deprecation" )
public class Diary {

	/**
	 * Retrieves the single instance of this class.
	 */
	private List<Report> reports;
	private int total;
	private Object totalLock = new Object();
	private Object reportsLock = new Object();

	public Diary() {
		reports = new LinkedList<Report>();
		total = 0;
	}
	public static Diary getInstance() {
		return instanceHolder.diaryInstance;
	}

	private static class instanceHolder {
		private static Diary diaryInstance = new Diary();
	}

	public List<Report> getReports() {
		return null;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd) {
		synchronized(reportsLock){reports.add(reportToAdd);}
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */

	public void printToFile(String filename) {
		JsonObject obj_json = new JsonObject();
		JsonArray reports_json = new JsonArray();
		for(Report report:reports)
		{
			JsonObject report_json = new JsonObject();
			report_json.addProperty("missionName",report.getMissionName());
			report_json.addProperty("m",report.getM());
			report_json.addProperty("moneypenny",report.getMoneypenny());

			JsonArray serials_json = new JsonArray();
			List<String> serials = report.getAgentsSerialNumbers();
			for(String serial: serials)
				serials_json.add(serial);
			report_json.add("agentsSerialNumbers",serials_json);

			JsonArray agentsNames_json = new JsonArray();
			List<String> names = report.getAgentsNames();
			for(String name: names)
				agentsNames_json.add(name);
			report_json.add("agentsNames",agentsNames_json);
			report_json.addProperty("gadgetName",report.getGadgetName());
			report_json.addProperty("timeCreated",report.getTimeCreated());
			report_json.addProperty("timeIssued", report.getTimeIssued());
			report_json.addProperty("qTime",report.getQTime());
			reports_json.add(report_json);
		}
		obj_json.add("reports",reports_json);
		obj_json.addProperty("total",total);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(obj_json.toString());
		String parsed = gson.toJson(je);

		try {
			FileWriter file = new FileWriter(filename);
			file.write(parsed);
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total;
	}

	/**
	 * Increments the total number of received missions by 1
	 */
	public void incrementTotal(){
		synchronized (totalLock) {total++;}
	}
}
