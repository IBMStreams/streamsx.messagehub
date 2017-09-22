package com.ibm.streamsx.messagehub.test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.ibm.streamsx.messagehub.test.utils.Constants;
import com.ibm.streamsx.messagehub.test.utils.Delay;
import com.ibm.streamsx.messagehub.test.utils.MessageHubSPLStreamsUtils;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.Topology;
import com.ibm.streamsx.topology.context.StreamsContext;
import com.ibm.streamsx.topology.context.StreamsContext.Type;
import com.ibm.streamsx.topology.context.StreamsContextFactory;
import com.ibm.streamsx.topology.spl.SPL;
import com.ibm.streamsx.topology.spl.SPLStream;
import com.ibm.streamsx.topology.spl.SPLStreams;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.tester.Tester;

public class MessageHubFileParamTest extends AbstractMessageHubTest {

	private static final String TEST_NAME = "KafkaOperatorsGreenThread";
	private static final String MESSAGEHUB_CREDS_FILE_PATH = "etc/userfile.json";
	
	public MessageHubFileParamTest() throws Exception {
		super(TEST_NAME);
	}
	
	@Before
	public void setup() throws Exception {
		Files.copy(new File("etc/messagehub.json"), new File(MESSAGEHUB_CREDS_FILE_PATH));
	}

	@After
	public void cleanup() throws Exception {
		(new File(MESSAGEHUB_CREDS_FILE_PATH)).deleteOnExit();
	}
	
	@Test
	public void messageHubFileParamTest() throws Exception {
		Topology topo = getTopology();
		topo.addFileDependency(MESSAGEHUB_CREDS_FILE_PATH, "etc");
		
		// create the producer (produces tuples after a short delay)
		TStream<String> stringSrcStream = topo.strings(Constants.STRING_DATA).modify(new Delay<>(5000));
		SPL.invokeSink(com.ibm.streamsx.messagehub.test.utils.Constants.MessageHubProducerOp, 
				MessageHubSPLStreamsUtils.convertStreamToMessageHubTupleTuple(stringSrcStream), 
				getKafkaParams());

		// create the consumer
		SPLStream consumerStream = SPL.invokeSource(topo, Constants.MessageHubConsumerOp, getKafkaParams(), MessageHubSPLStreamsUtils.STRING_SCHEMA);
		SPLStream msgStream = SPLStreams.stringToSPLStream(consumerStream.convert(t -> t.getString("message")));
		
		// test the output of the consumer
		StreamsContext<?> context = StreamsContextFactory.getStreamsContext(Type.DISTRIBUTED_TESTER);
		Tester tester = topo.getTester();
		Condition<List<String>> condition = MessageHubSPLStreamsUtils.stringContentsUnordered(tester, msgStream, Constants.STRING_DATA);
		tester.complete(context, new HashMap<>(), condition, 30, TimeUnit.SECONDS);

		// check the results
		Assert.assertTrue(condition.getResult().size() > 0);
		Assert.assertTrue(condition.getResult().toString(), condition.valid());		
	}
	
	private Map<String, Object> getKafkaParams() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("topic", Constants.TOPIC_TEST);
		params.put("messageHubCredentialsFile", MESSAGEHUB_CREDS_FILE_PATH);
		
		return params;
	}
}
	
