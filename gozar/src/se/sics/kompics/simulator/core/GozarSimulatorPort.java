package se.sics.kompics.simulator.core;

import se.sics.kompics.PortType;
import se.sics.kompics.p2p.experiment.dsl.events.TerminateExperiment;

public class GozarSimulatorPort extends PortType {
	{
		positive(PeerJoin.class);
		positive(PeerFail.class);
		negative(TerminateExperiment.class);
	}
}
