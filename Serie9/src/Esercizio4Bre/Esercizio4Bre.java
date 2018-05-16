package Esercizio4Bre;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class Esercizio4Bre {

	public static void main(String[] args) {
		final int N = 10;
		List<Thread> listaClienti = creaClienti(N); //clienti del barbiere
		Barbiere barbiere = new Barbiere(); //barbiere

		Thread threadBarbiere = new Thread(barbiere);
		threadBarbiere.start(); //avvio thread del barbiere

		for (int i = 0; i < N; i++) 
		{
			try 
			{
				Thread.sleep(ThreadLocalRandom.current().nextInt(450, 701));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			listaClienti.get(i).start();
		}

		for (Thread t : listaClienti)
		{
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			threadBarbiere.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static List<Thread> creaClienti(int n) 
	{
		List<Thread> lista = new ArrayList<>();
		for (int i = 0; i < n; i++) 
		{
			Thread t = new Thread(new Cliente(i + 1));
			lista.add(t);
		}
		return lista;
	}

}

class Barbiere implements Runnable {

	public static Queue<Cliente> clienti = new ConcurrentLinkedQueue<>();
	public static boolean barbiere_dorme;
	public static AtomicInteger clientiArrivati = new AtomicInteger(0);
	public static Object o = new Object();

	@Override
	public void run() 
	{
		barbiere_dorme = true;
		synchronized (o) {
			while (clientiArrivati.get() < 10 || !clienti.isEmpty()) 
			{
				if (verificaPresenzaClienti())
					tagliaCapelli();
				else {
					while (barbiere_dorme == true)
						try {
							o.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
			}

		}
	}

	public void tagliaCapelli() {
		int tempNum;
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(500, 1001));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (o) {
			tempNum = clienti.peek().getID();
			clienti.poll();
		}
		System.out.println("Barbiere: Ho finito di tagliare i capelli a Cliente"+tempNum);
	}

	boolean verificaPresenzaClienti() {
		synchronized (o) //sincronizzo su oggetto vuoto
		{
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(50, 101)); //tempo impiegato per controllare
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (clienti.isEmpty())  //se non c'è nessun cliente
			{	
				System.out.println("Barbiere: non c'è nessun cliente, dormo");
				barbiere_dorme = true; //metto flag per barbiere a dormire
				return false;
			}
			return true;
		}
	}
}

class Cliente implements Runnable 
{
	private int id;

	public Cliente(int id) 
	{
		this.id = id;
	}

	@Override
	public void run() 
	{
		svegliaBarbiere();
	}

	void vaiNellaSalaDiAttesa() {
			System.out.println("Cliente" + id + ": Mi vado a sedere in sala d'attesa.");
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(80, 161));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	private void svegliaBarbiere() 
	{
		synchronized (Barbiere.o) 
		{
			if (Barbiere.clienti.isEmpty()) 
			{
				System.out.println("Cliente" + id + ": Sveglia dormiglione!");
				Barbiere.o.notifyAll();
				Barbiere.barbiere_dorme = false;
			} else
				vaiNellaSalaDiAttesa();
			
			Barbiere.clienti.add(this);
			Barbiere.clientiArrivati.incrementAndGet();
		}
	}
	
	public int getID() {
		return id;
	}

}