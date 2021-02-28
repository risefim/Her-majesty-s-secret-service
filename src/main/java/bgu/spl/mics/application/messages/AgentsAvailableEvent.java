package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.subscribers.Moneypenny;

import java.util.List;

public class AgentsAvailableEvent implements Event {
    private MissionInfo mission;

    public AgentsAvailableEvent(MissionInfo mission) {this.mission = mission;}

    public MissionInfo getMission() {return mission;}

}
