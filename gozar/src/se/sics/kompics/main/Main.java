package se.sics.kompics.main;

import se.sics.kompics.simulator.scenarios.Scenario;
import se.sics.kompics.simulator.scenarios.Scenario1;

public class Main {
	public static void main(String[] args) throws Throwable {
		//long seed = Long.parseLong(args[0]);

		Configuration configuration = new Configuration();
		configuration.set();
		
		Scenario scenario = new Scenario1();
		scenario.setSeed(System.currentTimeMillis());
		scenario.simulate();
	}
}
