package se.sics.kompics.simulator.snapshot;

import java.util.ArrayList;
import java.util.HashMap;

import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.simulator.utils.FileIO;
import se.sics.kompics.simulator.utils.GraphUtil;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.gozar.GozarDescriptor;

public class Snapshot {
	private static HashMap<PeerAddress, PeerInfo> peers = new HashMap<PeerAddress, PeerInfo>();
	private static HashMap<PeerAddress, Integer> fanout = new HashMap<PeerAddress, Integer>();
	private static HashMap<PeerAddress, Integer> fanin = new HashMap<PeerAddress, Integer>();
	private static int deadRelay = 0;	
	private static int counter = 0;
	private static int notSuccessfulGossip = 0;
	private static String FILENAME = "gozar.out";
	private static GraphUtil g = new GraphUtil();

//-------------------------------------------------------------------
	public static void init(int numOfStripes) {
		FileIO.write("", FILENAME);
	}

//-------------------------------------------------------------------
	public static void addPeer(PeerAddress address, PeerType peerType) {
		peers.put(address, new PeerInfo(peerType));
		fanin.put(address, 0);
		fanout.put(address, 0);
	}

//-------------------------------------------------------------------
	public static void removePeer(PeerAddress address) {
		peers.remove(address);
		fanin.remove(address);
		fanout.remove(address);
	}

//-------------------------------------------------------------------
	public static void incDeadRelay() {
		deadRelay++;
	}

//-------------------------------------------------------------------
	public static void incRelayLoad(PeerAddress address, int size) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.incRelayLoad(size);
	}

//-------------------------------------------------------------------
	public static void updateRegisteredNode(PeerAddress address, int registeredNodes) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updateRegisterdNodes(registeredNodes);
	}

//-------------------------------------------------------------------
	public static void incTotalLoad(PeerAddress address, int size) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.incTotalLoad(size);
	}

//-------------------------------------------------------------------
	public static void incSelectedTimes(PeerAddress address) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.incSelectedTimes();
	}
	
//-------------------------------------------------------------------
	public static void updatePartners(PeerAddress address, ArrayList<GozarDescriptor> partners) {
		PeerInfo peerInfo = peers.get(address);
		
		if (peerInfo == null)
			return;
		
		peerInfo.updatePartners(partners);
		fanout.put(address, partners.size());
	}

//-------------------------------------------------------------------
	public static void incNotSuccessfulGossip() {
		notSuccessfulGossip++;
	}
	
//-------------------------------------------------------------------
	public static void report() {
		String str = new String();
		str += "current time: " + counter++ + "\n";
		str += reportNetworkState();
		str += reportLoad();
		str += reportRandomness();
		str += reportFanoutHistogram();
		str += reportFaninHistogram();
		//str += reportDetailes();
		str += "not successful gossips: " + notSuccessfulGossip + "\n";
		str += "graph statistics: " + reportGraphStat();
		str += "###\n";
		
		System.out.println(str);
		FileIO.append(str, FILENAME);
	}

//-------------------------------------------------------------------
	private static String reportNetworkState() {
		String str = new String("---\n");
		str += "total number of peers: " + peers.size() + "\n";
		
		return str;		
	}

//-------------------------------------------------------------------
	private static String reportLoad() {
		String str = new String("---\n");
		int openLoad = 0;
		int nattedLoad = 0;
		int openNodes = 0;
		int nattedNodes = 0;
		int totalRelayLoad = 0;
		int totalLoad = 0;

		for (PeerInfo info : peers.values()) {
			totalRelayLoad += info.getRelayLoad();
			totalLoad += info.getTotalLoad();
			
			if (info.getPeerType() == PeerType.OPEN) {
				openLoad += info.getTotalLoad();;
				openNodes++;
			} else if (info.getPeerType() == PeerType.RC || info.getPeerType() == PeerType.PRC || info.getPeerType() == PeerType.SYM) {
				nattedLoad += info.getTotalLoad();
				nattedNodes++;
			}
		}
		
		if (nattedNodes != 0) {
			str += "total natted load: " + nattedLoad + "\n";
			str += "total natted nodes: " + nattedNodes + "\n";
			str += "avg. natted nodes load: " + nattedLoad / nattedNodes + "\n";
		}
		
		if (openNodes != 0) {
			str += "total open load: " + openLoad + "\n";
			str += "total open nodes: " + openNodes + "\n";
			str += "avg. open nodes load: " + openLoad / openNodes + "\n";
		}
		
		str += "total load: " + totalLoad + "\n";
		str += "total relay load: " + totalRelayLoad + "\n";
		
		return str;
	}
	
