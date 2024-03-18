package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import java.util.Random;

public class MobileApp extends AbstractBehavior<MobileApp.MobileAppCommand> {

	/* --- Messages ------------------------------------- */
	public interface MobileAppCommand {
	}

	public static final class MakePayment implements MobileAppCommand {
		public final ActorRef<Account.AccountCommand> sender;
		public final ActorRef<Account.AccountCommand> receiver;
		public final ActorRef<Bank.BankCommand> bank;

		public MakePayment(ActorRef<Account.AccountCommand> sender, ActorRef<Account.AccountCommand> receiver,
				ActorRef<Bank.BankCommand> bank) {
			this.sender = sender;
			this.receiver = receiver;
			this.bank = bank;
		}
	}

	public static final class Pay implements MobileAppCommand {
		public final ActorRef<Account.AccountCommand> sender;
		public final ActorRef<Account.AccountCommand> receiver;
		public final ActorRef<Bank.BankCommand> bank;
		public final int amount;

		public Pay(ActorRef<Account.AccountCommand> sender, ActorRef<Account.AccountCommand> receiver,
				ActorRef<Bank.BankCommand> bank, int amount) {
			this.sender = sender;
			this.receiver = receiver;
			this.bank = bank;
			this.amount = amount;
		}
	}

	/* --- State ---------------------------------------- */
	// empty

	/* --- Constructor ---------------------------------- */
	// Feel free to extend the contructor at your convenience
	private MobileApp(ActorContext context) {
		super(context);
		context.getLog().info("Mobile app {} started!", context.getSelf().path().name());
	}

	/* --- Actor initial state -------------------------- */
	public static Behavior<MobileApp.MobileAppCommand> create() {
		return Behaviors.setup(MobileApp::new);
	}

	/* --- Message handling ----------------------------- */
	@Override
	public Receive<MobileAppCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(MakePayment.class, this::onMakePayment)
				.build();
	}

	/* --- Handlers ------------------------------------- */
	public Behavior<MobileAppCommand> onMakePayment(MakePayment msg) {
		getContext().getLog().info("{}: Actor {} subscribed",
				getContext().getSelf().path().name(),
				msg.sender.path().name(),
				msg.receiver.path().name());
		new Random().ints(100).forEach(
				i -> {
					msg.bank.tell(new Bank.Transaction(msg.sender, msg.receiver, i));
				});
		return this;
	}

	public Behavior<MobileAppCommand> Pay(Pay msg) {
		getContext().getLog().info("{}: Actor {} subscribed",
				getContext().getSelf().path().name(),
				msg.sender.path().name(),
				msg.receiver.path().name());

		msg.bank.tell(new Bank.Transaction(msg.sender, msg.receiver, amount));

		return this;
	}
}
