package Esercizio2Modificato;

import Esercizio2Modificato.SharedState;

interface IState {
	void increment();

	int getValue();
}

final class Holder
{
	private final int value;
	
	public Holder(int value)
	{
		this.value=value;
	}
	
	public int getValue()
	{
		return value;
	}
}

final class SharedState implements IState 
{
	private Holder holder;
	
	public SharedState(int value) 
	{
		holder=new Holder(value);
	}

	@Override
	public synchronized void increment() 
	{
		holder=new Holder(holder.getValue()+1);
	}

	@Override
	public synchronized int getValue() 
	{
		return holder.getValue();
	}
}

class Helper implements Runnable {
	@Override
	public void run() {
		System.out.println("Helper : started and waiting until shared state is set!");
		while (true) {
			if (Esercizio2Modificato.sharedState != null)
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

		for (int i = 0; i < 5000; i++) {
			Esercizio2Modificato.sharedState.increment();
			if ((i % 100) == 0)
				try {
					Thread.sleep(1);
				} catch (final InterruptedException e) {
				}
		}
		System.out.println("Helper : completed");
	}
}

class Starter implements Runnable {

	@Override
	public void run() {
		System.out.println("Starter : sleeping");
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		System.out.println("Starter : initialized shared state");
		// Choose which share to instantiate
		
		Esercizio2Modificato.sharedState = new SharedState(0);

		// Sleep before updating
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		// Perform 5000 increments and exit
		System.out.println("Starter : begin incrementing");
		for (int i = 0; i < 5000; i++) {
			Esercizio2Modificato.sharedState.increment();
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
	public static final boolean THREADSAFE_SHARE = true;

	static volatile IState sharedState = null; //volatile

	public static void main(final String[] args) {
		System.out.println("Esercizio2 modificato");
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