//	private static String reportLoad() {
//		String str = new String("---\n");
//		int openLoad = 0;
//		int nattedLoad = 0;
//		int openNodes = 0;
//		int nattedNodes = 0;
//		HashMap<Integer, Integer> relayLoadDistr = new HashMap<Integer, Integer>();
//		HashMap<Integer, Integer> registeredNodesDistr = new HashMap<Integer, Integer>();
//		HashMap<Integer, Integer> totalRegisteredNodesDistr = new HashMap<Integer, Integer>();
//
//		Integer count;
//		Integer registeredNodesCount;
//		Integer totalRegisteredNodesCount;
//		int load;
//		int relayLoad;
//		int registerNodes;
//		int totalRegisterNodes;
//		for (PeerInfo info : peers.values()) {
//			if (info.getPeerType() == PeerType.OPEN) {
//				relayLoad = info.getRelayLoad();
//				load = info.getTotalLoad();
//				registerNodes = info.getRegisterdNodes();
//				totalRegisterNodes = info.getTotalRegisterdNodes();
//				openLoad += load;
//				openNodes++;
//				
//				count = relayLoadDistr.get(relayLoad);
//				if (count == null)
//					relayLoadDistr.put(relayLoad, 1);
//				else
//					relayLoadDistr.put(relayLoad, count + 1);
//				
//				registeredNodesCount = registeredNodesDistr.get(registerNodes);
//				
//				if (registeredNodesCount == null)
//					registeredNodesDistr.put(registerNodes, 1);
//				else
//					registeredNodesDistr.put(registerNodes, registeredNodesCount + 1);
//
//				totalRegisteredNodesCount = totalRegisteredNodesDistr.get(totalRegisterNodes);
//
//				if (totalRegisteredNodesCount == null)
//					totalRegisteredNodesDistr.put(totalRegisterNodes, 1);
//				else
//					totalRegisteredNodesDistr.put(totalRegisterNodes, totalRegisteredNodesCount + 1);
//				
//			} else if (info.getPeerType() == PeerType.NATTED) {
//				nattedLoad += info.getTotalLoad();
//				nattedNodes++;
//			}
//		}
//		
//		str += "total dead relay: " + deadRelay + "\n";
//		
//		if (nattedNodes != 0) {
//			str += "total natted load: " + nattedLoad + "\n";
//			str += "total natted nodes: " + nattedNodes + "\n";
//			str += "avg. natted nodes load: " + nattedLoad / nattedNodes + "\n";
//		}
//		
//		if (openNodes != 0) {
//			str += "total open load: " + openLoad + "\n";
//			str += "total open nodes: " + openNodes + "\n";
//			str += "avg. open nodes load: " + openLoad / openNodes + "\n";
//			str += "open nodes relay load dist: " + relayLoadDistr + "\n";
//			str += "open nodes registered nodes dist: " + registeredNodesDistr + "\n";
//			str += "open nodes total registered nodes dist: " + totalRegisteredNodesDistr + "\n";
//		}
//		
//		return str;
//	}

//-------------------------------------------------------------------
	private static String reportRandomness() {
		String str = new String("---\n");
		HashMap<Integer, Integer> randomness = new HashMap<Integer, Integer>();

		int selectedTimes;
		Integer count;
		for (PeerInfo info : peers.values()) {
			selectedTimes = info.getSelectedTimes() / 10;
			count = randomness.get(selectedTimes);
			
			if (count == null)
				randomness.put(selectedTimes, 1);
			else
				randomness.put(selectedTimes, count + 1);
		}
		
		str += "global randomness: " + randomness.toString() + "\n";			

		return str;
	}

//-------------------------------------------------------------------
	private static String reportFanoutHistogram() {
		HashMap<Integer, Integer> fanoutHistogram = new HashMap<Integer, Integer>();
		String str = new String("---\n");

		Integer n;
		for (Integer num : fanout.values()) {
			n = fanoutHistogram.get(num);
			
			if (n == null)
				fanoutHistogram.put(num, 1);
			else
				fanoutHistogram.put(num, n + 1);
			
		}
		
		str += "out-degree: " + fanoutHistogram.toString() + "\n";
		
		return str;
	}

//-------------------------------------------------------------------
	private static String reportFaninHistogram() {
		HashMap<Integer, Integer> faninHistogram = new HashMap<Integer, Integer>();
		String str = new String("---\n");

		int count;
		for (PeerAddress node : fanin.keySet()) {
			count = 0;
			for (PeerInfo peerInfo : peers.values()) {
				if (peerInfo.getPartners() != null && peerInfo.isPartner(node))
					count++;					
			}
			
			fanin.put(node, count);
		}

		Integer n;
		for (Integer num : fanin.values()) {
			n = faninHistogram.get(num);
			
			if (n == null)
				faninHistogram.put(num, 1);
			else
				faninHistogram.put(num, n + 1);
			
		}
		
		str += "in-degree: " + faninHistogram.toString() + "\n";
		
		return str;
	}
//-------------------------------------------------------------------
	private static String reportDetailes() {
		PeerInfo peerInfo;
		String str = new String("---\n");

		for (PeerAddress peer : peers.keySet()) {
			peerInfo = peers.get(peer);		
			str += "peer: " + peer + ", prtners: " + peerInfo.getPartners() + "\n";
		}
		
		return str;
	}

//-------------------------------------------------------------------
	private static String reportGraphStat() {
		String str = new String("---\n");
		double id, od, cc, pl, istd, cs;
		int diameter;
		
		g.init(peers);
		id = g.getMeanInDegree();
		istd = g.getInDegreeStdDev();
		od = g.getMeanOutDegree();
		cc = g.getMeanClusteringCoefficient();
		pl = g.getMeanPathLength();
		cs = g.getMaxClusterSize();
		diameter = g.getDiameter();

		str += "Diameter: " + diameter + "\n";
		str += "Average path length: " + String.format("%.4f", pl) + "\n";
		str += "Clustering-coefficient: " + String.format("%.4f", cc) + "\n";
		str += "Average in-degree: " + String.format("%.4f", id) + "\n";
		str += "In-degree standard deviation: " + String.format("%.4f", istd) + "\n";
		str += "Average out-degree: " + String.format("%.4f", od) + "\n";
		str += "Biggest cluster size: " + cs + "\n";
		
		return str;
	}
}
