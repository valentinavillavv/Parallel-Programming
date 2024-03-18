package exercise61;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ThreadsAccountExperimentsManyPool {

  static final int N = 10; 
  static final int NO_TRANSACTION=5;
  static final int NO_THREADS = 10;
  static final Account[] accounts = new Account[N];
  static final List<Future<?>> list = new ArrayList<>();
  static private ExecutorService pool = new ForkJoinPool(4);

  static Random rnd = new Random();
  
  public static void main(String[] args){ new ThreadsAccountExperimentsMany(); }

  public ThreadsAccountExperimentsManyPool(){
    
    for( int i = 0; i < N; i++){
      accounts[i] = new Account(i);
    }
    
    for( int i = 0; i<NO_THREADS; i++){
      try{ 
        Future<?> f1= pool.submit( () -> doNTransactions(NO_TRANSACTION) );
        list.add(f1);
        }
      catch(Error ex){
        System.out.println("At i = " + i + " I got error: " + ex);
        System.exit(0);
      }
    }

   /* try {
	    for(Future<?> f : list) {
		    f.get(); // Wait for each future to be executed and add partial result
	    }
	  } catch (InterruptedException | ExecutionException e) {
	    e.printStackTrace();
	  }
	  pool.shutdown(); // We are sure to be done, so we shut down the pool*/
  }
  
  private static void doNTransactions(int noTransactions){
    for(int i = 0; i<noTransactions; i++){
      long amount = rnd.nextInt(5000)+100;
      int source = rnd.nextInt(N);
      int target = (source + rnd.nextInt(N-2)+1) % N; // make sure target <> source
      doTransaction( new Transaction( amount, accounts[source], accounts[target]));
    }
  }
  
  private static void doTransaction(Transaction t){
    System.out.println(t);
    t.transfer();
  }
  
  static class Transaction {
    final Account source, target;
    final long amount;
    Transaction(long amount, Account source, Account target){
      this.amount = amount;
      this.source = source;
      this.target = target;
    }
    
    public void transfer(){
      Account min = accounts[Math.min(source.id, target.id)];
      Account max = accounts[Math.max(source.id, target.id)];
      synchronized(min){
        synchronized(max){
      /* 
      Account s= accounts[source.id];
      Account t = accounts[target.id];
      synchronized(s){
        synchronized(t){ */
      
          source.withdraw(amount);
          try{Thread.sleep(50);} catch(Exception e){}; // Simulate transaction time
          target.deposit(amount);
        }
      }
    }
    
    public String toString(){
      return "Transfer " + amount + " from " + source.id + " to " + target.id;
    }
  }

  static class Account{
    // should have transaction history, owners, account-type, and 100 other real things
    public final int id;
    private long balance = 0;
    Account( int id ){ this.id = id;}
    public void deposit(long sum){ balance += sum; } 
    public void withdraw(long sum){ balance -= sum; }
    public long getBalance(){ return balance; }
  }

}
