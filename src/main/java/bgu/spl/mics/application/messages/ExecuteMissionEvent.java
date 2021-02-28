package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class ExecuteMissionEvent implements Event {

    private Future<Boolean> toExecute;
    private MissionInfo mission;


    private Future<Boolean> msgRecieved;

    public ExecuteMissionEvent(Future<Boolean> toExecute, MissionInfo mission, Future<Boolean> msgRecieved) {
        this.toExecute = toExecute;
        this.mission = mission;
        this.msgRecieved = msgRecieved;
    }

    public Future<Boolean> getToExecute() {
        return toExecute;
    }

    public Future<Boolean> isMsgRecieved() {
        return msgRecieved;
    }

    public MissionInfo getMission() {
        return mission;
    }
}
