package se.sics.kompics.simulator.core;

import java.math.BigInteger;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.sics.kompics.ChannelFilter;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.Stop;
import se.sics.kompics.address.Address;
import se.sics.kompics.main.Configuration;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Network;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.simulator.snapshot.Snapshot;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.gozar.GozarConfiguration;
import se.sics.kompics.system.peer.JoinPeer;
import se.sics.kompics.system.peer.PeerConfiguration;
import se.sics.kompics.system.peer.Peer;
import se.sics.kompics.system.peer.PeerInit;
import se.sics.kompics.system.peer.PeerPort;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;

public final class GozarSimulator extends ComponentDefinition {

	Positive<GozarSimulatorPort> simulator = positive(GozarSimulatorPort.class);
	Positive<Network> network = positive(Network.class);
	Positive<Timer> timer = positive(Timer.class);

	private static final Logger logger = LoggerFactory.getLogger(GozarSimulator.class);
	private final HashMap<BigInteger, Component> peers;
	private final HashMap<BigInteger, PeerAddress> peersAddress;
	
	// peer initialization state
	private Address peer0Address;
	private BootstrapConfiguration bootstrapConfiguration;
	private GozarConfiguration gozarConfiguration;
	private PeerConfiguration peerConfiguration;	
	private PingFailureDetectorConfiguration fdConfiguration;

	private int peerIdSequence;
	private BigInteger idSpaceSize;
	private ConsistentHashtable<BigInteger> view;

//-------------------------------------------------------------------	
	public GozarSimulator() {
		peers = new HashMap<BigInteger, Component>();
		peersAddress = new HashMap<BigInteger, PeerAddress>();
		view = new ConsistentHashtable<BigInteger>();

		subscribe(handleInit, control);

		subscribe(handleGenerateReport, timer);
		
		subscribe(handleGozarPeerJoin, simulator);
		subscribe(handleGozarPeerFail, simulator);
	}

//-------------------------------------------------------------------	
	Handler<GozarSimulatorInit> handleInit = new Handler<GozarSimulatorInit>() {
		public void handle(GozarSimulatorInit init) {
			peers.clear();
			peerIdSequence = 0;

			peer0Address = init.getPeer0Address();
			bootstrapConfiguration = init.getBootstrapConfiguration();
			gozarConfiguration = init.getGozarConfiguration();
			fdConfiguration = init.getFdConfiguration();
			peerConfiguration = init.getPeerConfiguration();

			idSpaceSize = gozarConfiguration.getIdentifierSpaceSize();
			
			// generate periodic report
			SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(Configuration.SNAPSHOT_PERIOD, Configuration.SNAPSHOT_PERIOD);
			spt.setTimeoutEvent(new GenerateReport(spt));
			trigger(spt, timer);
		}
	};

//-------------------------------------------------------------------	
	Handler<PeerJoin> handleGozarPeerJoin = new Handler<PeerJoin>() {
		public void handle(PeerJoin event) {
			BigInteger id = event.getPeerId();
			PeerType peerType = event.getPeerType();

			// join with the next id if this id is taken
			BigInteger successor = view.getNode(id);
			while (successor != null && successor.equals(id)) {
				id = id.add(BigInteger.ONE).mod(idSpaceSize);
				successor = view.getNode(id);
			}
			
			logger.debug("JOIN@{}", id);

			Component newPeer = createAndStartNewPeer(id, peerType);
			view.addNode(id);

			trigger(new JoinPeer(id), newPeer.getPositive(PeerPort.class));
		}
	};

//-------------------------------------------------------------------	
	Handler<PeerFail> handleGozarPeerFail = new Handler<PeerFail>() {
		public void handle(PeerFail event) {
			BigInteger id = view.getNode(event.getPeerId());

			logger.debug("FAIL@" + id);

			if (view.size() == 0) {
				System.err.println("Empty network");
				return;
			}

			view.removeNode(id);
			stopAndDestroyPeer(id);
		}
	};

//-------------------------------------------------------------------	
	private final Component createAndStartNewPeer(BigInteger id, PeerType peerType) {
		Component peer = create(Peer.class);
		int peerId = ++peerIdSequence;
		Address peerAddress = new Address(peer0Address.getIp(), peer0Address.getPort(), peerId);

		PeerAddress gozarPeerAddress = new PeerAddress(peerAddress, id);
		
		connect(network, peer.getNegative(Network.class), new MessageDestinationFilter(peerAddress));
		connect(timer, peer.getNegative(Timer.class));

		trigger(new PeerInit(gozarPeerAddress, peerType, peerConfiguration, bootstrapConfiguration, gozarConfiguration, fdConfiguration), peer.getControl());

		trigger(new Start(), peer.getControl());
		peers.put(id, peer);
		peersAddress.put(id, gozarPeerAddress);
		
		Snapshot.addPeer(gozarPeerAddress, peerType);

		return peer;
	}

//-------------------------------------------------------------------	
	private final void stopAndDestroyPeer(BigInteger id) {
		Component peer = peers.get(id);

		trigger(new Stop(), peer.getControl());

		disconnect(network, peer.getNegative(Network.class));
		disconnect(timer, peer.getNegative(Timer.class));

		Snapshot.removePeer(peersAddress.get(id));

		peers.remove(id);
		peersAddress.remove(id);

		destroy(peer);
	}

//-------------------------------------------------------------------	
	/**
	 * The <code>MessageDestinationFilter</code> class.
	 * 
	 * @author Cosmin Arad <cosmin@sics.se>
	 * @version $Id: MessageDestinationFilter.java 750 2009-04-02 09:55:01Z
	 *          Cosmin $
	 */
	private final static class MessageDestinationFilter extends
			ChannelFilter<Message, Address> {
		public MessageDestinationFilter(Address address) {
			super(Message.class, address, true);
		}

		public Address getValue(Message event) {
			return event.getDestination();
		}
	}
	
//-------------------------------------------------------------------	
	Handler<GenerateReport> handleGenerateReport = new Handler<GenerateReport>() {
		public void handle(GenerateReport event) {
			Snapshot.report();
		}
	};
}
