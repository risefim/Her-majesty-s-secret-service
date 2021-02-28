package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.MissionRecievedEvent;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.concurrent.CountDownLatch;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
@SuppressWarnings("unchecked")
public class Intelligence extends Subscriber {
	private MissionInfo[] missions;
	private CountDownLatch initLatch, terminateLatch;
	public Intelligence(String name, CountDownLatch initLatch,CountDownLatch terminateLatch
			, MissionInfo[] missions) {
		super(name);
		this.missions = missions;
		this.initLatch = initLatch;
		this.terminateLatch = terminateLatch;
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, message->{
			int currTick = message.getTick();
			if(message.getDuration() < currTick) {
				this.terminate();
				terminateLatch.countDown();
			}
			else {
				for (MissionInfo mission : this.missions)
					if (mission.getTimeIssued() == currTick) {
						sendEvent(new MissionRecievedEvent(mission));
					}
			}
		});
		initLatch.countDown();
	}

}
