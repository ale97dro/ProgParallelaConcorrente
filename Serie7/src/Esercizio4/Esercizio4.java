package Esercizio4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

enum Giorno {
	LUNEDI, MARTEDI, MERCOLEDI, GIOVEDI, VENERDI, SABATO, DOMENICA;
}

class RemoveWorker implements Runnable {
	private final int id;

	public RemoveWorker(int id) {
		this.id = id;
	}

	@Override
	public void run() 
	{
		while (true) 
		{
			Giorno giorno = Giorno.values()[ThreadLocalRandom.current().nextInt(7)];
			int counter = 0;

			String oldString;
			String newString = null;
			
			do 
			{
				oldString = Esercizio4.mappa.get(giorno);
				if (oldString.length() > 0)
					newString = oldString.substring(0, (oldString.length() - 1));
				else
					break;
				counter++;
			} while (Esercizio4.mappa.replace(giorno, oldString, newString));

			if (counter > 1)
				System.out.println("RemoveWorker" + id + ":  Updated " + giorno.toString() + " after " + counter + " tries");
			counter = 0;
			if(oldString.length()<1)
				break;
		}
	}
}

public class Esercizio4 {
	public static ConcurrentHashMap<Giorno, String> mappa = new ConcurrentHashMap<>();

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) {
		System.out.println("Esercizio 4");

		String alfabeto = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		for (int i = 0; i < 7; i++) {
			StringBuilder sBuilder = new StringBuilder();

			for (int c = 0; c < 10_000; c++)
				sBuilder.append(alfabeto.charAt(ThreadLocalRandom.current().nextInt(alfabeto.length())));

			mappa.put(Giorno.values()[i], sBuilder.toString());
		}
		
		//togliere
		@SuppressWarnings("unused")
		ConcurrentHashMap mappa3=mappa;
		
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 30; i++)
			threads.add(new Thread(new RemoveWorker(i)));

		threads.forEach(Thread::start);

		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		//togliere
		@SuppressWarnings("unused")
		ConcurrentHashMap mappa2=mappa;
		
		for(int i=0;i<7;i++)
			System.out.println(mappa.get(Giorno.values()[i]));

		System.out.println("FINE");
	}
}
