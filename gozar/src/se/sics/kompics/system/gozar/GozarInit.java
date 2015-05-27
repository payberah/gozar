package se.sics.kompics.system.gozar;

import se.sics.kompics.Init;
import se.sics.kompics.simulator.core.PeerType;

public final class GozarInit extends Init {

	private final GozarConfiguration configuration;
	private final PeerType peerType;

//-------------------------------------------------------------------
	public GozarInit(GozarConfiguration configuration, PeerType peerType) {
		super();
		this.configuration = configuration;
		this.peerType = peerType;
	}

//-------------------------------------------------------------------
	public GozarConfiguration getConfiguration() {
		return this.configuration;
	}

//-------------------------------------------------------------------
	public PeerType getPeerType() {
		return this.peerType;
	}
}