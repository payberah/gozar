package se.sics.kompics.system.common;

import se.sics.kompics.network.Message;

public class PeerMessage extends Message {
	private static final long serialVersionUID = -6815596147580962155L;
	private final PeerAddress source;
	private final PeerAddress destination;

//-------------------------------------------------------------------	
	public PeerMessage(PeerAddress source, PeerAddress destination) {
		super(source.getPeerAddress(), destination.getPeerAddress());
		this.source = source;
		this.destination = destination;
	}

//-------------------------------------------------------------------	
	public PeerAddress getPeerDestination() {
		return destination;
	}

//-------------------------------------------------------------------	
	public PeerAddress getPeerSource() {
		return source;
	}
}

