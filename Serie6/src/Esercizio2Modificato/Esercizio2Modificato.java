package Esercizio2Modificato;

import Esercizio2Modificato.Esercizio2Modificato;
import Esercizio2Modificato.Helper;
import Esercizio2Modificato.IState;
import Esercizio2Modificato.SharedState;
import Esercizio2Modificato.Starter;
import Esercizio2Modificato.ThreadSafeSharedState;

interface IState {
	IState increment();

	int getValue();
}

final class SharedState implements IState {
	private final int value;
	
	public SharedState(int value)
	{
		this.value=value;
	}

	@Override
	public synchronized IState increment() {
		return new SharedState(value+1);
	}

	@Override
	public synchronized int getValue() {
		return value;
	}
}

final class ThreadSafeSharedState implements IState {
	private final int value;
	
	public ThreadSafeSharedState(int value) {
		this.value=value;
	}

	@Override
	public synchronized IState increment() {
		return new ThreadSafeSharedState(value+1);
	}

	@Override
	public synchronized int getValue() {
		return value;
	}
}

class Helper implements Runnable {
	@Override
	public void run() { //thread di sola lettura
		
		System.out.println("Helper : started and waiting until shared state is set!");
		while (true) {
			if (Esercizio2Modificato.sharedState != null) //evitare check then act
				break;
		}

		int lastValue = Esercizio2Modificato.sharedState.getValue();

		System.out.println("Helper : shared state initialized and current value is " + lastValue
				+ ". Waiting until value changes");

		// Wait until value changes
		while (true) {
			final int curValue = Esercizio2Modificato.sharedState.getValue();
			if (lastValue != curValue) {
				lastValue = curValue;
				break;
			}
		}
		System.out.println("Helper : value changed to " + lastValue + "!");

		System.out.println("Helper : completed");
	}
}

class Starter implements Runnable {

	@Override
	public void run() { //Unico thread che pu� scrivere
		System.out.println("Starter : sleeping");
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		System.out.println("Starter : initialized shared state");
		
		// Choose which share to instantiate
		if (Esercizio2Modificato.THREADSAFE_SHARE)
			Esercizio2Modificato.sharedState = new ThreadSafeSharedState(0);
		else
			Esercizio2Modificato.sharedState = new SharedState(0);

		// Sleep before updating
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		// Perform 10000 increments and exit
		System.out.println("Starter : begin incrementing");
		for (int i = 0; i < 10000; i++) {
			Esercizio2Modificato.sharedState=Esercizio2Modificato.sharedState.increment();
			if ((i % 100) == 0)
				try {
					Thread.sleep(1);
				} catch (final InterruptedException e) {
				}
		}
		System.out.println("Starter : completed");
	}
}

public class Esercizio2Modificato {
	public static final boolean THREADSAFE_SHARE = false;

	static volatile IState sharedState = null; //volatile

	public static void main(final String[] args) {
		System.out.println("Esercizio 2 modificato");
		// Create Threads
		final Thread readThread = new Thread(new Helper());
		final Thread updateThread = new Thread(new Starter());

		// Start Threads
		readThread.start();
		updateThread.start();

		// Wait until threads finish
		try {
			updateThread.join();
			readThread.join();
		} catch (final InterruptedException e) {

		}
		System.out.println("Main: final value " + Esercizio2Modificato.sharedState.getValue());
	}
}