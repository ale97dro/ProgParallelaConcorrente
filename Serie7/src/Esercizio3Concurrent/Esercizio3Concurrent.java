package Esercizio3Concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Amico implements Runnable 
{
	private final int id;
	private Amico amico;
	private final CopyOnWriteArrayList<String> casella_posta;

	public Amico(int id) {
		this.id = id;
		casella_posta = new CopyOnWriteArrayList<String>();
	}

	@Override
	public void run() {
		System.out.println("Thread " + id);

		int counter_risposte = 0;
		int counter_lettere = ThreadLocalRandom.current().nextInt(2, 6);

		for (int i = 0; i < counter_lettere; i++)
			amico.spedisciLettera("Messaggio " + i);

		while (counter_risposte < 150) {
			if (isCasellaVuota())
				continue;
			System.out.println("Utente " + id + " ricevuto messaggio " + casella_posta.get(0));

			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(5, 51));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			amico.spedisciLettera("Messaggio " + counter_lettere);
			counter_lettere++;
			counter_risposte++;

			rimuoviLettera();
		}
	}

	public void spedisciLettera(String lettera) 
	{
		casella_posta.add(lettera);
	}

	public void aggiungiAmico(Amico amico) 
	{
		this.amico = amico;
	}

	private boolean isCasellaVuota() 
	{
		return casella_posta.isEmpty();
	}

	private void rimuoviLettera() 
	{
		casella_posta.remove(0);
	}
}

public class Esercizio3Concurrent {
	public static void main(String[] args) {
		List<Thread> threads = new ArrayList<Thread>();
		List<Amico> amici = new ArrayList<Amico>();

		for (int i = 0; i < 2; i++) {
			Amico a = new Amico(i);
			amici.add(a);
			threads.add(new Thread(a));
		}

		amici.get(0).aggiungiAmico(amici.get(1));
		amici.get(1).aggiungiAmico(amici.get(0));

		threads.forEach(Thread::start);

		for (Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
