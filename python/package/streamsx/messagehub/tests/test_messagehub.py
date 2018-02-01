from unittest import TestCase

import streamsx.messagehub as mh

from streamsx.topology.topology import Topology
from streamsx.topology.tester import Tester
from streamsx.topology.schema import CommonSchema, StreamSchema

import datetime
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

    def test_start_ok(self):
        topo = Topology()
        mh.subscribe(topo, 'T1', CommonSchema.String, start=None)
        mh.subscribe(topo, 'T1', CommonSchema.String, start=False)
        mh.subscribe(topo, 'T1', CommonSchema.String, start=True)
        mh.subscribe(topo, 'T1', CommonSchema.String, start=time.time())
        mh.subscribe(topo, 'T1', CommonSchema.String, start=datetime.datetime.now())

    def test_start_bad(self):
        topo = Topology()
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', CommonSchema.String, start=3)
        self.assertRaises(TypeError, mh.subscribe, topo, 'T1', CommonSchema.String, start='now')

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

class TestMH(TestCase):
    def setUp(self):
        Tester.setup_streaming_analytics(self, force_remote_build=True)

    def test_json(self):
        n = 104
        topo = Topology()
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

    def test_end_beginning(self):
        for start in [False, True]:
            with self.subTest(start=start):
                # Pre-populate with some data.
                n1 = 58
                topo = Topology()
                uid1 = str(uuid.uuid4())
                s1 = topo.source(StringData(uid1, n1, False)).as_string()
                mh.publish(s1, 'MH_TEST')
                tester = Tester(topo)
                tester.tuple_count(s1, n1)
                tester.test(self.test_ctxtype, self.test_config)
        
                n2 = 43
                topo = Topology()
                uid2 = str(uuid.uuid4())
                s2 = topo.source(StringData(uid2, n2)).as_string()
                mh.publish(s2, 'MH_TEST')
        
                r = mh.subscribe(topo, 'MH_TEST', CommonSchema.String, start=start)
                r = r.filter(lambda t : t.startswith(uid1) or t.startswith(uid2))

                if start:
                    expected = list(StringData(uid1, n1, False)())
                    expected.extend(StringData(uid2, n2, False)())
                    n = n1 + n2
                else:
                    expected = list(StringData(uid2, n2, False)())
                    n = n2

                tester = Tester(topo)
                tester.contents(r, expected)
                tester.tuple_count(r, n)
                tester.test(self.test_ctxtype, self.test_config)
