import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;

class Sciatore implements Runnable
{
	private int id;
	
	public Sciatore(int id)
	{
		this.id=id;
	}
	
	public int getId()
	{
		return id;
	}

	@Override
	public void run() 
	{
		int tempo_totale=0;
		
		Esercizio7.partenza.countDown();
		try {
			Esercizio7.partenza.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		for(int i=0;i<6;i++)
		{
			int tempo_tappa=ThreadLocalRandom.current().nextInt(4,9);
			try {
				Thread.sleep(tempo_tappa);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tempo_totale+=tempo_tappa;
			
			Esercizio7.tempi[i].set(id, tempo_tappa); //tempo della tappa
			Esercizio7.tappe_completate[i].countDown(); //tappa completata
		}
		
		System.out.println("Sciatore"+id+" completa in "+tempo_totale);
	}
}

class TempoSciatore implements Comparable<TempoSciatore>
{
	private int id;
	private long tempo;
	
	public TempoSciatore(int id, long l)
	{
		this.id=id;
		this.tempo=l;
	}

	@Override
	public int compareTo(TempoSciatore arg0) 
	{
		return (int) (tempo-arg0.getTempo());
	}
	
	public long getTempo()
	{
		return tempo;
	}
	
	@Override
	public String toString()
	{
		return "Sciatore"+id+" tempo: "+tempo+" ms";
	}
}

class Commissario implements Runnable
{
	private int tappa;
	
	public Commissario(int tappa)
	{
		this.tappa=tappa;
	}

	@Override
	public void run() 
	{
		try {
			Esercizio7.tappe_completate[tappa].await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//ARRIVO QUI, tutti gli sciatori hanno completato la tappa
		//Devo recuperare i tempi e scrivere, in ordine, chi è arrivato prima
		
		List<TempoSciatore> tempi = new ArrayList<>();
		for(int i=0;i<5;i++)
			tempi.add(new TempoSciatore(i, Esercizio7.tempi[tappa].get(i)));
		
		Collections.sort(tempi);
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<5;i++)
		{
			sb.append(i+") "+tempi.get(i).toString()+"\n");
		}
		
		System.out.println("Classifica tappa "+tappa+": "+sb.toString());
	}
}



public class Esercizio7 
{
	static final CountDownLatch partenza = new CountDownLatch(5); //per farli partire insieme
	static final CountDownLatch[] tappe_completate=new CountDownLatch[6]; //notifica quando tutti gli sciatori completano una tappa
	static final AtomicLongArray[] tempi = new AtomicLongArray[6]; //tempi di ogni sciatore per ogni tappa
	
	public static void main(String[] args)
	{
		//inizializzo i contatori
		for(int i=0;i<6;i++)
		{
			tappe_completate[i]=new CountDownLatch(5);
			tempi[i]=new AtomicLongArray(5);
		}
		
		
		final ExecutorService executor = Executors.newFixedThreadPool(11); //6 commissari per sei tappe, 5 sciatori
		
		
		for(int i=0;i<6;i++)
			executor.execute(new Commissario(i));
		
		for(int i=0;i<5;i++)
			executor.execute(new Sciatore(i));
		
		executor.shutdown();
		
	}
}
