package se.sics.kompics.system.peer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public final class PeerConfiguration {

//-------------------------------------------------------------------	
	public PeerConfiguration() {
		super();
	}

//-------------------------------------------------------------------	
	public void store(String file) throws IOException {
		Properties p = new Properties();
		
		Writer writer = new FileWriter(file);
		p.store(writer, "se.sics.kompics.system.peer");
	}

//-------------------------------------------------------------------	
	public static PeerConfiguration load(String file) throws IOException {
		Properties p = new Properties();
		Reader reader = new FileReader(file);
		p.load(reader);

		return new PeerConfiguration();
	}
}
