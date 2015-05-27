package se.sics.kompics.system.gozar;

import java.io.Serializable;
import java.util.HashMap;

import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.system.common.PeerAddress;

public class GozarDescriptor implements Comparable<GozarDescriptor>, Serializable {
	private static final long serialVersionUID = 1906679375438244117L;
	private final PeerAddress peerAddress;
	private final PeerType peerType;
	private int age;
	private HashMap<PeerAddress, Integer> relayNodes;
	
//-------------------------------------------------------------------
	public GozarDescriptor(PeerAddress peerAddress, PeerType peerType, HashMap<PeerAddress, Integer> relayNodes) {
		this.peerAddress = peerAddress;
		this.peerType = peerType;
		this.age = 0;
		this.relayNodes = relayNodes;
	}

//-------------------------------------------------------------------
	public int incrementAndGetAge() {
		this.age++;
		return this.age;
	}

//-------------------------------------------------------------------
	public int getAge() {
		return this.age;
	}

//-------------------------------------------------------------------
	public PeerAddress getPeerAddress() {
		return this.peerAddress;
	}

//-------------------------------------------------------------------
	public PeerType getPeerType() {
		return this.peerType;
	}

//-------------------------------------------------------------------
	public HashMap<PeerAddress, Integer> getRelayNodes() {
		return this.relayNodes;
	}

//-------------------------------------------------------------------
	public int compareTo(GozarDescriptor that) {
		if (this.age > that.age)
			return 1;
		if (this.age < that.age)
			return -1;
		return 0;
	}

//-------------------------------------------------------------------
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((peerAddress == null) ? 0 : peerAddress.hashCode());
		return result;
	}

//-------------------------------------------------------------------
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GozarDescriptor other = (GozarDescriptor) obj;
		if (peerAddress == null) {
			if (other.peerAddress != null)
				return false;
		} else if (!peerAddress.equals(other.peerAddress))
			return false;
		return true;
	}

//-------------------------------------------------------------------
	@Override
	public String toString() {
		//return "(" + msPeerAddress + ", " + peerType +  ", " + relayNodes + ")";
		return peerAddress.toString();
	}
}
