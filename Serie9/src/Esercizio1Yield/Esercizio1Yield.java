package Esercizio1Yield;

import java.util.concurrent.CountDownLatch;

public class Esercizio1Yield extends Thread {
	static final CountDownLatch cdl = new CountDownLatch(2);
	static volatile boolean finished = false;
	static volatile int sum = 0;
	static volatile int cnt = 0;

	public static void main(final String[] args) 
	{
		System.out.println("Esercizio1Yield");
		final Thread thread1 = new Thread(() -> {
			cdl.countDown();
			try {
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			int count = 0;
			while (!Esercizio1Yield.finished) {
				Esercizio1Yield.cnt = ++count;
				System.out.println("sum " + Esercizio1Yield.sum);
			}
		});

		final Thread thread2 = new Thread(() -> {
			cdl.countDown();
			try {
				cdl.await();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 1; i <= 50_000; i++) 
			{
				Esercizio1Yield.sum = i;
				Thread.yield(); //ad ogni calcolo, fermo thread2 e do la possibilità a thread1 di stampare il risultato
			}
			Esercizio1Yield.finished = true;
			System.out.println("cnt " + Esercizio1Yield.cnt);
		});

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
