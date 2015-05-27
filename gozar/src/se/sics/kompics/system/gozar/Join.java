package se.sics.kompics.system.gozar;

import java.util.LinkedList;

import se.sics.kompics.Event;
import se.sics.kompics.system.common.PeerAddress;

public final class Join extends Event {

	private final PeerAddress peerSelf;
	private final LinkedList<PeerAddress> insiders;

//-------------------------------------------------------------------	
	public Join(PeerAddress self, LinkedList<PeerAddress> insiders) {
		super();
		this.peerSelf = self;
		this.insiders = insiders;
	}

//-------------------------------------------------------------------	
	public final PeerAddress getPeerSelf() {
		return peerSelf;
	}

//-------------------------------------------------------------------	
	public LinkedList<PeerAddress> getCyclonInsiders() {
		return insiders;
	}

//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return "Join(" + peerSelf + ", " + insiders + ")";
	}
}
