package se.sics.kompics.system.common;

import java.math.BigInteger;

import se.sics.kompics.address.Address;
import se.sics.kompics.p2p.overlay.OverlayAddress;

public final class PeerAddress extends OverlayAddress implements Comparable<PeerAddress> {

	private static final long serialVersionUID = -7582889514221620065L;
	private final BigInteger peerId;

//-------------------------------------------------------------------	
	public PeerAddress(Address address, BigInteger peerId) {
		super(address);
		this.peerId = peerId;
	}

//-------------------------------------------------------------------	
	public BigInteger getPeerId() {
		return peerId;
	}

//-------------------------------------------------------------------	
	@Override
	public int compareTo(PeerAddress that) {
		return peerId.compareTo(that.peerId);
	}

//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return peerId.toString();
	}

//-------------------------------------------------------------------	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((peerId == null) ? 0 : peerId.hashCode());
		return result;
	}

//-------------------------------------------------------------------	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerAddress other = (PeerAddress) obj;
		if (peerId == null) {
			if (other.peerId != null)
				return false;
		} else if (!peerId.equals(other.peerId))
			return false;
		return true;
	}
}
