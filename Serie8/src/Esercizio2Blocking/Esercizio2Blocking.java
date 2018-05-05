package Esercizio2Blocking;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

class Amico implements Runnable
{
	private final int id;
	private Amico amico;
	private final LinkedBlockingQueue<String>casella_posta;
	
	public Amico(int id)
	{
		this.id=id;
		casella_posta=new LinkedBlockingQueue<String>();
	}
	
	@Override
	public void run()
	{
		System.out.println("Thread "+id);
		
		int counter_risposte=0;
		int counter_lettere=ThreadLocalRandom.current().nextInt(2, 6);
		
		for(int i=0;i<counter_lettere;i++)
			amico.spedisciLettera("Messaggio "+i);
		
		while (counter_risposte < 150) 
		{
			try {
				System.out.println("Utente " + id + " ricevuto messaggio " + casella_posta.take());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(5, 51));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			amico.spedisciLettera("Messaggio " + counter_lettere);
			counter_lettere++;
			counter_risposte++;
		}
	}
	
	public  void spedisciLettera(String lettera)
	{
		casella_posta.add(lettera);
	}
	
	public void aggiungiAmico(Amico amico)
	{
		this.amico=amico;
	}
}

public class Esercizio2Blocking 
{
	public static void main(String[] args)
	{
		System.out.println("Esercizio 2 Blocking");
		
		List<Thread>threads=new ArrayList<Thread>();
		List<Amico>amici=new ArrayList<Amico>();
	
		for(int i=0;i<2;i++)
		{
			Amico a=new Amico(i);
			amici.add(a);
			threads.add(new Thread(a));
		}
		
		amici.get(0).aggiungiAmico(amici.get(1));
		amici.get(1).aggiungiAmico(amici.get(0));
		
		threads.forEach(Thread::start);
		
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
