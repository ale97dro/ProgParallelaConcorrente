import java.util.ArrayList;
import java.util.List;

class Amico implements Runnable
{
	private final int id;
	private final List<String> casella_posta;
	private int counter_lettere;
	
	public Amico(int id)
	{
		this.id=id;
		casella_posta=new ArrayList<String>();
	}
	
	@Override
	public void run()
	{
		System.out.println("Thread "+id);
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getCounterLettere()
	{
		return counter_lettere;
	}
}


public class Esercizio3 
{
	public static void main(String[] args)
	{
		List<Thread>threads=new ArrayList<Thread>();
	
		for(int i=0;i<2;i++)
		{
			threads.add(new Thread(new Amico(i+1)));
		}
		
		threads.forEach(Thread::start);
		
		System.out.println("Thread andati");
	}
}
