package se.sics.kompics.system.gozar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.simulator.snapshot.Snapshot;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public final class Gozar extends ComponentDefinition {
	public static int RELAY_NODE_SIZE = 1;
	
	Negative<GozarPort> gozarPort = negative(GozarPort.class);
	Positive<Network> networkPort = positive(Network.class);
	Positive<Timer> timerPort = positive(Timer.class);
	
	GozarConfiguration cyclonConfiguration;

	private PeerAddress self;
	private View view;
	private PeerType peerType;

	private int shuffleLength;
	private long shufflePeriod;
	private long shuffleTimeout;
	private long updateRelayNodeTimeout = 50000;
	private boolean joining;

	private HashMap<UUID, PeerAddress> outstandingShuffles;
	private HashMap<PeerAddress, Integer> relayNodes;
	private HashMap<PeerAddress, Integer> relayNodesLoad = new HashMap<PeerAddress, Integer>();
	private ArrayList<PeerAddress> registeredNodes = new ArrayList<PeerAddress>();
	
	private int relayCounter = 0;
	private int pingCounter = 0;
	
//-------------------------------------------------------------------	
	public Gozar() {
		outstandingShuffles = new HashMap<UUID, PeerAddress>();

		subscribe(handleInit, control);

		subscribe(handleJoin, gozarPort);

		subscribe(handleInitiateShuffle, timerPort);
		subscribe(handleShuffleTimeout, timerPort);
		subscribe(handleUpdateRelayNodes, timerPort);

		subscribe(handleShuffleRequest, networkPort);
		subscribe(handleShuffleResponse, networkPort);
		subscribe(handleRelayShuffleRequest, networkPort);
		subscribe(handleRelayShuffleResponse, networkPort);
		subscribe(handlePing, networkPort);
		subscribe(handlePong, networkPort);
	}

//-------------------------------------------------------------------	
	Handler<GozarInit> handleInit = new Handler<GozarInit>() {
		public void handle(GozarInit init) {
			cyclonConfiguration = init.getConfiguration();
			peerType = init.getPeerType();
			
			shuffleLength = cyclonConfiguration.getShuffleLength();
			shufflePeriod = cyclonConfiguration.getShufflePeriod();
			shuffleTimeout = cyclonConfiguration.getShuffleTimeout();
			
			if (peerType == PeerType.RC || peerType == PeerType.PRC || peerType == PeerType.SYM)
				relayNodes = new HashMap<PeerAddress, Integer>();
			else
				relayNodes = null;
		}
	};

//-------------------------------------------------------------------	
	Handler<Join> handleJoin = new Handler<Join>() {
		public void handle(Join event) {
			self = event.getPeerSelf();
			view = new View(cyclonConfiguration.getRandomViewSize(), self);

			LinkedList<PeerAddress> insiders = event.getCyclonInsiders();

			if (insiders.size() == 0) {
				// I am the first peer
				trigger(new JoinCompleted(self), gozarPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
				return;
			}

			PeerAddress peer = insiders.poll();
			view.initialize(insiders);
			initiateShuffle(1, peer, null, PeerType.OPEN);
			joining = true;
			
			if (peerType == PeerType.RC || peerType == PeerType.PRC || peerType == PeerType.SYM) {
				SchedulePeriodicTimeout upt = new SchedulePeriodicTimeout(0, updateRelayNodeTimeout);
				upt.setTimeoutEvent(new UpdateRelayNodes(upt));
				trigger(upt, timerPort);
			}
		}
	};

//-------------------------------------------------------------------	
	private void initiateShuffle(int shuffleSize, PeerAddress randomPeer, PeerAddress relayNode, PeerType randomPeerType) {
		// send the random view to a random peer
		ArrayList<GozarDescriptor> descriptors = view.selectToSendAtActive(shuffleSize - 1, randomPeer);
		descriptors.add(new GozarDescriptor(self, peerType, relayNodes));
		DescriptorBuffer buffer = new DescriptorBuffer(self, descriptors);
		
		ScheduleTimeout st = new ScheduleTimeout(shuffleTimeout);
		st.setTimeoutEvent(new ShuffleTimeout(st, randomPeer));
		UUID timeoutId = st.getTimeoutEvent().getTimeoutId();
		outstandingShuffles.put(timeoutId, randomPeer);
		trigger(st, timerPort);

		ShuffleRequest request = new ShuffleRequest(timeoutId, buffer, self, randomPeer, relayNode, peerType);
		
		if (randomPeerType == PeerType.OPEN)
			trigger(request, networkPort);
		else if (relayNode != null) {
			RelayShuffleRequest relayRequest = new RelayShuffleRequest(self, relayNode, request);
			trigger(relayRequest, networkPort);
		}
		
		//Snapshot.incTotalLoad(self);
	}

//-------------------------------------------------------------------	
	Handler<InitiateShuffle> handleInitiateShuffle = new Handler<InitiateShuffle>() {
		public void handle(InitiateShuffle event) {
			view.incrementDescriptorAges();
			
			GozarDescriptor desc = view.selectPeerToShuffleWith();
			
			if (desc != null) {
				PeerAddress randomPeer = desc.getPeerAddress();
				PeerType randomPeerType = desc.getPeerType();
				Snapshot.incSelectedTimes(randomPeer);				

				if (randomPeerType == PeerType.OPEN)
					initiateShuffle(shuffleLength, randomPeer, null, randomPeerType);
				if (randomPeerType == PeerType.RC || randomPeerType == PeerType.PRC || randomPeerType == PeerType.SYM) {
					HashMap<PeerAddress, Integer> relayNode = desc.getRelayNodes();
					ArrayList<PeerAddress> shuffledList = new ArrayList<PeerAddress>(relayNode.keySet());
					Collections.shuffle(shuffledList);
					
					int lowestLoad = Integer.MAX_VALUE;
					PeerAddress lowestLoadNode = null;

					for (PeerAddress peer : shuffledList) {
						if (relayNode.get(peer) < lowestLoad) {
							lowestLoad = relayNode.get(peer);
							lowestLoadNode = peer;
						}
					}

					initiateShuffle(shuffleLength, randomPeer, lowestLoadNode, randomPeerType);
				}
			}
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleRequest> handleShuffleRequest = new Handler<ShuffleRequest>() {
		public void handle(ShuffleRequest event) {
			PeerAddress source = event.getPeerSource();
			PeerAddress relayNode = event.getRelayNode();
			PeerType sourceType = event.getSourceType();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			
			DescriptorBuffer toSendRandomBuffer = new DescriptorBuffer(self, view.selectToSendAtPassive(receivedRandomBuffer.getSize(), source));
			view.selectToKeep(source, receivedRandomBuffer.getDescriptors());
			Snapshot.updatePartners(self, view.getAll());
		
			ShuffleResponse response = new ShuffleResponse(event.getRequestId(), toSendRandomBuffer, self, source);
			
			if (peerType == PeerType.OPEN || sourceType == PeerType.OPEN || sourceType == PeerType.RC || sourceType == PeerType.PRC)
				trigger(response, networkPort);
			else if (sourceType == PeerType.SYM) {
				RelayShuffleResponse relayResponse = new RelayShuffleResponse(self, relayNode, response);
				trigger(relayResponse, networkPort);
			}

			//Snapshot.incTotalLoad(self);
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleResponse> handleShuffleResponse = new Handler<ShuffleResponse>() {
		public void handle(ShuffleResponse event) {
			if (joining) {
				joining = false;
				trigger(new JoinCompleted(self), gozarPort);

				// schedule shuffling
				SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(shufflePeriod, shufflePeriod);
				spt.setTimeoutEvent(new InitiateShuffle(spt));
				trigger(spt, timerPort);
			}

			// cancel shuffle timeout
			UUID shuffleId = event.getRequestId();
			if (outstandingShuffles.containsKey(shuffleId)) {
				outstandingShuffles.remove(shuffleId);
				CancelTimeout ct = new CancelTimeout(shuffleId);
				trigger(ct, timerPort);
			}

			PeerAddress peer = event.getPeerSource();
			DescriptorBuffer receivedRandomBuffer = event.getRandomBuffer();
			view.selectToKeep(peer, receivedRandomBuffer.getDescriptors());
			Snapshot.updatePartners(self, view.getAll());
			
			//Snapshot.incTotalLoad(self);

		}
	};

//-------------------------------------------------------------------	
	Handler<RelayShuffleRequest> handleRelayShuffleRequest = new Handler<RelayShuffleRequest>() {
		public void handle(RelayShuffleRequest event) {
			if (!registeredNodes.contains(event.gethuffleRequest().getPeerDestination())) {
				Snapshot.incDeadRelay();
				return;
			}
			
			relayCounter++;
			ShuffleRequest request = event.gethuffleRequest();
			
			trigger(request, networkPort);
			
			Snapshot.incRelayLoad(self, request.getMessageSize());
			//Snapshot.incTotalLoad(self);
		}
	};

//-------------------------------------------------------------------	
	Handler<RelayShuffleResponse> handleRelayShuffleResponse = new Handler<RelayShuffleResponse>() {
		public void handle(RelayShuffleResponse event) {
			ShuffleResponse response = event.getShuffleResponse();
			
			trigger(response, networkPort);

			Snapshot.incRelayLoad(self, response.getMessageSize());
			//Snapshot.incTotalLoad(self);
		}
	};

//-------------------------------------------------------------------	
	Handler<ShuffleTimeout> handleShuffleTimeout = new Handler<ShuffleTimeout>() {
		public void handle(ShuffleTimeout event) {
			Snapshot.incNotSuccessfulGossip();
		}
	};

//-------------------------------------------------------------------	
	Handler<UpdateRelayNodes> handleUpdateRelayNodes = new Handler<UpdateRelayNodes>() {
		public void handle(UpdateRelayNodes event) {
			updateRelayNodes();
		}
	};

//-------------------------------------------------------------------	
	Handler<Ping> handlePing = new Handler<Ping>() {
		public void handle(Ping event) {
			PeerAddress source = event.getPeerSource();
			RegisterState registerState = event.getRegisterState();
			
			if (registerState == RegisterState.REGISTER && !registeredNodes.contains(source))
				registeredNodes.add(source);
			else if (registerState == RegisterState.UNREGISTER && registeredNodes.contains(source))
				registeredNodes.remove(source);
			
			trigger(new Pong(self, source, event.getSeqNum(), relayCounter), networkPort);
			
			if (registerState != RegisterState.NULL)
				Snapshot.updateRegisteredNode(self, registeredNodes.size());
		}
	};

//-------------------------------------------------------------------	
	Handler<Pong> handlePong = new Handler<Pong>() {
		public void handle(Pong event) {
			if (event.getSecNum() == pingCounter)
				relayNodesLoad.put(event.getPeerSource(), event.getRelayCounter());
		}
	};
	
//-------------------------------------------------------------------	
	private void updateRelayNodes() {
		ArrayList<PeerAddress> deadRelays = new ArrayList<PeerAddress>();
		Map<PeerAddress, Integer> sortedRelays = sortByValue(relayNodesLoad);
		
		for (PeerAddress relay : relayNodes.keySet()) {
			if (!relayNodesLoad.containsKey(relay))
				deadRelays.add(relay);
			else
				relayNodes.put(relay, relayNodesLoad.get(relay));
		}

		for (PeerAddress dead : deadRelays)
			relayNodes.remove(dead);

		if (relayNodes.size() < RELAY_NODE_SIZE) {
			for (PeerAddress relay : sortedRelays.keySet()) {
				if (!relayNodes.containsKey(relay)) {
					relayNodes.put(relay, relayNodesLoad.get(relay));
					if (relayNodes.size() == RELAY_NODE_SIZE)
						break;
				}					
			}
		}
	
		pingCounter++;
		relayNodesLoad.clear();						
		
		ArrayList<PeerAddress> openNodes = view.getOpenNodes();
		for (PeerAddress relayNode : relayNodes.keySet()) {
			if (!openNodes.contains(relayNode))
				openNodes.add(relayNode);
		}
		
		for (PeerAddress node : openNodes) {
			if (relayNodes.containsKey(node))
				trigger(new Ping(self, node, pingCounter, RegisterState.REGISTER), networkPort);
			else
				trigger(new Ping(self, node, pingCounter, RegisterState.NULL), networkPort);
		}
	}

//-------------------------------------------------------------------	
	@SuppressWarnings("unchecked")
	private Map<PeerAddress, Integer> sortByValue(Map map) {
	     List list = new LinkedList(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return ((Comparable)((Map.Entry)(o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
	          }
	     });
	     
	     Map result = new LinkedHashMap();
	     for (Iterator it = list.iterator(); it.hasNext();) {
	    	 Map.Entry entry = (Map.Entry)it.next();
	    	 result.put(entry.getKey(), entry.getValue());
	     }
	     
	     return result;
	}

}
