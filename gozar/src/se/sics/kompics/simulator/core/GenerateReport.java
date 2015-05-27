package se.sics.kompics.simulator.core;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class GenerateReport extends Timeout {

	public GenerateReport(SchedulePeriodicTimeout request) {
		super(request);
	}

//-------------------------------------------------------------------	
	public GenerateReport(ScheduleTimeout request) {
		super(request);
	}
}
