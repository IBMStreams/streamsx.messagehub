# coding=utf-8
# Licensed Materials - Property of IBM
# Copyright IBM Corp. 2017

import streamsx.spl.op
from streamsx.topology.schema import CommonSchema

def consume(topology, topic, schema, app_config_name=None, name=None):
    """Consume messages from Message Hub for a topic.
    """
    if schema == CommonSchema.Json:
        msg_attr_name='jsonString'
    elif schema == CommonSchema.String:
        msg_attr_name='string'
    else:
        raise TypeError(schema)

    _op = MessageHubConsumer(topology, schema=CommonSchema.Json, outputMessageAttributeName=msg_attr_name, appConfigName=app_config_name, topic=topic)
    return _op.stream

def produce(stream, topic, app_config_name=None, name=None):
    """Produce Message Hub messages to a topic.
    For each tuple on `stream` a message is produced on queue `topic`.
    """
    if stream.oport.schema == CommonSchema.Json:
        msg_attr = 'jsonString'
    elif stream.oport.schema == CommonSchema.String:
        msg_attr = 'string'
    else:
        raise TypeError(schema)

    _op = MessageHubProducer(stream, appConfigName=app_config_name, topic=topic)
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
