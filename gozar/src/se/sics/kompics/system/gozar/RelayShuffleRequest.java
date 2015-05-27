package se.sics.kompics.system.gozar;

import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.common.PeerMessage;

public class RelayShuffleRequest extends PeerMessage {

	private static final long serialVersionUID = 8854120720733113053L;
	private ShuffleRequest request;
	
//-------------------------------------------------------------------	
	public RelayShuffleRequest(PeerAddress source, PeerAddress destination, ShuffleRequest request) {
		super(source, destination);
		this.request = request;
	}

//-------------------------------------------------------------------	
	public ShuffleRequest gethuffleRequest() {
		return this.request;
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
