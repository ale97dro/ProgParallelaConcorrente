package Esercizio4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Cliente implements Runnable
{
	private final int id;
	private final Barbiere barbiere;
	
	public Cliente(int id, Barbiere barbiere)
	{
		this.id=id;
		this.barbiere=barbiere;
	}
	
	@Override
	public void run() 
	{
		if(barbiere.isBarbiereDormiente())
		{
			barbiere.svegliaBarbiere(id);
			System.out.println("Cliente "+id+" sveglio il barbiere");
			barbiere.aggiungimiAllaSalaAttesa(this);
		}
		else
			barbiere.aggiungimiAllaSalaAttesa(this);
	}
	
	public int getId()
	{
		return id;
	}
	
	@Override
	public String toString()
	{
		return "Cliente "+id;
	}
}


class Barbiere implements Runnable
{
	private final BlockingQueue<Cliente> salaAttesa;
	private AtomicBoolean sto_dormendo;
	private Lock lock=new ReentrantLock();
	private Condition dormo = lock.newCondition();
	
	public Barbiere()
	{
		salaAttesa=new LinkedBlockingQueue<Cliente>();
		sto_dormendo=new AtomicBoolean(true);
	}

	@Override
	public void run() 
	{
		while(true)
		{
			if(controllaSalaAttesa())
				dormo();
			tagliaCapelli();
		}
	}
	
	//private synchronized boolean controllaSalaAttesa()
	private boolean controllaSalaAttesa()
	{
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(50, 101));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return salaAttesa.isEmpty();
	}
	
	private void dormo()
	{
		lock.lock();
		System.out.println("Barbiere: non c'è nessuno, quindi dormo");
		try
		{
			sto_dormendo.set(true);
			while(sto_dormendo.get())
				dormo.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		
	}
	
	private void tagliaCapelli()
	{
		try 
		{
			System.out.println("Barbiere: taglio capelli al cliente "+salaAttesa.take());
			Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void svegliaBarbiere(int id_cliente)
	{
		lock.lock();
		
		try
		{
			sto_dormendo.set(false);
			dormo.signalAll();
		}
		finally
		{
			lock.unlock();
		}
	}
	
	public boolean isBarbiereDormiente()
	{
		return sto_dormendo.get();
	}
	
	public boolean aggiungimiAllaSalaAttesa(Cliente c)
	{
		if (salaAttesa.size() < 10) 
		{
			salaAttesa.add(c);
			System.out.println("Cliente " + c.getId() + " mi siedo, siamo in "+salaAttesa.size());
			return true;
		} 
		else 
		{
			System.out.println("Cliente " + c.getId() + " me ne vado");
			return false;
		}
	}
}



public class Esercizio4 
{
	public static void main(String[] args)
	{
		Barbiere barbiere = new Barbiere();
		
		Thread barbiereThread = new Thread(barbiere);
		
		barbiereThread.start();
		int conta_clienti=0;
		
		while(true)
		{
			Cliente c = new Cliente(conta_clienti, barbiere);
			Thread threadCliente = new Thread(c);
			threadCliente.start();
			conta_clienti++;
			
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(450, 701));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
