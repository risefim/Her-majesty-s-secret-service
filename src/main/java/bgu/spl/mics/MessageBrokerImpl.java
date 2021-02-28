package bgu.spl.mics;

import bgu.spl.mics.application.messages.AbortMissionEvent;
import bgu.spl.mics.application.messages.MissionRecievedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.subscribers.M;
import javafx.util.Pair;
import bgu.spl.mics.Future;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
@SuppressWarnings ("unchecked")
public class MessageBrokerImpl implements MessageBroker {
	private ConcurrentHashMap <Class<? extends Broadcast>,ConcurrentLinkedQueue<Subscriber>> broadcastsToSubs;
	private ConcurrentHashMap <Class<? extends Event>,ConcurrentLinkedQueue<Subscriber>> eventsToSubs;
	private ConcurrentHashMap <Subscriber, LinkedBlockingQueue<Message>> messageQueues;
	private ConcurrentHashMap <Event,Future> results;
	private int mone=0;
	private  Object subscribe_lock = new Object();
	//Thread Safe singleton:
	private static class MessageBrokerImplHolder{private static final MessageBrokerImpl instance=new MessageBrokerImpl();}
	/**
	 * Retrieves the single instance of this class.
	 */
	private MessageBrokerImpl (){
		this.broadcastsToSubs=new ConcurrentHashMap<>();
		this.eventsToSubs=new ConcurrentHashMap<>();
		this.messageQueues=new ConcurrentHashMap<>();
		this.results=new ConcurrentHashMap<>();
	}
	public static MessageBroker getInstance() {
		return MessageBrokerImplHolder.instance;
	}

	@Override
	//Add Subscriber to required type of event
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		//Known type
		synchronized (subscribe_lock){
		if (eventsToSubs.containsKey(type)){
			if (!eventsToSubs.get(type).contains(m)){
				eventsToSubs.get(type).add(m);
			}
		}
		//Not known
		else{
				eventsToSubs.put(type,new ConcurrentLinkedQueue<>());
				eventsToSubs.get(type).add(m);
			}
		}
	}
	public void printTickSubscribers()
	{

	}
	//Same for broadcast
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (subscribe_lock){
			if (broadcastsToSubs.containsKey(type)){
				if (!broadcastsToSubs.get(type).contains(m)){
					broadcastsToSubs.get(type).add(m);
				}
			}
			//Not known
			else{
				broadcastsToSubs.put(type,new ConcurrentLinkedQueue<>());
				broadcastsToSubs.get(type).add(m);
			}
		}
	}
	//Set future obj. for event
	@Override
	public <T> void complete(Event<T> e, T result) {
		results.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		boolean flag=(broadcastsToSubs.get(b.getClass())==null);
//		System.out.println ("Target #2 is null :"+flag);
		synchronized (broadcastsToSubs.get(b.getClass())){//what if null??No cases?
			if (broadcastsToSubs.containsKey(b.getClass())){
				//duplicate queue of Subs of that type of broadcast
				ConcurrentLinkedQueue<Subscriber>q=new ConcurrentLinkedQueue<>(broadcastsToSubs.get(b.getClass()));
				while (!q.isEmpty()){
					Subscriber cur=broadcastsToSubs.get(b.getClass()).poll();
					if (messageQueues.containsKey(cur)){
						messageQueues.get(cur).add(b);
					}
					broadcastsToSubs.get(b.getClass()).add(cur);
					q.remove();
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future f=new Future<>();
		//boolean flag=eventsToSubs.get(e.getClass())==null;
//		System.out.println("first target is null: "+flag);
		synchronized (eventsToSubs.get(e.getClass())){
			if (eventsToSubs.containsKey(e.getClass())){
				Subscriber head=eventsToSubs.get(e.getClass()).poll();
				if (head!=null){
					results.putIfAbsent(e,f);
					messageQueues.get(head).add(e);
					eventsToSubs.get(e.getClass()).add(head);//Round Robin
				}
				else {return null;}
			}
			return f;
		}
	}

	@Override
	public void register(Subscriber m) {
		messageQueues.putIfAbsent(m,new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(Subscriber m) {
		//Part 1:deleting Futures
		for (Message msg:messageQueues.get(m)){
			if (msg!=null){
				if (results.get(msg)!=null){
					results.get(msg).resolve(null);
				}
			}
		}
		//Part 2:deleting subscriber from Events&Broadcasts
		Iterator<Class <? extends Event>> iter=eventsToSubs.keySet().iterator();
		while (iter.hasNext()){
			Class <? extends Event>cur=iter.next();
			if (eventsToSubs.get(cur).contains(m)){eventsToSubs.get(cur).remove(m);}
		}
		Iterator<Class <? extends Broadcast>> iter1=broadcastsToSubs.keySet().iterator();
		while (iter1.hasNext()){
			Class <? extends Broadcast>cur=iter1.next();
			if (broadcastsToSubs.get(cur).contains(m)){broadcastsToSubs.get(cur).remove(m);}
		}
		//Part 3:remove m from msgQues
		if (messageQueues.containsKey(m)){messageQueues.remove(m);}
	}
	@Override
	public Message awaitMessage(Subscriber m) throws InterruptedException {
		return messageQueues.get(m).take();
	}



}
