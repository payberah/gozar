package se.sics.kompics.system.gozar;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class InitiateShuffle extends Timeout {

	public InitiateShuffle(SchedulePeriodicTimeout request) {
		super(request);
	}

//-------------------------------------------------------------------	
	public InitiateShuffle(ScheduleTimeout request) {
		super(request);
	}
}
