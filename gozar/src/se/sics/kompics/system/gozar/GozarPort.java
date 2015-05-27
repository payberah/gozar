package se.sics.kompics.system.gozar;

import se.sics.kompics.PortType;

public final class GozarPort extends PortType {
	{
		negative(Join.class);
		positive(JoinCompleted.class);
	}
}
