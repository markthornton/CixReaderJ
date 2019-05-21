package uk.me.mthornton.utility;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** publish events to subscribers.
 *  Note that Swing subscribers can use an executor that runs on the Event thread. Queue subscribers
 *  can retrieve all events that are available in bulk.
 * @param <T> class event provided
 */
public class EventPublisher<T> {
    private CopyOnWriteArrayList<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    private Supplier<BlockingQueue<T>> queueFactory;

    public EventPublisher(Supplier<BlockingQueue<T>> queueFactory) {
        this.queueFactory = queueFactory;
    }

    public EventPublisher() {
        this(LinkedBlockingQueue::new);
    }

    public static <T> Consumer<BlockingQueue<T>> newQueueSubscriber(Consumer<? super T> subscriber) {
        return ts -> ts.forEach(subscriber);
    }

    public void publish(T event) {
        subscribers.forEach(subscriber -> subscriber.add(event));
    }

    public void addSubscriber(Consumer<? super T> subscriber, Executor executor) {
        addQueueSubscriber(newQueueSubscriber(subscriber), executor);
    }

    public void addSubscriber(Consumer<? super T> subscriber, Executor executor, Predicate<T> filter) {
        addQueueSubscriber(newQueueSubscriber(subscriber), executor, filter);
    }

    public void addQueueSubscriber(Consumer<BlockingQueue<T>> subscriber, Executor executor) {
        addQueueSubscriber(subscriber, executor, null);
    }

    public void addQueueSubscriber(Consumer<BlockingQueue<T>> subscriber, Executor executor, Predicate<T> filter) {
        subscribers.add(new Subscriber(subscriber, executor, filter, queueFactory.get()));
    }

    public void removeQueueSubscriber(Consumer<BlockingQueue<T>> subscriber) {
        subscribers.removeIf(s -> s.subscriber == subscriber);
    }

    private class Subscriber implements Runnable {
        private final Consumer<BlockingQueue<T>> subscriber;
        private final Executor executor;
        private final BlockingQueue<T> queue;
        private final Predicate<T> filter;
        private final AtomicBoolean submitted = new AtomicBoolean();

        public Subscriber(Consumer<BlockingQueue<T>> subscriber, Executor executor, Predicate<T> filter, BlockingQueue<T> queue) {
            this.subscriber = subscriber;
            this.executor = executor;
            this.queue = queue;
            this.filter = filter;
        }

        public void add(T event) {
            if (filter == null || filter.test(event)) {
                queue.add(event);
                if (!submitted.getAndSet(true)) {
                    executor.execute(this);
                }
            }
        }

        @Override
        public void run() {
            submitted.set(false);
            if (!queue.isEmpty()) {
                subscriber.accept(queue);
            }
        }
    }

}
