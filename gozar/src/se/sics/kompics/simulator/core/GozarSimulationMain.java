package se.sics.kompics.simulator.core;

import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;

import se.sics.kompics.ChannelFilter;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Kompics;
import se.sics.kompics.address.Address;
import se.sics.kompics.network.Message;
import se.sics.kompics.network.Network;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.model.king.KingLatencyMap;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.bootstrap.server.BootstrapServer;
import se.sics.kompics.p2p.bootstrap.server.BootstrapServerInit;
import se.sics.kompics.p2p.experiment.dsl.SimulationScenario;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.p2p.simulator.P2pSimulator;
import se.sics.kompics.p2p.simulator.P2pSimulatorInit;
import se.sics.kompics.simulation.SimulatorScheduler;
import se.sics.kompics.system.gozar.GozarConfiguration;
import se.sics.kompics.system.peer.PeerConfiguration;
import se.sics.kompics.timer.Timer;

public final class GozarSimulationMain extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static SimulatorScheduler simulatorScheduler = new SimulatorScheduler();
	private static SimulationScenario scenario = SimulationScenario.load(System.getProperty("scenario"));

//-------------------------------------------------------------------	
	public static void main(String[] args) {
		Kompics.setScheduler(simulatorScheduler);
		Kompics.createAndStart(GozarSimulationMain.class, 1);
	}

//-------------------------------------------------------------------	
	public GozarSimulationMain() throws IOException {
		P2pSimulator.setSimulationPortType(GozarSimulatorPort.class);

		// create
		Component p2pSimulator = create(P2pSimulator.class);
		Component bootstrapServer = create(BootstrapServer.class);
		Component gozarSimulator = create(GozarSimulator.class);

		// loading component configurations
		final BootstrapConfiguration bootConfiguration = BootstrapConfiguration.load(System.getProperty("bootstrap.configuration"));
		final GozarConfiguration gozarConfiguration = GozarConfiguration.load(System.getProperty("gozar.configuration"));
		final PeerConfiguration peerConfiguration = PeerConfiguration.load(System.getProperty("peer.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration.load(System.getProperty("network.configuration"));
		final PingFailureDetectorConfiguration fdConfiguration = PingFailureDetectorConfiguration.load(System.getProperty("ping.fd.configuration"));

		trigger(new P2pSimulatorInit(simulatorScheduler, scenario, new KingLatencyMap()), p2pSimulator.getControl());
		trigger(new BootstrapServerInit(bootConfiguration), bootstrapServer.getControl());
		trigger(new GozarSimulatorInit(peerConfiguration, bootConfiguration, gozarConfiguration, fdConfiguration, networkConfiguration.getAddress()), gozarSimulator.getControl());

		final class MessageDestinationFilter extends ChannelFilter<Message, Address> {
			public MessageDestinationFilter(Address address) {
				super(Message.class, address, true);
			}

			public Address getValue(Message event) {
				return event.getDestination();
			}
		}

		// connect
		connect(bootstrapServer.getNegative(Network.class), p2pSimulator.getPositive(Network.class), new MessageDestinationFilter(bootConfiguration.getBootstrapServerAddress()));
		connect(bootstrapServer.getNegative(Timer.class), p2pSimulator.getPositive(Timer.class));
		connect(gozarSimulator.getNegative(Network.class), p2pSimulator.getPositive(Network.class));
		connect(gozarSimulator.getNegative(Timer.class), p2pSimulator.getPositive(Timer.class));
		connect(gozarSimulator.getNegative(GozarSimulatorPort.class), p2pSimulator.getPositive(GozarSimulatorPort.class));
	}
}
