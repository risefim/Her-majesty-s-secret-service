package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class MissionRecievedEvent implements Event {
    private MissionInfo mission;

    public MissionRecievedEvent(MissionInfo mission) {
        this.mission = mission;
    }
    public MissionInfo getMission() {
        return mission;
    }
}
