package bgu.spl.mics.application.passiveObjects;
import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

	private static class instanceHolder {
		private static Squad squadInstance = new Squad();
	}

	private Map<String, Agent> agents;
	public Squad() {}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Squad getInstance() {
		return instanceHolder.squadInstance;
	}

	/**
	 * Initializes the squad. This method adds all the agents to the squad.
	 * <p>
	 * @param agents 	Data structure containing all data necessary for initialization
	 * 						of the squad.
	 */
	public void load (Agent[] agents) {
		this.agents = new ConcurrentHashMap<String,Agent>();
		for(int i=0; i<agents.length; i++)
			this.agents.put(agents[i].getSerialNumber(),agents[i]);
	}

	/**
	 * Releases agents.
	 */
	public synchronized void releaseAgents(List<String> serials) {
		for (String serial : serials){
			agents.get(serial).release();
			notifyAll();
		}
	}


	/**
	 * simulates executing a mission by calling sleep.
	 * @param time   milliseconds to sleep
	 */
	public void sendAgents(List<String> serials, int time) {
		try { // simulating mission execution.
			Thread.sleep((long)(time*100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		releaseAgents(serials);
	}



	/**
	 * acquires an agent, i.e. holds the agent until the caller is done with it
	 * @param serials   the serial numbers of the agents
	 * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
	 */
	public boolean getAgents(List<String> serials) {
		if(!agentsExist(serials)) return false;
		synchronized(this) {
			boolean flag=agentsAvailable(serials);
			while(!flag) {
				try {wait();}
				catch (InterruptedException e) {e.printStackTrace();}
				flag=agentsAvailable(serials);
			}
			aquireAgents(serials);
		}
		return true;
	}
	private boolean agentsAvailable(List<String> serials) {
		boolean agentsAvailable = true;
		Iterator it=serials.iterator();
		while (agentsAvailable&&it.hasNext())
		{
			String cur=(String)it.next();
			if (!agents.get(cur).isAvailable())
				agentsAvailable=false;
		}
		return  agentsAvailable;
	}
	private boolean agentsExist(List<String> serials) {
		boolean agentsExist = true;
		for (String serial : serials)
			if (!agents.containsKey(serial)) agentsExist = false;
		return  agentsExist;
	}
	private void aquireAgents(List<String> serials) {
		for(String serial:serials){
			agents.get(serial).acquire();
		}

	}


	/**
	 * gets the agents names
	 * @param serials the serial numbers of the agents
	 * @return a list of the names of the agents with the specified serials.
	 */
	public List<String> getAgentsNames(List<String> serials){
		List<String> names= new LinkedList<String>();
		for(String serial:serials)
			names.add(agents.get(serial).getName());
		return names;
	}

}
