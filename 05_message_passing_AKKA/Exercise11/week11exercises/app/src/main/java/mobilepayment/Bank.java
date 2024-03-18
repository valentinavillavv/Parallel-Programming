package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Bank extends AbstractBehavior<Bank.BankCommand> {

  /* --- Messages ------------------------------------- */
  public interface BankCommand {
  }

  public static final class Transaction implements BankCommand {
    public final ActorRef<Account.AccountCommand> sender;
    public final ActorRef<Account.AccountCommand> receiver;
    public final int amount;

    public Transaction(ActorRef<Account.AccountCommand> sender, ActorRef<Account.AccountCommand> receiver, int amount) {
      this.sender = sender;
      this.receiver = receiver;
      this.amount = amount;
    }
  }

  /* --- State ---------------------------------------- */
  // empty

  /* --- Constructor ---------------------------------- */
  private Bank(ActorContext<BankCommand> context) {
    super(context);
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<BankCommand> create() {
    return Behaviors.setup(Bank::new);
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<BankCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(Transaction.class, this::onTransaction)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  public Behavior<BankCommand> onTransaction(Transaction msg) {
    getContext().getLog().info("{}: Actor {} subscribed",
        getContext().getSelf().path().name(),
        msg.sender.path().name(),
        msg.receiver.path().name());
    msg.sender.tell(new Account.Deposit(-msg.amount));
    msg.receiver.tell(new Account.Deposit(msg.amount));
    return this;
  }
}
