package se.sics.kompics.system.peer;

import se.sics.kompics.Init;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.gozar.GozarConfiguration;

public final class PeerInit extends Init {

	private final PeerAddress peerSelf;
	private final PeerType peerType;
	private final BootstrapConfiguration bootstrapConfiguration;
	private final GozarConfiguration gozarConfiguration;
	private final PeerConfiguration peerConfiguration;
	private final PingFailureDetectorConfiguration fdConfiguration;

//-------------------------------------------------------------------
	public PeerInit(PeerAddress peerSelf, PeerType peerType,
			PeerConfiguration peerConfiguration,
			BootstrapConfiguration bootstrapConfiguration,
			GozarConfiguration cyclonConfiguration,
			PingFailureDetectorConfiguration fdConfiguration) {
		super();
		this.peerSelf = peerSelf;
		this.peerType = peerType;
		this.bootstrapConfiguration = bootstrapConfiguration;
		this.gozarConfiguration = cyclonConfiguration;
		this.peerConfiguration = peerConfiguration;
		this.fdConfiguration = fdConfiguration;
	}

//-------------------------------------------------------------------
	public PeerAddress getPeerSelf() {
		return peerSelf;
	}

//-------------------------------------------------------------------
	public PeerType getPeerType() {
		return this.peerType;
	}

//-------------------------------------------------------------------
	public BootstrapConfiguration getBootstrapConfiguration() {
		return bootstrapConfiguration;
	}

//-------------------------------------------------------------------
	public GozarConfiguration getGozarConfiguration() {
		return gozarConfiguration;
	}

//-------------------------------------------------------------------
	public PeerConfiguration getPeerConfiguration() {
		return peerConfiguration; 
	}
	
//-------------------------------------------------------------------
	public PingFailureDetectorConfiguration getFdConfiguration() {
		return fdConfiguration;
	}

}
