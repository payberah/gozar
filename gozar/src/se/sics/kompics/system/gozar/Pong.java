package se.sics.kompics.system.gozar;

import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.common.PeerMessage;

public class Pong extends PeerMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private final int secNum;
	private final int relayCounter;

//-------------------------------------------------------------------
	public Pong(PeerAddress source, PeerAddress destination, int secNum, int relayCounter) {
		super(source, destination);
		this.secNum = secNum;
		this.relayCounter = relayCounter;
	}

//-------------------------------------------------------------------
	public int getSecNum() {
		return this.secNum;
	}

//-------------------------------------------------------------------
	public int getRelayCounter() {
		return this.relayCounter;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}

//-------------------------------------------------------------------
	public int getMessageSize() {
		return 0;
	}
}
