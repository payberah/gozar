package se.sics.kompics.system.gozar;

import se.sics.kompics.Event;
import se.sics.kompics.system.common.PeerAddress;

public class JoinCompleted extends Event {

	private final PeerAddress localPeer;

//-------------------------------------------------------------------	
	public JoinCompleted(PeerAddress localPeer) {
		super();
		this.localPeer = localPeer;
	}

//-------------------------------------------------------------------	
	public PeerAddress getLocalPeer() {
		return localPeer;
	}
}
