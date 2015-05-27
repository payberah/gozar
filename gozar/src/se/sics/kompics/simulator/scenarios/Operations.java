package se.sics.kompics.simulator.scenarios;

import java.math.BigInteger;

import se.sics.kompics.p2p.experiment.dsl.adaptor.Operation1;
import se.sics.kompics.simulator.core.PeerFail;
import se.sics.kompics.simulator.core.PeerJoin;
import se.sics.kompics.simulator.core.PeerType;

@SuppressWarnings("serial")
public class Operations {

//-------------------------------------------------------------------
	static Operation1<PeerJoin, BigInteger>  gozarPeerJoin(final PeerType peerType) {
		return new Operation1<PeerJoin, BigInteger>() {
			public PeerJoin generate(BigInteger id) {
				return new PeerJoin(id, peerType);
			}
		};
	}
	
//-------------------------------------------------------------------
	static Operation1<PeerFail, BigInteger> gozarPeerFail = new Operation1<PeerFail, BigInteger>() {
		public PeerFail generate(BigInteger id) {
			return new PeerFail(id);
		}
	};
}
