package com.ibm.streamsx.messagehub.test.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.streams.flow.handlers.StreamCollector;
import com.ibm.streams.operator.OutputTuple;
import com.ibm.streams.operator.StreamSchema;
import com.ibm.streams.operator.Tuple;
import com.ibm.streams.operator.Type;
import com.ibm.streamsx.topology.TStream;
import com.ibm.streamsx.topology.function.BiFunction;
import com.ibm.streamsx.topology.spl.SPLStream;
import com.ibm.streamsx.topology.spl.SPLStreams;
import com.ibm.streamsx.topology.tester.Condition;
import com.ibm.streamsx.topology.tester.Tester;

public class MessageHubSPLStreamsUtils {

	public static final StreamSchema STRING_SCHEMA = Type.Factory.getStreamSchema("tuple<rstring key, rstring message>");

	public static SPLStream convertStreamToMessageHubTupleTuple(TStream<String> stream) {
		return SPLStreams.convertStream(stream, MessageHubSPLStreamsUtils.getStringBiFunction(), MessageHubSPLStreamsUtils.STRING_SCHEMA);
	}
	
	public static BiFunction<String, OutputTuple, OutputTuple> getStringBiFunction() {
		return new BiFunction<String, OutputTuple, OutputTuple>() {
			private static final long serialVersionUID = 1L;
			private int counter = 0;
			
			@Override
			public OutputTuple apply(String message, OutputTuple outTuple) {
				outTuple.setString("key", "key_" + counter++);
				outTuple.setString("message", message);
				
				return outTuple;
			}
		};
	}
	
	public static SPLStream union(List<SPLStream> streams, StreamSchema schema) {
		if(streams.size() == 0)
			throw new IllegalArgumentException("At least one stream must be provided.");
		
		if(streams.size() == 1) {
			return streams.get(0);
		} else {
			SPLStream stream1 = streams.get(0);
			Set<TStream<Tuple>> streamSet = new HashSet<TStream<Tuple>>(streams);
			streamSet.remove(stream1);
			
			return SPLStreams.convertStream(stream1.union(streamSet), getTupleStreamConvert(), schema);
		}
	}
	
	private static BiFunction<Tuple, OutputTuple, OutputTuple> getTupleStreamConvert() {
		return new BiFunction<Tuple, OutputTuple, OutputTuple>() {
			private static final long serialVersionUID = 1L;

			@Override
			public OutputTuple apply(Tuple v1, OutputTuple v2) {
				v2.assign(v1);
				return v2;
			}
		};
	}
	
    public static Condition<List<String>> stringContentsUnordered(Tester tester, SPLStream stream,
            String... values) {

        final List<String> sortedValues = Arrays.asList(values);
        Collections.sort(sortedValues);

        final StreamCollector<LinkedList<Tuple>, Tuple> tuples = StreamCollector
                .newLinkedListCollector();

        tester.splHandler(stream, tuples);

        return new Condition<List<String>>() {
            
            @Override
            public List<String> getResult() {
                List<String> strings = new ArrayList<>(tuples.getTupleCount());
                synchronized (tuples.getTuples()) {
                    for (Tuple tuple : tuples.getTuples()) {
                        strings.add(tuple.getString(0));
                    }
                }
                return strings;
            }

            @Override
            public boolean valid() {
                List<String> strings =  getResult();
                if (strings.size() != sortedValues.size())
                    return false;
                Collections.sort(strings);
                return sortedValues.equals(strings);
            }

            @Override
            public String toString() {
                return "Received Tuples: " + getResult();
            }
        };
    }
    
    public static String[] duplicateArrayEntries(String[] inputArr, int numDuplicates) {
    	List<String> list = new ArrayList<String>();
    	for(String d : inputArr) {
    		for(int i = 0; i < numDuplicates; i++) {
    			list.add(d);
    		}
    	}
    	
    	return list.toArray(new String[0]);
    }
}
