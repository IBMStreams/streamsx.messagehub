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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
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

public class MessageHubLegacyDefaultFileTest extends AbstractMessageHubTest {

    private static final String TEST_NAME = "MessageHubLegacyDefaultFileTest";

    public MessageHubLegacyDefaultFileTest() throws Exception {
        super(TEST_NAME);
    }

    @Test
    public void messageHubLegacyDefaultFileTest() throws Exception {
        Topology topo = getTopology();
        topo.addFileDependency("etc/messagehub.json", "etc");

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

        return params;
    }
}

