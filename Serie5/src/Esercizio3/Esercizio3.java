package Esercizio3;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Sportello implements Runnable
{
	private final int thread;
	
	public Sportello(int thread)
	{
		this.thread=thread;
	}
	@Override
	public void run() 
	{
		while(Esercizio3.getContinua())
		{
			int importo=ThreadLocalRandom.current().nextInt(50,501);
			int valuta1=ThreadLocalRandom.current().nextInt(0,4);
			int valuta2=ThreadLocalRandom.current().nextInt(0,4);
			
			double tasso=1;
			try
			{
				Esercizio3.lock();
				tasso=Esercizio3.getTasso(valuta1, valuta2);
			}
			finally
			{
				Esercizio3.unlock();
			}
			
			double importo_convertito=importo*tasso;
			
			System.out.println("Thread "+thread+" Importo: "+importo+" Valuta1: "+valuta1+" Valuta2: "+valuta2+" ImportoConvertito: "+importo_convertito);

			try {
				Thread.sleep(ThreadLocalRandom.current().nextLong(1,5));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}
}

class TassiDiCambio
{
	public static final int N_VALUTE=5;
	
	private final double[][] tassi=new double[N_VALUTE][N_VALUTE];
	
	public static TassiDiCambio costruisciTassiDiCambio(final double[][] tassi)
	{
		return new TassiDiCambio(tassi);
	}
	
	private TassiDiCambio(final double[][] tassi)
	{
		for(int r=0;r<N_VALUTE;r++)
		{
			for(int c=0;c<N_VALUTE;c++)
			{
				this.tassi[r][c]=tassi[r][c];
			}
		}
	}

	public double getTasso(int valuta1, int valuta2)
	{
		return tassi[valuta1][valuta2];
	}
	
	public void scriviMatrice()
	{
		PrintWriter writer=null;
		try {
			writer = new PrintWriter("tassi.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		for(int r=0;r<N_VALUTE;r++)
		{
			for(int c=0;c<N_VALUTE;c++)
			{
				writer.write(tassi[r][c]+" ");
			}
			writer.println();
		}
		writer.close();
	}
	
	public void stampaMatrice()
	{
		System.out.println("Matrice dei tassi");
		
		for(int r=0;r<N_VALUTE;r++)
		{
			for(int c=0;c<N_VALUTE;c++)
			{
				System.out.print(tassi[r][c]+" ");
			}
			
			System.out.println();
		}
	}
}

public class Esercizio3
{
	private static volatile boolean continua=true;
	private static TassiDiCambio tassi=null;
	private static Lock lock=new ReentrantLock();
	
	public static boolean getContinua()
	{
		return continua;
	}
	
	public static void lock()
	{
		lock.lock();
	}
	
	public static void unlock()
	{
		lock.unlock();
	}
	
	public static double getTasso(int valuta1, int valuta2)
	{
		try
		{
			return tassi.getTasso(valuta1, valuta2);
		}
		catch(Exception ex)
		{
			return 1;
		}
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		System.out.println("Esercizio 3");
		List<Thread>sportelli=new ArrayList<Thread>();
		
		for(int i=0;i<10;i++)
			sportelli.add(new Thread(new Sportello(i)));
		
		for(Thread t : sportelli)
			t.start();
		
		for(int i=0;i<1;i++)
		{
			//Creo nuovi tassi
			double[][] tassi_temp=new double[TassiDiCambio.N_VALUTE][TassiDiCambio.N_VALUTE];
			for(int r=0;r<TassiDiCambio.N_VALUTE;r++)
			{
				for(int c=0;c<TassiDiCambio.N_VALUTE;c++)
				{
					if(r==c)
						tassi_temp[r][c]=1.0;
					else
						tassi_temp[r][c]=ThreadLocalRandom.current().nextDouble()*(1.5-0.5)+0.5;
				}
			}
			
			//Aggiorno i tassi di cambio nell'oggetto condiviso
			try
			{
				lock.lock();
				tassi=TassiDiCambio.costruisciTassiDiCambio(tassi_temp);
			}
			finally
			{
				lock.unlock();
			}
			
			tassi.scriviMatrice();
			
			Thread.sleep(100);
		}
		
		continua=false;
		
		for(Thread t : sportelli)
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
		System.out.println("Terminato");
	}

}