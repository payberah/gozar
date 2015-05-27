package se.sics.kompics.system.gozar;

import java.util.HashSet;

import se.sics.kompics.system.common.PeerAddress;

public class ViewEntry {

	private final GozarDescriptor descriptor;
	private final long addedAt;
	private long sentAt;
	private HashSet<PeerAddress> sentTo;

//-------------------------------------------------------------------	
	public ViewEntry(GozarDescriptor descriptor) {
		this.descriptor = descriptor;
		this.addedAt = System.currentTimeMillis();
		this.sentAt = 0;
		this.sentTo = null;
	}

//-------------------------------------------------------------------	
	public boolean isEmpty() {
		return descriptor == null;
	}

//-------------------------------------------------------------------	
	public void sentTo(PeerAddress peer) {
		if (sentTo == null)
			sentTo = new HashSet<PeerAddress>();
		
		sentTo.add(peer);
		sentAt = System.currentTimeMillis();
	}

//-------------------------------------------------------------------	
	public GozarDescriptor getDescriptor() {
		return descriptor;
	}

//-------------------------------------------------------------------	
	public long getAddedAt() {
		return addedAt;
	}

//-------------------------------------------------------------------	
	public long getSentAt() {
		return sentAt;
	}

//-------------------------------------------------------------------	
	public boolean wasSentTo(PeerAddress peer) {
		return sentTo == null ? false : sentTo.contains(peer);
	}

//-------------------------------------------------------------------	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descriptor == null) ? 0 : descriptor.hashCode());
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
		ViewEntry other = (ViewEntry) obj;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		return true;
	}
}
