package se.sics.kompics.simulator.core;

import java.util.TreeMap;

public class ConsistentHashtable<K> {

	private final TreeMap<K, K> buckets = new TreeMap<K, K>();

//-------------------------------------------------------------------	
	public ConsistentHashtable() {
		super();
	}

//-------------------------------------------------------------------	
	public K addNode(K node) {
		return buckets.put(node, node);
	}

//-------------------------------------------------------------------	
	public K removeNode(K node) {
		return buckets.remove(node);
	}

//-------------------------------------------------------------------	
	public K getNode(K key) {

		if (buckets.isEmpty())
			return null;
		
		if (!buckets.containsKey(key)) {
			// returns the first key greater than or equal to supplied key,
			// or null if no such key.
			key = buckets.ceilingKey(key);
			// if no key found, go to start and return first key in the ring.
			if (key == null)
				key = buckets.firstKey();			
		}
		
		return buckets.get(key);
	}

//-------------------------------------------------------------------	
	public int size() {
		return buckets.size();
	}
}
