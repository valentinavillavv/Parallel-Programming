package mobilepayment;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Account extends AbstractBehavior<Account.AccountCommand> {

  /* --- Messages ------------------------------------- */
  public interface AccountCommand {
  }

  // 11.1.2
  public static final class Deposit implements AccountCommand {
    public final int amount;

    public Deposit(int amount) {
      this.amount = amount;
    }
  }

  // 11.1.7
  private static final class PrintBalance implements AccountCommand {
  }

  /* --- State ---------------------------------------- */
  private int total;

  /* --- Constructor ---------------------------------- */
  private Account(ActorContext<AccountCommand> context) {
    super(context);
    total = 0;
  }

  /* --- Actor initial state -------------------------- */
  public static Behavior<Account.AccountCommand> create() {
    return Behaviors.setup(Account::new);
  }

  /* --- Message handling ----------------------------- */
  @Override
  public Receive<AccountCommand> createReceive() {
    return newReceiveBuilder()
        .onMessage(Deposit.class, this::onDeposit)
        .onMessage(PrintBalance.class, this::onPrintBalance)
        .build();
  }

  /* --- Handlers ------------------------------------- */
  public Behavior<AccountCommand> onDeposit(Deposit msg) {
    this.getContext()
        .getLog()
        .info("Received a deposit message!");
    total = total + msg.amount;
    return this;
  }

  public Behavior<AccountCommand> onPrintBalance(PrintBalance msg) {
    this.getContext()
        .getLog()
        .info("Total amount in the account: {}", total);
    return this;
  }

}