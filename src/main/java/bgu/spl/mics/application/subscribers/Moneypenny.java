package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
@SuppressWarnings("unchecked")
public class Moneypenny extends Subscriber {
	private int serialNumber;
	private Squad squad;
	private int currTick;
	private CountDownLatch initLatch, terminateLatch;
	public Moneypenny(String name, int serialNumber, CountDownLatch initCountDown, CountDownLatch terminateLatch) {
		super(name);
		this.serialNumber = serialNumber;
		this.initLatch = initCountDown;
		this.terminateLatch = terminateLatch;
	}

	@Override
	protected void initialize() {
		squad = Squad.getInstance();
		if(serialNumber%2 == 0) {
			subscribeEvent(AgentsAvailableEvent.class, msg->{
				//checking existence of agents (available or not).
				boolean exist;
				List<String> agentsNumbers = msg.getMission().getSerialAgentsNumbers();
				exist = squad.getAgents(agentsNumbers);
				complete(msg,new Pair<Boolean,Integer>(exist,serialNumber));
			});
		}
		else {
			subscribeEvent(ExecuteMissionEvent.class, msg -> {
				msg.isMsgRecieved().resolve(true);
				if (!msg.getToExecute().get()) {
					squad.releaseAgents(msg.getMission().getSerialAgentsNumbers());
				} else {
					squad.sendAgents(msg.getMission().getSerialAgentsNumbers(), msg.getMission().getDuration());
				}
				complete(msg, squad.getAgentsNames(msg.getMission().getSerialAgentsNumbers()));
			});

			subscribeEvent(AbortMissionEvent.class, msg -> {
				squad.releaseAgents(msg.getMission().getSerialAgentsNumbers());
				complete(msg, true);
			});
		}

		subscribeBroadcast(TickBroadcast.class, msg->{
			currTick = msg.getTick();
		});
		subscribeBroadcast(TerminateBroadcast.class, msg-> {
			this.terminate();
			terminateLatch.countDown();
		});
		initLatch.countDown();

	}

}
