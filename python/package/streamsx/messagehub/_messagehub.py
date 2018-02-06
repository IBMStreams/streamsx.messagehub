# coding=utf-8
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2017,2018

import datetime

import streamsx.spl.op
import streamsx.spl.types
from streamsx.topology.schema import CommonSchema

def subscribe(topology, topic, schema, start=False, credentials=None, name=None):
    """Subscribe to messages from Message Hub for a topic.

    Adds a Message Hub consumer that subscribes to a topic
    and converts each message to a stream tuple.

    Starting position for reading messages from the topic is set by the `start` parameter.

    * When `start` evaluates to false messages are read from the end of the topic's queue.
    * When `start` is equal to `True` messages are read from the start of the topic's queue.
    * When `start` is an ``float`` or ``datetime.datetime`` the value is used as a timestamp and messages are read from the earliest offset whose timestamp is greater than or equal to the timestamp. For an ``float`` value it is taken as the number of seconds since the Unix 1970 epoch (for example a value from ``time.time()``.

    Args:
        topology(Topology): Topology that will contain the stream of messages.
        topic(str): Topic to subscribe messages from.
        schema(StreamSchema): Schema for returned stream.
        start: Where to start reading messages.
        credentials(str): Name of the application configuration containing the credentials for Message Hub. When set to ``None`` the application configuration ``messagehub`` is used.
        name(str): Consumer name in the Streams context, defaults to a generated name.

    Returns:
         Stream: Stream containing messages.
    """
    if schema is CommonSchema.Json:
        msg_attr_name='jsonString'
    elif schema is CommonSchema.String:
        msg_attr_name='string'
    else:
        raise TypeError(schema)

    start_pos = None
    start_time = None
    if start:
        if isinstance(start, datetime.datetime):
            start = start.timestamp()

        if start is True:
            start_pos = streamsx.spl.op.Expression.expression('Beginning')
        elif isinstance(start, float):
            start_pos = streamsx.spl.op.Expression.expression('Time')
            start_time = streamsx.spl.types.int64(int(start * 1000.0))
        else:
            raise TypeError(type(start))

    _op = MessageHubConsumer(topology, schema=schema, outputMessageAttributeName=msg_attr_name, appConfigName=credentials, topic=topic, startPosition=start_pos, startTime=start_time, name=name)
    print("MHCONS",  _op.params)
    return _op.stream

def publish(stream, topic, credentials=None, name=None):
    """Publish Message Hub messages to a topic.

    Adds a Message Hub producer where each tuple on `stream` is
    published as a stream message.

    Args:
        stream(Stream): Stream of tuples to published as messages.
        topic(str): Topic to publish messages to.
        credentials(str): Name of the application configuration containing the credentials for Message Hub. When set to ``None`` the application configuration ``messagehub`` is used.
        name(str): Producer name in the Streams context, defaults to a generated name.
    """
    if stream.oport.schema == CommonSchema.Json:
        msg_attr = 'jsonString'
    elif stream.oport.schema == CommonSchema.String:
        msg_attr = 'string'
    else:
        raise TypeError(schema)

    _op = MessageHubProducer(stream, appConfigName=credentials, topic=topic)
    _op.params['messageAttribute'] = _op.attribute(stream, msg_attr)
    
class MessageHubConsumer(streamsx.spl.op.Source):
    def __init__(self, topology, schema, vmArg=None, appConfigName=None, clientId=None, messageHubCredentialsFile=None, outputKeyAttributeName=None, outputMessageAttributeName=None, outputTimestampAttributeName=None, outputOffsetAttributeName=None, outputPartitionAttributeName=None, outputTopicAttributeName=None, partition=None, propertiesFile=None, startPosition=None, startTime=None, topic=None, triggerCount=None, userLib=None, name=None):
        kind="com.ibm.streamsx.messagehub::MessageHubConsumer"
        inputs=None
        schemas=schema
        params = dict()
        if vmArg is not None:
            params['vmArg'] = vmArg
        if appConfigName is not None:
            params['appConfigName'] = appConfigName
        if clientId is not None:
            params['clientId'] = clientId
        if messageHubCredentialsFile is not None:
            params['messageHubCredentialsFile'] = messageHubCredentialsFile
        if outputKeyAttributeName is not None:
            params['outputKeyAttributeName'] = outputKeyAttributeName
        if outputMessageAttributeName is not None:
            params['outputMessageAttributeName'] = outputMessageAttributeName
        if outputTimestampAttributeName is not None:
            params['outputTimestampAttributeName'] = outputTimestampAttributeName
        if outputOffsetAttributeName is not None:
            params['outputOffsetAttributeName'] = outputOffsetAttributeName
        if outputPartitionAttributeName is not None:
            params['outputPartitionAttributeName'] = outputPartitionAttributeName
        if outputTopicAttributeName is not None:
            params['outputTopicAttributeName'] = outputTopicAttributeName
        if partition is not None:
            params['partition'] = partition
        if propertiesFile is not None:
            params['propertiesFile'] = propertiesFile
        if startPosition is not None:
            params['startPosition'] = startPosition
        if startTime is not None:
            params['startTime'] = startTime
        if topic is not None:
            params['topic'] = topic
        if triggerCount is not None:
            params['triggerCount'] = triggerCount
        if userLib is not None:
            params['userLib'] = userLib
        super(MessageHubConsumer, self).__init__(topology,kind,schemas,params,name)



class MessageHubProducer(streamsx.spl.op.Sink):
    def __init__(self, stream, vmArg=None, appConfigName=None, keyAttribute=None, messageAttribute=None, messageHubCredentialsFile=None, partitionAttribute=None, propertiesFile=None, timestampAttribute=None, topicAttribute=None, topic=None, userLib=None, name=None):
        topology = stream.topology
        kind="com.ibm.streamsx.messagehub::MessageHubProducer"
        params = dict()
        if vmArg is not None:
            params['vmArg'] = vmArg
        if appConfigName is not None:
            params['appConfigName'] = appConfigName
        if keyAttribute is not None:
            params['keyAttribute'] = keyAttribute
        if messageAttribute is not None:
            params['messageAttribute'] = messageAttribute
        if messageHubCredentialsFile is not None:
            params['messageHubCredentialsFile'] = messageHubCredentialsFile
        if partitionAttribute is not None:
            params['partitionAttribute'] = partitionAttribute
        if propertiesFile is not None:
            params['propertiesFile'] = propertiesFile
        if timestampAttribute is not None:
            params['timestampAttribute'] = timestampAttribute
        if topicAttribute is not None:
            params['topicAttribute'] = topicAttribute
        if topic is not None:
            params['topic'] = topic
        if userLib is not None:
            params['userLib'] = userLib
        super(MessageHubProducer, self).__init__(kind,stream,params,name)
