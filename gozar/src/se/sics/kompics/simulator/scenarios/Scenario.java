package se.sics.kompics.simulator.scenarios;

import java.util.Random;

import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;
import se.sics.kompics.simulator.core.GozarExecutionMain;
import se.sics.kompics.simulator.core.GozarSimulationMain;

public class Scenario {
	private static Random random;
	protected SimulationScenario scenario;

//-------------------------------------------------------------------
	public Scenario(SimulationScenario scenario) {
		this.scenario = scenario;
		this.scenario.setSeed(System.currentTimeMillis());
		random = scenario.getRandom();
	}

//-------------------------------------------------------------------
	public void setSeed(long seed) {
		this.scenario.setSeed(seed);
	}

//-------------------------------------------------------------------
	public void execute() {
		this.scenario.execute(GozarExecutionMain.class);
	}

//-------------------------------------------------------------------
	public void simulate() {
		this.scenario.simulate(GozarSimulationMain.class);
	}

//-------------------------------------------------------------------
	public static Random getRandom() {
		return random;
	}

//-------------------------------------------------------------------
	public static void setRandom(Random r) {
		random = r;
	}
}
