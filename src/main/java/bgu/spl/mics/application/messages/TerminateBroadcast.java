package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class TerminateBroadcast implements Broadcast {
    private int currentTick;
    private int duration;
    public TerminateBroadcast() {
        this.currentTick = currentTick;
        this.duration = duration;
    }
    public int getTick() {
        return currentTick;
    }
    public int getDuration() { return duration;}
}
