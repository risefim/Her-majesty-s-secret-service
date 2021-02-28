package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import com.google.gson.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
@SuppressWarnings("deprecation")
public final  class OurParser {
    private JsonObject jo;

    public OurParser(String filename) {
        start(filename);
    }
    public void start(String filename) {
        JsonParser jp = new JsonParser();
        try {
            jo =(JsonObject) jp.parse(new FileReader(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String[] get_Gadgets(){
        JsonArray gadgets_json = jo.get("inventory").getAsJsonArray();
        String[] gadgets = new String[gadgets_json.size()];
        for(int i=0; i< gadgets_json.size(); i++)
            gadgets[i] = gadgets_json.get(i).getAsString();
        return gadgets;
    }
    public int getM() {
        int M = jo.getAsJsonObject("services").get("M").getAsInt();
        return M;
    }
    public int getMoneypenny(){
        int Moneypenny = jo.getAsJsonObject("services").get("Moneypenny").getAsInt();
        return Moneypenny;
    }
    public MissionInfo[][] getMissionsInfo() {
        JsonArray missionsArr_json = jo.getAsJsonObject("services").get("intelligence").getAsJsonArray();
        MissionInfo[][] missions = new MissionInfo[missionsArr_json.size()][];
        JsonArray[] missions_json = new JsonArray[missionsArr_json.size()];
        for(int i=0; i< missionsArr_json.size(); i++) {
            missions_json[i] = missionsArr_json.get(i).getAsJsonObject().get("missions").getAsJsonArray();
            missions[i] = new MissionInfo[missions_json[i].size()];
            for (int j = 0; j < missions_json[i].size(); j++) {
                JsonArray serialAgentsNumbers_json = missions_json[i].get(j).getAsJsonObject().get("serialAgentsNumbers").getAsJsonArray();
                List<String> serialAgentsNumbers = new LinkedList<String>();
                for (int k = 0; k < serialAgentsNumbers_json.size(); k++)
                    serialAgentsNumbers.add(serialAgentsNumbers_json.get(k).getAsString());
                int duration = missions_json[i].get(j).getAsJsonObject().get("duration").getAsInt();
                String gadget = missions_json[i].get(j).getAsJsonObject().get("gadget").getAsString();
                String missionName = missions_json[i].get(j).getAsJsonObject().get("name").getAsString();
                int timeExpired = missions_json[i].get(j).getAsJsonObject().get("timeExpired").getAsInt();
                int timeIssued = missions_json[i].get(j).getAsJsonObject().get("timeIssued").getAsInt();
                missions[i][j] = new MissionInfo(serialAgentsNumbers, duration, gadget, missionName, timeExpired, timeIssued);
            }
        }
        return missions;
    }
    public int getDuration() {
        int duration = jo.getAsJsonObject("services").get("time").getAsInt();
        return duration;
    }
    public Agent[] getSquad() {
        JsonArray agents_json = jo.getAsJsonArray("squad");
        Agent[] agents = new Agent[agents_json.size()];
        for (int i = 0; i < agents_json.size(); i++) {
            agents[i] = new Agent();
            String name = agents_json.get(i).getAsJsonObject().get("name").getAsString();
            String serial = agents_json.get(i).getAsJsonObject().get("serialNumber").getAsString();
            agents[i].setName(name);
            agents[i].setSerialNumber(serial);
        }
        return agents;
    }
}


