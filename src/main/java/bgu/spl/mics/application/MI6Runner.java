package bgu.spl.mics.application;

import bgu.spl.mics.Message;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {
    public static void main(String[] args) {
        // this is for submission!!
        OurParser p = new OurParser(args[0]);
        String[] gadgets = p.get_Gadgets();
        Inventory.getInstance().load(gadgets);

        int M = p.getM();
        int Moneypenny = p.getMoneypenny();

        MissionInfo[][] missions = p.getMissionsInfo();
        CountDownLatch initLatch = new CountDownLatch(missions.length+M+Moneypenny+1);
        CountDownLatch terminateLatch = new CountDownLatch(missions.length+M+Moneypenny+2);
        CountDownLatch MterminateLatch = new CountDownLatch(M-1);

        Q q = new Q("Q",initLatch,terminateLatch);
        Thread threadQ = new Thread(q);
        threadQ.setName("Q");
        threadQ.start();

        Thread[] threadIntelligece = new Thread[missions.length];
        for (int i = 0; i <missions.length; i++) {
            Intelligence intelligence = new Intelligence("Intelligence", initLatch,terminateLatch, missions[i]);
            threadIntelligece[i] = new Thread(intelligence);
            threadIntelligece[i].setName("intelligence_"+i);
            threadIntelligece[i].start();
        }

        Thread[] threadM = new Thread[M];//M size
        for(int i=0; i<M; i++) {
            M m = new M("M",i,MterminateLatch,initLatch,terminateLatch);
            threadM[i] = new Thread(m);
            threadM[i].setName("M_"+i);
            threadM[i].start();
        }

        int duration = p.getDuration();

        Agent[] agents = p.getSquad();
        Squad.getInstance().load(agents);
        Thread[] threadMoneypenny = new Thread[Moneypenny];
        //Only one moneypenny thread for now
        for(int i=0; i<Moneypenny; i++) {//MoneyPenny size
            Moneypenny moneypenny = new Moneypenny("Moneypenny", i, initLatch, terminateLatch);
            threadMoneypenny[i] = new Thread(moneypenny);
            threadMoneypenny[i].setName("moneypenny_"+i);
            threadMoneypenny[i].start();
        }

//        MessageBrokerImpl.getInstance().printSubscribers();

        // Run TimeService:
        try {initLatch.await();}
        catch (InterruptedException e) {e.printStackTrace();}
        TimeService timeService = new TimeService("TimeService",duration,terminateLatch);
        Thread threadTimeService = new Thread(timeService);
        threadTimeService.setName("timer");
        threadTimeService.start();




        try {terminateLatch.await();}
        catch (InterruptedException e) {e.printStackTrace();}


        Diary diary = Diary.getInstance();

        // Create Output:
        Inventory.getInstance().printToFile(args[1]);
        diary.printToFile(args[2]);
    }
}
