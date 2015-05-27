package se.sics.kompics.system.gozar;

import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.common.PeerMessage;

public class RelayShuffleResponse extends PeerMessage {

	private static final long serialVersionUID = 8854120720733113053L;
	private ShuffleResponse response;
	
//-------------------------------------------------------------------	
	public RelayShuffleResponse(PeerAddress source, PeerAddress destination, ShuffleResponse request) {
		super(source, destination);
		this.response = request;
	}

//-------------------------------------------------------------------	
	public ShuffleResponse getShuffleResponse() {
		return this.response;
	}

//-------------------------------------------------------------------	
	public int getSize() {
		return 0;
	}

//-------------------------------------------------------------------	
	public int getMessageSize() {
		return 20 + 2 + 5 * (4 + Gozar.RELAY_NODE_SIZE * 4) + 4;
	}

}
