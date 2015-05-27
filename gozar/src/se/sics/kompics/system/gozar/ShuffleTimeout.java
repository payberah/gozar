package se.sics.kompics.system.gozar;

import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class ShuffleTimeout extends Timeout {

	private final PeerAddress peerAddress;

//-------------------------------------------------------------------	
	public ShuffleTimeout(ScheduleTimeout request, PeerAddress peerAddress) {
		super(request);
		this.peerAddress = peerAddress;
	}

//-------------------------------------------------------------------	
	public PeerAddress getPeerAddress() {
		return peerAddress;
	}
}
