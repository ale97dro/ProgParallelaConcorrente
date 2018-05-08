package Esercizio4Conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fantino implements Runnable
{
	private final int id;
	
	public Fantino(int id)
	{
		this.id=id;
	}

	@Override
	public void run() 
	{
		long startTime=-1;
		long endTime=-1;
		
		//simulo arrivo
		try {
			Thread.sleep(ThreadLocalRandom.current().nextLong(1000, 1051));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		
		//attesa
		Esercizio4Conditions.incrementaFantini();
		System.out.println("Fantino"+id+" arrivato");
		if(Esercizio4Conditions.getFantini()<10)
			startTime=System.currentTimeMillis();
		Esercizio4Conditions.aspetta();
		endTime=System.currentTimeMillis();
		
		if(startTime!=-1)
			System.out.println("Fantino"+id+": ha atteso "+(endTime-startTime)+"ms ");
		else
			System.out.println("Fantino"+id+": ha atteso 0 ms ");
	}
}

public class Esercizio4Conditions
{
	private static AtomicInteger counterFantini=new AtomicInteger(0);
	private static Lock lock=new ReentrantLock();
	private static Condition attesa=lock.newCondition();
	
	public static void lock()
	{
		lock.lock();
	}
	
	public static void unlock()
	{
		lock.unlock();
	}
	
	public static void aspetta()
	{
		lock.lock();
		try
		{
			attesa.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}
	}
	
	public static void incrementaFantini()
	{
		counterFantini.incrementAndGet();
	}
	
	public static int getFantini()
	{
		return counterFantini.get();
	}
	
	public static void main(String[] args)
	{
		System.out.println("Esercizio4Conditions");
		List<Thread>threads=new ArrayList<Thread>();
		
		for(int i=0;i<10;i++)
			threads.add(new Thread(new Fantino(i)));
		
		for(Thread t : threads)
			t.start();
		
		while(counterFantini.get()<10)
		{
			
		}
		
		lock.lock();
		try 
		{
			attesa.signalAll();
		}
		finally {
			lock.unlock();
		}
		
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
