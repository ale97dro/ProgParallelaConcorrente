package Esericio1Priorita;

import java.util.concurrent.CountDownLatch;

public class Esercizio1Priorita extends Thread {
	static final CountDownLatch cdl = new CountDownLatch(2);
	static volatile boolean finished = false;
	static volatile int sum = 0;
	static volatile int cnt = 0;

	public static void main(final String[] args) {
		System.out.println("Esercizio1Priorita");
		final Thread thread1 = new Thread(() -> {
			cdl.countDown();
			try {
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			int count = 0;
			while (!Esercizio1Priorita.finished) {
				Esercizio1Priorita.cnt = ++count;
				System.out.println("sum " + Esercizio1Priorita.sum);
			}
		});

		final Thread thread2 = new Thread(() -> {
			cdl.countDown();
			try {
				
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 1; i <= 50000; i++) {
				Esercizio1Priorita.sum = i;
			}
			Esercizio1Priorita.finished = true;
			System.out.println("cnt " + Esercizio1Priorita.cnt);
		});
		
		thread1.setPriority(Thread.MAX_PRIORITY); //massima priorità
		thread2.setPriority(Thread.MIN_PRIORITY); //minima priorità (uso "intensivo" della cpu)

		thread1.start();
		thread2.start();

		try {
			thread1.join();
			thread2.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
