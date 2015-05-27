package se.sics.kompics.system.peer;

import java.math.BigInteger;

import se.sics.kompics.Event;

public class JoinPeer extends Event {

	private final BigInteger peerId;

//-------------------------------------------------------------------	
	public JoinPeer(BigInteger peerId) {
		this.peerId = peerId;
	}

//-------------------------------------------------------------------	
	public BigInteger getPeerId() {
		return this.peerId;
	}
}
