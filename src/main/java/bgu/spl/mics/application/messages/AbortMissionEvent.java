package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class AbortMissionEvent implements Event {

    private MissionInfo mission;

    public AbortMissionEvent(MissionInfo mission) {
        this.mission = mission;
    }

    public MissionInfo getMission() {
        return mission;
    }

}
