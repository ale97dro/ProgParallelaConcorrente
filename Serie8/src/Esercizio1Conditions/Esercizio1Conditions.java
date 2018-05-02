package Esercizio1Conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Sommatore implements Runnable
{
	private final int id;
	private final Integer[] riga;
	private final Integer[] colonna;
	
	public Sommatore(final int id, final int[] riga, final Integer[] colonna2)
	{
		this.id=id;
		
		this.riga=new Integer[riga.length];
		this.colonna=new Integer[colonna2.length];
		
		for(int i=0;i<riga.length;i++)
			this.riga[i]=riga[i];
		
		for(int i=0;i<colonna2.length;i++)
			this.colonna[i]=colonna2[i];
	}
	
	@Override
	public void run() 
	{
		int somma=0;
		
		for(int i=0;i<riga.length;i++)
			somma+=riga[i];
		Esercizio1Conditions.inserisciSommaRiga(somma, id);
		
		somma=0;
		
		for(int i=0;i<colonna.length;i++)
			somma+=colonna[i];
		Esercizio1Conditions.inserisciSommaColonna(somma, id);
	}
	
}


public class Esercizio1Conditions {
	private final static int[][] matrix = new int[10][10];
	private final static int[] rowSum = new int[matrix.length];
	private final static int[] colSum = new int[matrix[0].length];
	private static int counter=0;
	private final static Lock lock=new ReentrantLock();
	private final static Condition sommaFinita=lock.newCondition();

	public static void inserisciSommaRiga(int somma, int index)
	{
		lock.lock();
		try {
			rowSum[index]=somma;
			counter++;
			sommaFinita.signal();
		}
		finally { lock.unlock(); }
	}
	
	public static void inserisciSommaColonna(int somma, int index)
	{
		lock.lock();
		try
		{
			colSum[index]=somma;
			counter++;
			sommaFinita.signal();
		}
		finally { lock.unlock(); }
	}
	
	
	public static void main(String[] args) 
	{
		System.out.println("Esercizio1Conditions");
		List<Thread>threads=new ArrayList<>();
		int somma_righe=0, somma_colonne=0;
		
		initMatrix();
		
		for(int i=0;i<10;i++)
		{
			Integer[] colonna=new Integer[10];
			
			for(int k=0;k<10;k++)
			{
				colonna[k]=matrix[k][i];
			}
			threads.add(new Thread(new Sommatore(i, matrix[i], colonna)));
		}
		
		for(Thread t : threads)
			t.start();
		
		lock.lock();
		try
		{
			while (counter < 10) 
			{
					sommaFinita.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		
		
		for(int i=0;i<10;i++)
			somma_righe+=rowSum[i];
	//	counter=0;
		
		/*
		lock.lock();
		try
		{
			while (counter < 20) 
			{
					sommaFinita.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally
		{
			lock.unlock();
		}
		*/
		
		for(Thread t : threads)
			try {
				t.join();
			} catch (InterruptedException e) 
		{
				e.printStackTrace();
			}
		
		for(int i=0;i<10;i++)
			somma_colonne+=colSum[i];
		
		System.out.println("Somma righe: "+somma_righe);
		System.out.println("Somma colonne: "+somma_colonne);
	}

	private static void initMatrix() {
		Random r = new Random();
		for (int row = 0; row < matrix.length; row++) {
			for (int col = 0; col < matrix[row].length; col++) {
				matrix[row][col] = 1 + r.nextInt(100);
			}
		}
	}
}
