package Esercizio2Modificato;

import Esercizio2Modificato.Esercizio2Modificato;
import Esercizio2Modificato.Helper;
import Esercizio2Modificato.IState;
import Esercizio2Modificato.Starter;


interface IState {
	IState increment();

	int getValue();
}

final class SharedState implements IState 
{
	private final int value;

	public SharedState(int value)
	{
		this.value=value;
	}
	
	@Override
	public SharedState increment() 
	{
		return new SharedState(value+1);
	}

	@Override
	public int getValue() 
	{
		return value;
	}
}


class Helper implements Runnable {
	@Override
	public void run() { //thread di sola lettura
		
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
		
		//Qui? Bho!
		for (int i = 0; i < 5000; i++) {
			Esercizio2Modificato.sharedState=Esercizio2Modificato.sharedState.increment();
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
	public void run() { //Unico thread che può scrivere
		System.out.println("Starter : sleeping");
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		System.out.println("Starter : initialized shared state");
		
		Esercizio2Modificato.sharedState=new SharedState(0);
		
		// Sleep before updating
		try {
			Thread.sleep(1000);
		} catch (final InterruptedException e) {
		}

		// Perform 5000 increments and exit
		System.out.println("Starter : begin incrementing");
		for (int i = 0; i < 5000; i++) {
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
