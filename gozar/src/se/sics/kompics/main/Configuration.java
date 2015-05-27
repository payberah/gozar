package se.sics.kompics.main;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import se.sics.kompics.address.Address;
import se.sics.kompics.network.NetworkConfiguration;
import se.sics.kompics.network.Transport;
import se.sics.kompics.p2p.bootstrap.BootstrapConfiguration;
import se.sics.kompics.p2p.fd.ping.PingFailureDetectorConfiguration;
import se.sics.kompics.system.gozar.GozarConfiguration;
import se.sics.kompics.system.peer.PeerConfiguration;

public class Configuration {
	public static int SNAPSHOT_PERIOD = 5000;
	
	public InetAddress ip = null;
	{
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
	}
	int networkPort = 8081;
	int webPort = 8080;
	int bootId = Integer.MAX_VALUE;

	Address bootServerAddress = new Address(ip, networkPort, bootId);
	Address peer0Address = new Address(ip, networkPort, 0);

	BootstrapConfiguration bootConfiguration = new BootstrapConfiguration(bootServerAddress, 60000, 4000, 3, 30000, webPort, webPort);
	PingFailureDetectorConfiguration fdConfiguration = new PingFailureDetectorConfiguration(10000, 50000, 5000, 1000, Transport.TCP);
	GozarConfiguration gozarConfiguration = new GozarConfiguration(5, 10, 1000, 200000, new BigInteger("2").pow(13), 10);
	PeerConfiguration peerConfiguration = new PeerConfiguration();
	NetworkConfiguration networkConfiguration = new NetworkConfiguration(ip, networkPort, 0);

	public void set() throws IOException {
		String c = File.createTempFile("bootstrap.", ".conf").getAbsolutePath();
		bootConfiguration.store(c);
		System.setProperty("bootstrap.configuration", c);

		c = File.createTempFile("gozar.", ".conf").getAbsolutePath();
		gozarConfiguration.store(c);
		System.setProperty("gozar.configuration", c);

		c = File.createTempFile("ping.fd.", ".conf").getAbsolutePath();
		fdConfiguration.store(c);
		System.setProperty("ping.fd.configuration", c);
		
		c = File.createTempFile("peer.", ".conf").getAbsolutePath();
		peerConfiguration.store(c);
		System.setProperty("peer.configuration", c);

		c = File.createTempFile("network.", ".conf").getAbsolutePath();
		networkConfiguration.store(c);
		System.setProperty("network.configuration", c);
	}
}
