package se.sics.kompics.simulator.core;

import se.sics.kompics.network.Message;

public class ReceivedMessage implements Comparable<ReceivedMessage> {

	private final Class<? extends Message> messageType;
	private int totalCount;

//-------------------------------------------------------------------	
	public ReceivedMessage(Class<? extends Message> messageType) {
		this.messageType = messageType;
		this.totalCount = 0;
	}

//-------------------------------------------------------------------	
	public ReceivedMessage(Class<? extends Message> messageType, int totalCount) {
		super();
		this.messageType = messageType;
		this.totalCount = totalCount;
	}

//-------------------------------------------------------------------	
	public Class<? extends Message> getMessageType() {
		return messageType;
	}

//-------------------------------------------------------------------	
	public void incrementCount() {
		totalCount++;
	}
	
//-------------------------------------------------------------------	
	public int getTotalCount() {
		return totalCount;
	}

//-------------------------------------------------------------------	
	@Override
	public int compareTo(ReceivedMessage that) {
		if (this.totalCount < that.totalCount)
			return 1;
		if (this.totalCount > that.totalCount)
			return -1;
		return 0;
	}

//-------------------------------------------------------------------	
	@Override
	public String toString() {
		return totalCount + " " + messageType.getSimpleName();
	}
}
