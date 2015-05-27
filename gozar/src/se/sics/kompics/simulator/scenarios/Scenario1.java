package se.sics.kompics.simulator.scenarios;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;
import se.sics.kompics.simulator.core.PeerType;

@SuppressWarnings("serial")
public class Scenario1 extends Scenario {
	private static SimulationScenario scenario = new SimulationScenario() {{
		
		StochasticProcess process1 = new StochasticProcess() {{
			eventInterArrivalTime(constant(100));
			raise(1, Operations.gozarPeerJoin(PeerType.OPEN), uniform(13));
		}};

		StochasticProcess process2 = new StochasticProcess() {{
			eventInterArrivalTime(constant(100));
			raise(1, Operations.gozarPeerJoin(PeerType.OPEN), uniform(13));
		}};

		StochasticProcess process3 = new StochasticProcess() {{
			eventInterArrivalTime(exponential(10));
			raise(400, Operations.gozarPeerJoin(PeerType.RC), uniform(13));
			raise(320, Operations.gozarPeerJoin(PeerType.PRC), uniform(13));
			raise(80, Operations.gozarPeerJoin(PeerType.SYM), uniform(13));
			raise(200, Operations.gozarPeerJoin(PeerType.OPEN), uniform(13));
		}};
		
		process1.start();
		process2.startAfterTerminationOf(2000, process1);
		process3.startAfterTerminationOf(4000, process2);
	}};
	
//-------------------------------------------------------------------
	public Scenario1() {
		super(scenario);
	} 
}
