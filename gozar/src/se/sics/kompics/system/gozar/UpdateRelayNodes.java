package se.sics.kompics.system.gozar;

import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

public class UpdateRelayNodes extends Timeout {

	public UpdateRelayNodes(SchedulePeriodicTimeout request) {
		super(request);
	}

//-------------------------------------------------------------------	
	public UpdateRelayNodes(ScheduleTimeout request) {
		super(request);
	}
}
