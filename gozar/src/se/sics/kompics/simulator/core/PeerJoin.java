package se.sics.kompics.simulator.core;

import java.math.BigInteger;

import se.sics.kompics.Event;

public final class PeerJoin extends Event {
	private final BigInteger peerId;
	private final PeerType peerType;

//-------------------------------------------------------------------	
	public PeerJoin(BigInteger peerId, PeerType peerType) {
		this.peerId = peerId;
		this.peerType = peerType;
	}

//-------------------------------------------------------------------	
	public BigInteger getPeerId() {
		return peerId;
	}

//-------------------------------------------------------------------	
	public PeerType getPeerType() {
		return this.peerType;
	}
}
