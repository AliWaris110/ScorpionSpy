package io.socket.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

public class EventThread extends Thread {
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {
        /* class io.socket.thread.EventThread.AnonymousClass1 */

        public Thread newThread(Runnable runnable) {
            EventThread unused = EventThread.thread = new EventThread(runnable);
            EventThread.thread.setName("EventThread");
            EventThread.thread.setDaemon(Thread.currentThread().isDaemon());
            return EventThread.thread;
        }
    };
    private static int counter = 0;
    private static final Logger logger = Logger.getLogger(EventThread.class.getName());
    private static ExecutorService service;
    private static EventThread thread;

    static /* synthetic */ int access$310() {
        int i = counter;
        counter = i - 1;
        return i;
    }

    private EventThread(Runnable runnable) {
        super(runnable);
    }

    public static boolean isCurrent() {
        return currentThread() == thread;
    }

    public static void exec(Runnable runnable) {
        if (isCurrent()) {
            runnable.run();
        } else {
            nextTick(runnable);
        }
    }

    public static void nextTick(final Runnable runnable) {
        ExecutorService executorService;
        synchronized (EventThread.class) {
            counter++;
            if (service == null) {
                service = Executors.newSingleThreadExecutor(THREAD_FACTORY);
            }
            executorService = service;
        }
        executorService.execute(new Runnable() {
            /* class io.socket.thread.EventThread.AnonymousClass2 */

            public void run() {
                try {
                    runnable.run();
                    synchronized (EventThread.class) {
                        EventThread.access$310();
                        if (EventThread.counter == 0) {
                            EventThread.service.shutdown();
                            ExecutorService unused = EventThread.service = null;
                            EventThread unused2 = EventThread.thread = null;
                        }
                    }
                } catch (Throwable th) {
                    synchronized (EventThread.class) {
                        EventThread.access$310();
                        if (EventThread.counter == 0) {
                            EventThread.service.shutdown();
                            ExecutorService unused3 = EventThread.service = null;
                            EventThread unused4 = EventThread.thread = null;
                        }
                        throw th;
                    }
                }
            }
        });
    }
}
