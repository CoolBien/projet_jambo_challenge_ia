package jumbo.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ThreadPool {

	private final Thread[] threadPool;
	private final AtomicInteger nextWorkId;

	private boolean started;

	public <T> ThreadPool(final int nbThread, final T[] array, final Consumer<T> task) {
		this(nbThread, array.length, i -> task.accept(array[i]));
	}

	public <T> ThreadPool(final int nbThread, final List<T> list, final Consumer<T> task) {
		this(nbThread, list.size(), i -> task.accept(list.get(i)));
	}

	public ThreadPool(final int nbThread, final int nbTasks, final Consumer<Integer> task) {
		threadPool = new Thread[nbThread];
		nextWorkId = new AtomicInteger();
		for (int i=0; i<nbThread; i++) {
			threadPool[i] = new Thread(() -> {
				int id;
				while ((id = nextWorkId.getAndIncrement()) < nbTasks) {
					task.accept(id);
				}
			}, "ThreadPool-"+i);
		}
	}

	public synchronized void startAndWait() throws InterruptedException {
		start();
		await();
	}

	public synchronized void start() {
		if (started) {
			throw new IllegalStateException("Already started");
		}
		started = true;
		for (final Thread t: threadPool) {
			t.start();
		}
	}

	public synchronized void await() throws InterruptedException {
		if (!started) {
			throw new IllegalStateException("Not started");
		}
		for (final Thread t: threadPool) {
			t.join();
		}
	}
}
