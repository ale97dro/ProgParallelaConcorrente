package Esercizio3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Amico implements Runnable
{
	private static Lock lock=new ReentrantLock();
	
	private final int id;
	private Amico amico;
	private final List<String> casella_posta;
	private int counter_lettere;
	private int counter_risposte;
	
	public Amico(int id)
	{
		this.id=id;
		counter_risposte=0;
		casella_posta=new ArrayList<String>();
	}
	
	@Override
	public void run()
	{
		System.out.println("Thread "+id);
		
		int counter_lettere=ThreadLocalRandom.current().nextInt(2, 6);
		
		for(int i=0;i<counter_lettere;i++)
			amico.aggiungiLettera("Messaggio "+i);
		
		while (counter_risposte < 150) 
		{
			lock.lock();
			try {
				if (isCasellaVuota())
					continue;
			} finally {
				lock.unlock();
			}
			lock.lock();
			try {
				System.out.println(id + " ricevuto messaggio " + casella_posta.get(0));
			} finally {
				lock.unlock();
			}

			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(5, 51));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			amico.aggiungiLettera("Messaggio " + counter_lettere + " " + counter_risposte);
			counter_lettere++;
			counter_risposte++;

			lock.lock();
			try {
				rimuoviLettera();
			} finally {
				lock.unlock();
			}

		}

	}
	
	public int getId()
	{
		return id;
	}
	
	public int getCounterLettere()
	{
		return counter_lettere;
	}
	
	public void aggiungiLettera(String lettera)
	{
		casella_posta.add(lettera);
	}
	
	public void aggiungiAmico(Amico amico)
	{
		this.amico=amico;
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

public class Esercizio3 
{
	public static void main(String[] args)
	{
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
