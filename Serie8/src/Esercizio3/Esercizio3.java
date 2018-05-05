package Esercizio3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Squadra
{
	private final int numero;
	private final Lock lock;
	private AtomicInteger testimoneInt;
	private CopyOnWriteArrayList<Integer> tempi;
	private Condition testimone;
	
	public Squadra(int numero)
	{
		this.numero=numero;
		lock=new ReentrantLock();
		testimoneInt=new AtomicInteger(0);
		tempi=new CopyOnWriteArrayList<>();
		testimone=lock.newCondition();
	}
	
	public void lock()
	{
		lock.lock();
	}
	
	public void unlock()
	{
		lock.unlock();
	}
	
	public int getNumero()
	{
		return numero;
	}
	
	
	public int getTestimoneInt()
	{
		return testimoneInt.get();
	}
	
	public void passaTestimoneInt()
	{
		testimoneInt.incrementAndGet();
	}
	
	public void passaTestimone()
	{
		lock.lock();
		try
		{
			testimone.signalAll();
		}
		finally {
			lock.unlock();
		}
	}
	
	public void aspettaTestimone()
	{
		lock.lock();
		try
		{
			testimone.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}
	}
	
	public void addTempo(int tempo)
	{
		tempi.add(tempo);
	}
	
	public int sommaTempi()
	{
		int somma=0;
		
		for(Integer t : tempi)
			somma+=t;
		
		return somma;
	}
}

class Corridore implements Runnable
{
	private final int numero;
	private final Squadra squadra;
	
	public Corridore(int numero, Squadra squadra)
	{
		this.squadra=squadra;
		this.numero=numero;
	}
	
	@Override
	public void run() 
	{
		//attendo partenza
		if(numero==0)
			System.out.println("Corridore"+numero+"Squadra"+squadra.getNumero()+": attendo partenza");
		else
			System.out.println("Corridore"+numero+"Squadra"+squadra.getNumero()+": attendo testimone");
		Esercizio3.aspettaPartenza();
		
		//devo aspettare per ricevere il testimone
		while(squadra.getTestimoneInt()!=numero)
			squadra.aspettaTestimone();
		if(numero!=0)
			System.out.println("Corridore"+numero+"Squadra"+squadra.getNumero()+": ricevo testimone da Corridore"+(numero-1)+"Squadra"+squadra.getNumero());
		
		
		int tempo_corsa=ThreadLocalRandom.current().nextInt(100, 151);
		squadra.addTempo(tempo_corsa);
		System.out.println("Corridore"+numero+"Squadra"+squadra.getNumero()+": corro per "+tempo_corsa+" ms");
		
		try {
			Thread.sleep(tempo_corsa);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Corridore"+numero+"Squadra"+squadra.getNumero()+": passo testimone a Corridore"+(numero+1)+"Squadra"+squadra.getNumero());
		squadra.passaTestimoneInt();
		squadra.passaTestimone();
		
		if(numero==9)
		{
			if (Esercizio3.controllaVittoria()) 
			{
				System.out.println("Corridore" + numero + "Squadra" + squadra.getNumero() + ": VITTORIA!");
			} 
			else
				System.out.println("Corridore" + numero + "Squadra" + squadra.getNumero() + ": SCONFITTA!");
		}	
	}
	
}

public class Esercizio3 
{
	private static boolean primo=true;
	private static Lock lock = new ReentrantLock();
	private static Condition via=lock.newCondition();
	private static volatile boolean partenza=false;
	
	public static synchronized boolean controllaVittoria()
	{
		if(primo)
		{
			primo=false;
			return true;
		}
		else
			return false;
	}
	
	public static void aspettaPartenza()
	{
		lock.lock();
		try {
			while(!Esercizio3.getStatoPartenza())
				Esercizio3.via.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public static boolean getStatoPartenza()
	{
		return partenza;
	}
	
	public static void main(String[] args)
	{
		List<Squadra>squadre=new ArrayList<Squadra>();
		List<Thread>threads_giocatori=new ArrayList<>();
		
		for(int i=0;i<4;i++)
		{
			Squadra s = new Squadra(i);
			squadre.add(s);
			
			for(int k=0;k<10;k++)
			{
				Corridore g = new Corridore(k, s);
				threads_giocatori.add(new Thread(g));
			}
		}
		
		for(Thread t : threads_giocatori)
			t.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("\nPronti, Partenza, VIA!!\n");
		partenza=true;
		
		lock.lock();
		try
		{
			via.signalAll();
		}
		finally {
			lock.unlock();
		}
		
		for(Thread t : threads_giocatori)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("\nClassifica finale");
		Collections.sort(squadre, new Comparator<Squadra>() {
				@Override
				public int compare(Squadra arg0, Squadra arg1)
				{
					int tempo1=arg0.sommaTempi();
					int tempo2=arg1.sommaTempi();
					
					return tempo1-tempo2;
				}
		});
		
		for(Squadra s : squadre)
			System.out.println("Tempo Squadra"+s.getNumero()+": "+s.sommaTempi()+" ms");
	}
}
