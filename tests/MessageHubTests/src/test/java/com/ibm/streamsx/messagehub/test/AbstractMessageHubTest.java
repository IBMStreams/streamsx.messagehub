package com.ibm.streamsx.messagehub.test;

import java.io.File;

import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.spl.SPL;

abstract class AbstractMessageHubTest {
	private Topology topo;
	
	public AbstractMessageHubTest(String testName) throws Exception {
		topo = createTopology(testName);
	}
	
	protected Topology createTopology(String testName) throws Exception {
		Topology t = new Topology(testName);
		SPL.addToolkit(t, new File("../../com.ibm.streamsx.messagehub"));		
		return t;
	}
	
	public Topology getTopology() {
		return topo;
	}
}
