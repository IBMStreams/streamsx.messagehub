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
package com.ibm.streamsx.messagehub.test.utils;

public class Constants {

    public static final String TOPIC_TEST = "test";
    public static final String MessageHubConsumerOp = "com.ibm.streamsx.messagehub::MessageHubConsumer";
    public static final String MessageHubProducerOp = "com.ibm.streamsx.messagehub::MessageHubProducer";	
    public static final Long PRODUCER_DELAY = 5000l;
    public static final String[] STRING_DATA = {"dog", "cat", "bird", "fish", "lion", "wolf", "elephant", "zebra", "monkey", "bat"};

}
