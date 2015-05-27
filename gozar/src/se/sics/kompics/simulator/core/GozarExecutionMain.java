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
import se.sics.kompics.p2p.orchestrator.P2pOrchestrator;
import se.sics.kompics.p2p.orchestrator.P2pOrchestratorInit;
import se.sics.kompics.system.gozar.GozarConfiguration;
import se.sics.kompics.system.peer.PeerConfiguration;
import se.sics.kompics.timer.Timer;
import se.sics.kompics.web.Web;
import se.sics.kompics.web.WebRequest;
import se.sics.kompics.web.jetty.JettyWebServer;
import se.sics.kompics.web.jetty.JettyWebServerConfiguration;
import se.sics.kompics.web.jetty.JettyWebServerInit;

public final class GozarExecutionMain extends ComponentDefinition {
	static {
		PropertyConfigurator.configureAndWatch("log4j.properties");
	}
	private static SimulationScenario scenario = SimulationScenario.load(System.getProperty("scenario"));

//-------------------------------------------------------------------	

	public static void main(String[] args) {
		Kompics.createAndStart(GozarExecutionMain.class, 8);
	}

//-------------------------------------------------------------------	
	public GozarExecutionMain() throws InterruptedException, IOException {
		P2pOrchestrator.setSimulationPortType(GozarSimulatorPort.class);
		// create
		Component p2pOrchestrator = create(P2pOrchestrator.class);
		Component jettyWebServer = create(JettyWebServer.class);
		Component bootstrapServer = create(BootstrapServer.class);
		Component gozarSimulator = create(GozarSimulator.class);

		// loading component configurations
		final BootstrapConfiguration bootConfiguration = BootstrapConfiguration.load(System.getProperty("bootstrap.configuration"));
		final GozarConfiguration gozarConfiguration = GozarConfiguration.load(System.getProperty("gozar.configuration"));
		final JettyWebServerConfiguration webConfiguration = JettyWebServerConfiguration.load(System.getProperty("jetty.web.configuration"));
		final NetworkConfiguration networkConfiguration = NetworkConfiguration.load(System.getProperty("network.configuration"));
		final PeerConfiguration peerConfiguration = PeerConfiguration.load(System.getProperty("peer.configuration"));
		final PingFailureDetectorConfiguration fdConfiguration = PingFailureDetectorConfiguration.load(System.getProperty("ping.fd.configuration"));

		System.out.println("For web access please go to " + "http://" + webConfiguration.getIp().getHostAddress() + ":" + webConfiguration.getPort() + "/");
		Thread.sleep(2000);

		trigger(new P2pOrchestratorInit(scenario, new KingLatencyMap()), p2pOrchestrator.getControl());
		trigger(new JettyWebServerInit(webConfiguration), jettyWebServer.getControl());
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
		
		final class WebRequestDestinationFilter extends ChannelFilter<WebRequest, Integer> {
			public WebRequestDestinationFilter(Integer destination) {
				super(WebRequest.class, destination, false);
			}

			public Integer getValue(WebRequest event) {
				return event.getDestination();
			}
		}

		// connect
		connect(bootstrapServer.getNegative(Network.class), p2pOrchestrator.getPositive(Network.class), new MessageDestinationFilter(bootConfiguration.getBootstrapServerAddress()));
		connect(bootstrapServer.getNegative(Timer.class), p2pOrchestrator.getPositive(Timer.class));
		connect(bootstrapServer.getPositive(Web.class), jettyWebServer.getNegative(Web.class), new WebRequestDestinationFilter(bootConfiguration.getBootstrapServerAddress().getId()));
		connect(gozarSimulator.getNegative(Network.class), p2pOrchestrator.getPositive(Network.class));
		connect(gozarSimulator.getNegative(Timer.class), p2pOrchestrator.getPositive(Timer.class));
		connect(gozarSimulator.getPositive(Web.class), jettyWebServer.getNegative(Web.class));
		connect(gozarSimulator.getNegative(GozarSimulatorPort.class), p2pOrchestrator.getPositive(GozarSimulatorPort.class));
	}
}
