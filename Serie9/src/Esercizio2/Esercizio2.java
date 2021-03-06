package Esercizio2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

class Depot {
	final private int id;
	private final List<String> elements = new ArrayList<>(); //pezzi disponibili
	
	public Depot(final int id) {
		this.id = id;
		for (int i = 0; i < 1000; i++)
			elements.add(new String("Dep#" + id + "_item#" + i));
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int getStockSize() {
		return elements.size();
	}

	public String getElement() {
		return elements.remove(0);
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Depot" + id;
	}
}

class AssemblingWorker implements Runnable {
	private final int id;
	public AssemblingWorker(final int id) {
		this.id = id;
	}

	@Override
	public void run() {
		final Random random = new Random();
		int failureCounter = 0;
		while (true) 
		{
			// Choose randomly 3 different suppliers
			final List<Depot> depots = new ArrayList<>();
			while (depots.size() != 3) {
				final Depot randomDepot = Esercizio2.suppliers[random.nextInt(Esercizio2.suppliers.length)];
				if (!depots.contains(randomDepot)) //se tra i depositi scelti, c'� gi� l'ultimo radom estratto
					depots.add(randomDepot);
			}

			//ordino i depot acquisiti per garantire la stessa logica di accesso
			Collections.sort(depots, new Comparator<Depot>()
					{
						@Override
						public int compare(Depot arg0, Depot arg1) {
							return arg0.getId()-arg1.getId();
						}
				
					});
			
			
			//estraggo i depositi random dalla lista
			final Depot supplier1 = depots.get(0);
			final Depot supplier2 = depots.get(1);
			final Depot supplier3 = depots.get(2);

			//problema di ordine dei lock?
			log("assembling from : " + supplier1 + ", " + supplier2 + ", " + supplier3);
			synchronized (supplier1) 
			{
				if (supplier1.isEmpty()) 
				{
					log("not all suppliers have stock available!");
					failureCounter++;
				} 
				else 
				{
					synchronized (supplier2) 
					{ 
						if (supplier2.isEmpty()) 
						{
							log("not all suppliers have stock available!");
							failureCounter++;
						} 
						else 
						{
							synchronized (supplier3)
							{
								if (supplier3.isEmpty())
								{
									log("not all suppliers have stock available!");
									failureCounter++;
								} 
								else 
								{
									final String element1 = supplier1.getElement();
									final String element2 = supplier2.getElement();
									final String element3 = supplier3.getElement();
									log("assembled product from parts:  " + element1 + ", " + element2 + ", "
											+ element3);
								}
							}
						}
					}
				}
			}
			
			if (failureCounter > 1000) 
			{
				log("Finishing after " + failureCounter + " failures");
				break;
			}
		}
	}

	private final void log(final String msg) {
		System.out.println("AssemblingWorker" + id + ": " + msg);
	}
}

public class Esercizio2 {
	final static Depot[] suppliers = new Depot[10]; //depositi dei fornitori

	public static void main(final String[] args) {
		System.out.println("Esercizio2");
		for (int i = 0; i < 10; i++)
			suppliers[i] = new Depot(i);

		final List<Thread> allThreads = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			allThreads.add(new Thread(new AssemblingWorker(i)));
		}

		System.out.println("Simulation started");
		for (final Thread t : allThreads) {
			t.start();
		}

		for (final Thread t : allThreads) {
			try {
				t.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Simulation finished");
	}
}
