package se.sics.kompics.system.gozar;

import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.common.PeerMessage;

public class Ping extends PeerMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private final int seqNum;
	private final RegisterState registerState;

//-------------------------------------------------------------------
	public Ping(PeerAddress source, PeerAddress destination, int seqNum, RegisterState registerState) {
		super(source, destination);
		this.seqNum = seqNum;
		this.registerState = registerState;
	}

//-------------------------------------------------------------------
	public int getSeqNum() {
		return this.seqNum;
	}

//-------------------------------------------------------------------
	public RegisterState getRegisterState() {
		return this.registerState;
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
