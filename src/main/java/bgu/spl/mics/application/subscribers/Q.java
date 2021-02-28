package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import javafx.util.Pair;

import java.util.concurrent.CountDownLatch;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
@SuppressWarnings("unchecked")
public class Q extends Subscriber {
	private int count = 0;
	private CountDownLatch initLatch, terminateLatch;
	private Inventory inventory;
	private boolean terminated = false;
	private int[] currTick = new int[1];

	public Q(String name, CountDownLatch initLatch, CountDownLatch terminateCountDown ) {
		super(name);
		this.initLatch = initLatch;
		this.terminateLatch = terminateCountDown;
	}

	@Override
	protected void initialize() {
		inventory = Inventory.getInstance();
		subscribeEvent(GadgetAvailableEvent.class,msg ->{
			boolean gadgetAvailable;
			if(terminated) gadgetAvailable = false;
			else gadgetAvailable = inventory.getItem(msg.getMission().getGadget());
			complete(msg, new Pair<>(gadgetAvailable, currTick));
		});
		subscribeBroadcast(TickBroadcast.class,msg->{
			currTick[0] = msg.getTick();
			if(msg.getDuration() < msg.getTick()) terminated = true;
		});
		subscribeBroadcast(TerminateBroadcast.class, msg-> {
			this.terminate();
			terminateLatch.countDown();
		});
		initLatch.countDown();

	}

}
