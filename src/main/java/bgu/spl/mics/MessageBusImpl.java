package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private Map<Event,Future> allFutures;
	private Map<MicroService,ArrayList<Message>> msQueues;
	private Map<Class<? extends Event>,ArrayList<MicroService>> eventSubscribers;
	private Map<Class<? extends Broadcast>,ArrayList<MicroService>> broadcastSubscribers;
	private Object eventSubscribersLocker,broadcastSubscribersLocker,msQueuesLocker,futuresLocker;

	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	//constructor
	private MessageBusImpl(){
		allFutures=new HashMap<>();
		msQueues=new HashMap<>();
		eventSubscribers = new HashMap<>();
		broadcastSubscribers = new HashMap<>();
		eventSubscribersLocker=new Object();
		broadcastSubscribersLocker=new Object();
		msQueuesLocker=new Object();
		futuresLocker=new Object();
	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	@Override
	public   <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (eventSubscribersLocker) {
			if (!eventSubscribers.containsKey(type))
				eventSubscribers.put(type, new ArrayList<>());
			eventSubscribers.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (broadcastSubscribersLocker) {
			if (!broadcastSubscribers.containsKey(type))
				broadcastSubscribers.put(type, new ArrayList<>());
			broadcastSubscribers.get(type).add(m);
		}
    }

	@Override @SuppressWarnings("unchecked")
	public synchronized  <T> void complete(Event<T> e, T result) {
		synchronized (futuresLocker) {
			Future<T> relevantFuture = allFutures.get(e);
			relevantFuture.resolve(result);
		}
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		List<MicroService> relevantMS;
		synchronized (broadcastSubscribersLocker) {
			if (broadcastSubscribers.containsKey(b.getClass()) && !broadcastSubscribers.get(b.getClass()).isEmpty()) {
				relevantMS = broadcastSubscribers.get(b.getClass());
				synchronized (msQueuesLocker) {
					for (MicroService m : relevantMS) {
						msQueues.get(m).add(b);
					}
					msQueuesLocker.notifyAll();
				}
			}
		}
	}

	/**
	 * the function checks which  ms is registered to the relevant event type,
	 * pulls the first ms from the queue.
	 * pass the first ms in the queue to the end
	 * adds e to the ms's queue
	 * @param e     	The event to add to the queue.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> newFuture=new Future<>();
		synchronized (eventSubscribersLocker) {
			if ((!eventSubscribers.containsKey(e.getClass())) || (eventSubscribers.get(e.getClass()).isEmpty()))
				return null;
			List<MicroService> eventMsQueue=eventSubscribers.get(e.getClass());
			MicroService m=eventMsQueue.get(0);
			synchronized (msQueuesLocker) {
				msQueues.get(m).add(e);
				msQueuesLocker.notifyAll();

				synchronized (futuresLocker) {
					allFutures.put(e, newFuture);
				}
			}
			eventSubscribers.get(e.getClass()).remove(0);
			eventSubscribers.get(e.getClass()).add(m);
		}
		return newFuture;
	}

	@Override
	public void register(MicroService m) {
		synchronized (msQueuesLocker) {
			msQueues.put(m, new ArrayList<>());
		}
	}

	@Override
	public void unregister(MicroService m) {
		synchronized (eventSubscribersLocker) {
			for (List l : eventSubscribers.values())
				l.remove(m);
		}
		synchronized (broadcastSubscribersLocker) {
			for (List l : broadcastSubscribers.values())
				l.remove(m);
		}
		synchronized (msQueuesLocker) {
			msQueues.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (msQueuesLocker) {
			while (msQueues.get(m).isEmpty()) {
				msQueuesLocker.wait();
			}
			return msQueues.get(m).remove(0);
		}
	}
}
