from unittest import TestCase

import streamsx.messagehub as mh

from streamsx.topology.topology import Topology
from streamsx.topology.tester import Tester
from streamsx.topology.schema import CommonSchema, StreamSchema

import streamsx.spl.toolkit

import datetime
import os
import time
import uuid

##
## Test assumptions
##
## Streaming analytics service has:
##    application config 'messagehub' configured for MessageHub
##
## Message Hub has:
##
##    topic MH_TEST with one partition (1 hour retention)
##
##
## Locally the toolkit exists at and is at least 1.5.1
## $HOME/toolkits/com.ibm.streamsx.messagehub

class TestSubscribeParams(TestCase):
    def test_schemas_ok(self):
        topo = Topology()
        mh.subscribe(topo, 'T1', CommonSchema.String)
        mh.subscribe(topo, 'T1', CommonSchema.Json)

    def test_schemas_bad(self):
        topo = Topology()
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', CommonSchema.Python)
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', CommonSchema.Binary)
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', CommonSchema.XML)
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', StreamSchema('tuple<int32 a>'))
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', 'tuple<int32 a>')

## Using a uuid to avoid concurrent test runs interferring 
## with each other
class JsonData(object):
    def __init__(self, prefix, count, delay=True):
        self.prefix = prefix
        self.count = count
        self.delay = delay
    def __call__(self):
        # Since we are reading from the end allow
        # time to get the consumer started.
        if self.delay:
            time.sleep(5)
        for i in range(self.count):
            yield {'p': self.prefix + '_' + str(i), 'c': i}

class StringData(object):
    def __init__(self, prefix, count, delay=True):
        self.prefix = prefix
        self.count = count
        self.delay = delay
    def __call__(self):
        if self.delay:
            time.sleep(5)
        for i in range(self.count):
            yield self.prefix + '_' + str(i)

def add_mh_toolkit(topo):
    streamsx.spl.toolkit.add_toolkit(topo,
        os.path.expanduser('~/toolkits/com.ibm.streamsx.messagehub'))


class TestMH(TestCase):
    def setUp(self):
        Tester.setup_streaming_analytics(self, force_remote_build=True)

    def test_json(self):
        n = 104
        topo = Topology()
        add_mh_toolkit(topo)
        uid = str(uuid.uuid4())
        s = topo.source(JsonData(uid, n)).as_json()
        mh.publish(s, 'MH_TEST')

        r = mh.subscribe(topo, 'MH_TEST', CommonSchema.Json)
        r = r.filter(lambda t : t['p'].startswith(uid))
        expected = list(JsonData(uid, n, False)())

        tester = Tester(topo)
        tester.contents(r, expected)
        tester.tuple_count(r, n)
        tester.test(self.test_ctxtype, self.test_config)

    def test_string(self):
        n = 107
        topo = Topology()
        add_mh_toolkit(topo)
        uid = str(uuid.uuid4())
        s = topo.source(StringData(uid, n)).as_string()
        mh.publish(s, 'MH_TEST')

        r = mh.subscribe(topo, 'MH_TEST', CommonSchema.String)
        r = r.filter(lambda t : t.startswith(uid))
        expected = list(StringData(uid, n, False)())

        tester = Tester(topo)
        tester.contents(r, expected)
        tester.tuple_count(r, n)
        tester.test(self.test_ctxtype, self.test_config)
