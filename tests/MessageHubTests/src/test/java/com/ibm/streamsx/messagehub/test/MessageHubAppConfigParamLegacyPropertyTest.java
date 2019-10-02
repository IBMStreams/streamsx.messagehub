/*
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ibm.streamsx.messagehub.test;

import java.nio.file.Files;
import java.nio.file.Paths;
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

public class MessageHubAppConfigParamLegacyPropertyTest extends AbstractMessageHubTest {

    private static final String TEST_NAME = "MessageHubAppConfigParamLegacyPropertyTest";
    private static final String APPCONFIG_NAME = "userAppConfig2";
    private static final String APPCONFIG_PROP_NAME = "messagehub.creds";

    public MessageHubAppConfigParamLegacyPropertyTest() throws Exception {
        super(TEST_NAME);
    }

    @Before
    public void setup() throws Exception {
        String creds = new String(Files.readAllBytes(Paths.get("etc/messagehub.json")));
        creds = APPCONFIG_PROP_NAME + "=" + creds.replace("=", "&#61;");

        ProcessBuilder pb = new ProcessBuilder(System.getenv("STREAMS_INSTALL") + "/bin/streamtool", "rmappconfig", "--noprompt", APPCONFIG_NAME);
        pb.inheritIO();
        Process p = pb.start();
        p.waitFor(25, TimeUnit.SECONDS);

        pb = new ProcessBuilder(System.getenv("STREAMS_INSTALL") + "/bin/streamtool", "mkappconfig", "--property", creds, APPCONFIG_NAME);
        pb.inheritIO();
        p = pb.start();
        p.waitFor(25, TimeUnit.SECONDS);

        if(p.exitValue() != 0) {
            System.out.println(p.exitValue());
            Assert.fail("Creating app config failed! Test cancelled!");
        }
    }

    @After
    public void cleanup() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(System.getenv("STREAMS_INSTALL") + "/bin/streamtool", "rmappconfig", "--noprompt", APPCONFIG_NAME);
        pb.inheritIO();
        Process p = pb.start();
        Thread.sleep(5000);
        p.waitFor(25, TimeUnit.SECONDS);
        if(p.exitValue() != 0) {
            System.out.println(p.exitValue());
            Assert.fail("Removing appconfig failed. App config must be removed manually!");
        }
    }

    @Test
    public void messageHubAppConfigParamLegacyPropertyTest() throws Exception {
        Topology topo = getTopology();

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
        params.put("appConfigName", APPCONFIG_NAME);

        return params;
    }
}

