package mobilepayment;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

public class Guardian extends AbstractBehavior<Guardian.GuardianCommand> {

	/* --- Messages ------------------------------------- */
	public interface GuardianCommand {
	}

	public static final class KickOffGuardian implements GuardianCommand {
		private final String mobileAppName;

		public void KickOff(String mobileAppName) {
            this.mobileAppName = mobileAppName;
        }

		public String getMobileAppName() {
			return mobileAppName;
		}
	}

	/* --- State ---------------------------------------- */
	// empty

	/* --- Constructor ---------------------------------- */
	private Guardian(ActorContext<GuardianCommand> context) {
		super(context);
	}

	/* --- Actor initial state -------------------------- */
	public static Behavior<Guardian.GuardianCommand> create() {
		return Behaviors.setup(Guardian::new);
	}

	/* --- Message handling ----------------------------- */
	@Override
	public Receive<GuardianCommand> createReceive() {
		return newReceiveBuilder()
				.onMessage(KickOffGuardian.class, this::onKickOff)
				.build();
	}

	/* --- Handlers ------------------------------------- */
	private Behavior<GuardianCommand> onKickOff(KickOffGuardian msg) {

		// spawn the MobileApp actor 11.1.1
		ActorRef<MobileApp.MobileAppCommand> mobileApp = getContext().spawn(MobileApp.create(), "mobileApp_actor");
		getContext().getLog().info("Mobile app {} started!", "mobileApp_actor");

		// 11.1.5 (2 mobile apps, 2 banks, and 2 accounts)
		ActorRef<MobileApp.MobileAppCommand> mb1 = getContext().spawn(MobileApp.create(), "mb1_actor");
		ActorRef<MobileApp.MobileAppCommand> mb2 = getContext().spawn(MobileApp.create(), "mb2_actor");
		ActorRef<Bank.BankCommand> b1 = getContext().spawn(Bank.create(), "b1_actor");
		ActorRef<Bank.BankCommand> b2 = getContext().spawn(Bank.create(), "b1_actor");
		ActorRef<Account.AccountCommand> a1 = getContext().spawn(Account.create(), "a1_actor");
		ActorRef<Account.AccountCommand> a2 = getContext().spawn(Account.create(), "a2_actor");
		b1.tell(new Bank.Transaction(a1, a2, 100));
		b2.tell(new Bank.Transaction(a2, a1, 100));
		return this;
	}

}
