package se.sics.kompics.system.gozar;

import java.util.UUID;

import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.common.PeerMessage;

public class ShuffleRequest extends PeerMessage {

	private static final long serialVersionUID = 8493601671018888143L;
	private final UUID requestId;
	private final DescriptorBuffer randomBuffer;
	private PeerAddress relayNode;
	private PeerType sourceType;

//-------------------------------------------------------------------
	public ShuffleRequest(UUID requestId, DescriptorBuffer randomBuffer, PeerAddress source, PeerAddress destination, PeerAddress relayNode, PeerType sourceType) {
		super(source, destination);
		this.requestId = requestId;
		this.randomBuffer = randomBuffer;
		this.relayNode = relayNode;
		this.sourceType = sourceType;
	}

//-------------------------------------------------------------------
	public UUID getRequestId() {
		return requestId;
	}

//-------------------------------------------------------------------
	public DescriptorBuffer getRandomBuffer() {
		return randomBuffer;
	}

//-------------------------------------------------------------------
	public PeerAddress getRelayNode() {
		return this.relayNode;
	}

//-------------------------------------------------------------------
	public PeerType getSourceType() {
		return this.sourceType;
	}

//-------------------------------------------------------------------
	public int getSize() {
		return 0;
	}

//-------------------------------------------------------------------
	public int getMessageSize() {
		return 20 + 2 + 5 * (4 + Gozar.RELAY_NODE_SIZE * 4);
	}

}
