package bgu.spl.mics.application.subscribers;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
@SuppressWarnings("unchecked")
public class M extends Subscriber {
	private int currTick;
	private Diary diary;
	private int serialNumber;
	private int QTime;
	private CountDownLatch initLatch, terminateLatch, MterminateLatch;
	private Future<Pair<Boolean,Integer>> agentEvRes;
	private Future<Pair<Boolean,int[]>> gadgetEvRes;
	private Future<Boolean> msgRecieved = new Future<Boolean>();
	Future<Boolean> toExecute;
	private int[] currTime;
	private Future<List<String>> executionEvRes;
	private MissionInfo mission;
	public M(String name, int serialNumber, CountDownLatch MterminateLatch, CountDownLatch initCountDown, CountDownLatch terminateCountDown) {
		super(name);
		this.serialNumber = serialNumber;
		this.initLatch = initCountDown;
		this.terminateLatch = terminateCountDown;
		this.MterminateLatch = MterminateLatch;
	}

	@Override
	protected void initialize() {
		diary = Diary.getInstance();
		subscribeEvent(MissionRecievedEvent.class, msg ->
		{
			diary.incrementTotal();
			mission = msg.getMission();
			agentEvRes = sendEvent(new AgentsAvailableEvent(mission));
			if (agentEvRes.get().getKey()) {
				gadgetEvRes = sendEvent(new GadgetAvailableEvent(mission));
				QTime = gadgetEvRes.get().getValue()[0];
				currTime = gadgetEvRes.get().getValue();
			} else {
				gadgetEvRes = new Future<>();
				gadgetEvRes.resolve(new Pair(false, new int[1]));
			}
			toExecute = new Future<Boolean>();
			if (gadgetEvRes.get().getKey()) {
				executionEvRes = sendEvent(new ExecuteMissionEvent(toExecute, mission, msgRecieved));
				msgRecieved.get();
				if (currTime[0] <= mission.getTimeExpired()) {//fixed!
					toExecute.resolve(true);
					diary.addReport(createReport());
				} else {
					toExecute.resolve((false));
				}
			} else {
				if (agentEvRes.get().getKey()) {
					sendEvent(new AbortMissionEvent(mission));
				}
			}
		});
		this.<TickBroadcast>subscribeBroadcast(TickBroadcast.class, msg -> {
			currTick = msg.getTick();
			if (msg.getDuration() == currTick) {
				if (0 < serialNumber) {
					this.terminate();
					MterminateLatch.countDown();
					terminateLatch.countDown();
				} else { // serialNumber == 0.
					try {
						MterminateLatch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendBroadcast(new TerminateBroadcast());
					this.terminate();
					terminateLatch.countDown();
				}
			}
		});
		initLatch.countDown();
	}
	private Report createReport() {
		Report report = new Report();
		report.setMissionName(mission.getMissionName());
		report.setM(serialNumber);
		report.setMoneypenny(agentEvRes.get().getValue());
		report.setTimeIssued(mission.getTimeIssued());
		report.setQTime(QTime);
		report.setTimeCreated(mission.getTimeIssued());
		report.setAgentsSerialNumbers(mission.getSerialAgentsNumbers());
		report.setAgentsNames(executionEvRes.get());
		report.setGadgetName(mission.getGadget());
		return report;
	}

}
