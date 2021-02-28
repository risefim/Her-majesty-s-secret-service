package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int currentTick;
    private int duration;
    public TickBroadcast(int currentTick, int duration) {
        this.currentTick = currentTick;
        this.duration = duration;
    }
    public int getTick() {
        return currentTick;
    }
    public int getDuration() { return duration;}
}
