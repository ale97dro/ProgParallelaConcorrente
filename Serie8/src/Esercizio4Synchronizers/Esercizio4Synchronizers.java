package Esercizio4Synchronizers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

class Fantino implements Runnable
{
	private final int id;
	
	public Fantino(int id)
	{
		this.id=id;
		
		Esercizio4Synchronizers.registraThread();
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
		Esercizio4Synchronizers.incrementaFantini();
		System.out.println("Fantino"+id+" arrivato");
		if(Esercizio4Synchronizers.getFantini()<10)
			startTime=System.currentTimeMillis();
		Esercizio4Synchronizers.aspetta();
		endTime=System.currentTimeMillis();
		
		if(startTime!=-1)
			System.out.println("Fantino"+id+": ha atteso "+(endTime-startTime)+"ms ");
		else
			System.out.println("Fantino"+id+": ha atteso 0 ms ");
	}
}

public class Esercizio4Synchronizers
{
	private static AtomicInteger counterFantini=new AtomicInteger(0);
	private static Phaser phaser = new Phaser(1);
	
	public static void registraThread()
	{
		phaser.register();
	}
	
	public static void aspetta()
	{
		phaser.arriveAndAwaitAdvance();
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
		System.out.println("Esercizio4Synchronizers");
		List<Thread>threads=new ArrayList<Thread>();
		
		for(int i=0;i<10;i++)
			threads.add(new Thread(new Fantino(i)));
		
		for(Thread t : threads)
			t.start();
		
		while(counterFantini.get()<10)
		{
			
		}
		
		phaser.arrive();
		
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
}
