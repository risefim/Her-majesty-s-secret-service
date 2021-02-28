package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link -Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private Timer timer;
	private int currTick;
	private int duration;
	private CountDownLatch terminateLatch;


	public TimeService(String name, int duration,CountDownLatch terminateLatch) {
		super(name);
		this.terminateLatch = terminateLatch;
		this.duration=duration;
		initialize();
	}

	@Override
	protected void initialize() {
		timer=new Timer();
		currTick = 1;
	}
	@Override
	public void run() {
		Thread timeSerivceThread = Thread.currentThread();
		TimerTask task = new TimerTask() {
			public void run() {
				if(currTick <= duration+1) {
					sendBroadcast(new TickBroadcast(currTick,duration));
					currTick++;
				}
				else{
					timer.cancel();
					timer.purge();
					timeSerivceThread.interrupt();
					terminateLatch.countDown();
				}
			}
		};
		timer.scheduleAtFixedRate(task,0, 100);
	}

}
