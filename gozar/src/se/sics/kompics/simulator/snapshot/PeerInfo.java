package se.sics.kompics.simulator.snapshot;

import java.util.ArrayList;

import se.sics.kompics.simulator.core.PeerType;
import se.sics.kompics.system.common.PeerAddress;
import se.sics.kompics.system.gozar.GozarDescriptor;

public class PeerInfo {
	private final PeerType peerType;
	private int totalLoad;
	private int relayLoad;
	private int selectedTimes;
	private int registerdNodes;
	private int totalRegisterdNodes = 0;
	private ArrayList<GozarDescriptor> partners;
	
//-------------------------------------------------------------------
	public PeerInfo(PeerType peerType) {
		this.relayLoad = 0;
		this.selectedTimes = 0;
		this.peerType = peerType;
	}

//-------------------------------------------------------------------
	public void updatePartners(ArrayList<GozarDescriptor> partners) {
		this.partners = partners;
	}

//-------------------------------------------------------------------
	public int getRegisterdNodes() {
		return this.registerdNodes;
	}

//-------------------------------------------------------------------
	public int getTotalRegisterdNodes() {
		return this.totalRegisterdNodes;
	}

//-------------------------------------------------------------------
	public void updateRegisterdNodes(int registeredNodes) {
		this.registerdNodes = registeredNodes;
		
		if (this.totalRegisterdNodes == 0)
			this.totalRegisterdNodes = registeredNodes;
		else
			this.totalRegisterdNodes = (this.totalRegisterdNodes + registeredNodes) / 2;
	}

//-------------------------------------------------------------------
	public void incRelayLoad(int size) {
		this.relayLoad += size;
	}

//-------------------------------------------------------------------
	public void incTotalLoad(int size) {
		this.totalLoad += size;
	}

//-------------------------------------------------------------------
	public int getRelayLoad() {
		return this.relayLoad;
	}

//-------------------------------------------------------------------
	public int getTotalLoad() {
		return this.totalLoad;
	}

//-------------------------------------------------------------------
	public void incSelectedTimes() {
		this.selectedTimes++;
	}

//-------------------------------------------------------------------
	public int getSelectedTimes() {
		return this.selectedTimes;
	}

//-------------------------------------------------------------------
	public PeerType getPeerType() {
		return this.peerType;
	}

//-------------------------------------------------------------------
	public ArrayList<GozarDescriptor> getPartners() {
		return this.partners;
	}

//-------------------------------------------------------------------
	public boolean isPartner(PeerAddress peer) {
		for (GozarDescriptor desc : this.partners) {
			if (desc.getPeerAddress().equals(peer))
				return true;
		}
		
		return false;
	}

}
