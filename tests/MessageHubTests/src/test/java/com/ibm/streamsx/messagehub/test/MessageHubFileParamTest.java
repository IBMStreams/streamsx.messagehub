package com.ibm.streamsx.messagehub.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    private static final String TEST_NAME = "MessageHubFileParamTest";
    private static final String MESSAGEHUB_CREDS_FILE_PATH = "etc/userfile.json";

    public MessageHubFileParamTest() throws Exception {
        super(TEST_NAME);
    }

    @Before
    public void setup() throws Exception {
        Files.copy (Paths.get ("etc/messagehub.json"), Paths.get (MESSAGEHUB_CREDS_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
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
        TStream<String> stringSrcStream = topo.strings(Constants.STRING_DATA).modify(new Delay<>(10000));
        SPL.invokeSink(com.ibm.streamsx.messagehub.test.utils.Constants.MessageHubProducerOp, 
                MessageHubSPLStreamsUtils.convertStreamToMessageHubTuple(stringSrcStream), 
                getKafkaParams());

        // create the consumer
        SPLStream consumerStream = SPL.invokeSource(topo, Constants.MessageHubConsumerOp, getKafkaParams(), MessageHubSPLStreamsUtils.STRING_SCHEMA);
        SPLStream msgStream = SPLStreams.stringToSPLStream(consumerStream.convert(t -> t.getString("message")));

        // test the output of the consumer
        StreamsContext<?> context = StreamsContextFactory.getStreamsContext(Type.DISTRIBUTED_TESTER);
        Tester tester = topo.getTester();
        Condition<List<String>> stringContentsUnordered = tester.stringContentsUnordered (msgStream.toStringStream(), Constants.STRING_DATA);
        HashMap<String, Object> config = new HashMap<>();
//        config.put (ContextProperties.TRACING_LEVEL, java.util.logging.Level.FINE);
//        config.put(ContextProperties.KEEP_ARTIFACTS,  new Boolean(true));
        tester.complete(context, config, stringContentsUnordered, 60, TimeUnit.SECONDS);

        // check the results
        Assert.assertTrue (stringContentsUnordered.valid());
        Assert.assertTrue (stringContentsUnordered.getResult().size() == 10);
    }

    private Map<String, Object> getKafkaParams() {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("topic", Constants.TOPIC_TEST);
        params.put("credentialsFile", MESSAGEHUB_CREDS_FILE_PATH);

        return params;
    }
}

