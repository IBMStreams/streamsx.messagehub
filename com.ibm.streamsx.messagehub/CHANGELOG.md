# Changes
==========

## v3.3.1
* [IBMStreams/streamsx.kafka/#228](https://github.com/IBMStreams/streamsx.kafka/issues/228)KafkaConsumer: support submission time parameter for multiple topics

## v3.3.0
* [#116](https://github.com/IBMStreams/streamsx.messagehub/issues/116) Makefiles of sample application prepared for CP4D Streams build service, supports build with [VS Code](https://marketplace.visualstudio.com/items?itemName=IBM.ibm-streams)
* [IBMStreams/streamsx.kafka/#218](https://github.com/IBMStreams/streamsx.kafka/issues/218) support incremental cooperative rebalancing
* [IBMStreams/streamsx.kafka/#217](https://github.com/IBMStreams/streamsx.kafka/issues/217) upgrade kafka-clients from 2.3.1 to 2.5.1

## v3.2.2
* [IBMStreams/streamsx.messagehub/#115](https://github.com/IBMStreams/streamsx.messagehub/issues/115) I18n: update message translations
* [IBMStreams/streamsx.kafka/#224](https://github.com/IBMStreams/streamsx.kafka/issues/224) Application directory cannot be used as file location for files specified via system property and vmArg. This correction in the underlying Kafka toolkit is most likely irrelevant for this toolkit.
* [IBMStreams/streamsx.kafka/#222](https://github.com/IBMStreams/streamsx.kafka/issues/222) KafkaConsumer: Reset of CR may fail when partitions are revoked before the reset actually happens

## v3.2.1:
* [IBMStreams/streamsx.messagehub/#114](https://github.com/IBMStreams/streamsx.messagehub/issues/114) toolkit includes a vulnerable log4j.jar

## v3.2.0
* [IBMStreams/streamsx.messagehub/#113](https://github.com/IBMStreams/streamsx.messagehub/issues/113) MessageHubConsumer: add startPositionStr parameter to enable start position at submission time
* upgrade the streamsx.kafka toolkit library to version 3.1.0

## v3.1.0:
* [IBMStreams/streamsx.messagehub/#109](https://github.com/IBMStreams/streamsx.messagehub/issues/109) no support for static consumer group membership

## v3.0.4:
* [IBMStreams/streamsx.kafka/#203](https://github.com/IBMStreams/streamsx.kafka/issues/203) KafkaConsumer: assign output attributes via index rather than attribute name
* [#105](https://github.com/IBMStreams/streamsx.messagehub/issues/105) Make main composites of samples public.
  This allows using the samples with _streamsx_ Python package.
* [IBMStreams/streamsx.kafka/#208](https://github.com/IBMStreams/streamsx.kafka/issues/208) KafkaProducer: message or key attribute with underline causes error at context checker.
  All previous versions back to 1.0.0 are affected by this issue.
* New sample: [KafkaAvroSample](https://github.com/IBMStreams/streamsx.kafka/tree/develop/samples/KafkaAvroSample)

## v3.0.3:
* [IBMStreams/streamsx.kafka/#198](https://github.com/IBMStreams/streamsx.kafka/issues/198) - The "nConsecutiveRuntimeExc" variable never reaches 50 when exceptions occur

## v3.0.2
* [IBMStreams/streamsx.kafka/#200](https://github.com/IBMStreams/streamsx.kafka/issues/200) - I18n update

## v3.0.1
* [IBMStreams/streamsx.kafka/#196](https://github.com/IBMStreams/streamsx.kafka/issues/196) - KafkaProducer: Consistent region reset can trigger addtional reset

## v3.0.0
### Changes and enhancements
* The included Kafka client has been upgraded from version 2.2.1 to 2.3.1.
* The schema of the output port of the `MessageHubProducer` operator supports optional types for the error description.
* The optional input port of the `MessageHubConsumer` operator can be used to change the *topic subscription*, not only the *partition assignment*.
* The **guaranteeOrdering** parameter now enables the idempotent producer when set to `true`, which allows a higher throughput by allowing more
  in-flight requests per connection (requires Kafka server version 0.11 or higher).
* The `MessageHubConsumer` operator now enables and benefits from group management when the user does not specify a group identifier.
* Checkpoint reset of the `MessageHubConsumer` is optimized in consistent region when the consumer is the only group member.
* The `MessageHubConsumer` operator now uses `read_committed` as the default `isolation.level` configuration unless the user has specified a different value.
  In `read_committed` mode, the consumer will read only those transactional messages which have been successfully committed.
  Messages of aborted transactions are now skipped. The consumer will continue to read non-transactional messages as before.
  This new default setting is incompatible with Kafka 0.10.2.

### Deprecated features
The use of the input control port has been deprecated when the `MessageHubConsumer` is used in a consistent region.

### Removed features
The function, which has been deprecated in toolkit version 2.x has been removed in this toolkit version. The removed function contains following items:
* The **messageHubCredentialsFile**  operator parameter has been removed from all operators. Please use the **credentialsFile** parameter instead.
* The default filename `etc/messagehub.json` for specifying Event Streams service credentials is not read any more. Please use `etc/eventstreams.json` when you want to use a default filename for service credentials.
* The default name `messagehub` for an application configuration is not read any more. Please use `eventstreams` as name for the application configuration when you want to use a default.
* The property name `messagehub.creds` within an application configuration is not read any more. Please name the property for the Event Streams credentials `eventstreams.creds`.

### Incompatible changes
* The toolkit requires at minimum Streams version 4.3.
* When the `MessageHubConsumer` operator is configured with input port, the **topic**, **pattern**, **partition**, and **startPosition**
  parameters used to be ignored in previous versions. Now an SPL compiler failure is raised when one of these parameters is used
  together with the input port.
* The deprecated function has been removed.

## v2.2.1
* [IBMStreams/streamsx.kafka/#179](https://github.com/IBMStreams/streamsx.kafka/issues/179) - KafkaProducer: Lost output tuples on FinalMarker reception

## v2.2.0
* The `MessageHubProducer` operator supports an optional output port, configurable via the new **outputErrorsOnly** operator parameter
* Exception handling of the `MessageHubProducer` operator in autonomous region changed. The operator does not abort its PE anymore; it recovers internally instead.
* New custom metrics for the `MessageHubProducer` operator: `nFailedTuples`, `nPendingTuples`, and `nQueueFullPause`

## v2.1.0
### Changes and enhancements
* This toolkit version has been tested also with Kafka 2.3
* [IBMStreams/streamsx.kafka/#169](https://github.com/IBMStreams/streamsx.kafka/issues/169) new optional operator parameter **sslDebug**. For debugging SSL issues see also the [Kafka toolkit documentation](https://ibmstreams.github.io/streamsx.kafka/docs/user/debugging_ssl_issues/)
* [IBMStreams/streamsx.kafka/#167](https://github.com/IBMStreams/streamsx.kafka/issues/167) changed default values for following consumer and producer configurations:

  - `client.dns.lookup = use_all_dns_ips`
  - `reconnect.backoff.max.ms = 10000` (Kafka's default is 1000)
  - `reconnect.backoff.ms = 250` (Kafka's default is 50)
  - `retry.backoff.ms = 500` (Kafka's default is 100)

* Changed exception handling for the MessageHubProducer when not used in a consistent region: https://github.com/IBMStreams/streamsx.kafka/issues/163#issuecomment-505402607

### Bug fixes
* [IBMStreams/streamsx.kafka/#163](https://github.com/IBMStreams/streamsx.kafka/issues/163) MessageHubProducer's exception handling makes the operator lose tuples when in CR
* [IBMStreams/streamsx.kafka/#164](https://github.com/IBMStreams/streamsx.kafka/issues/164) on reset() the MessageHubProducerOperator should instantiate a new producer instance
* [IBMStreams/streamsx.kafka/#166](https://github.com/IBMStreams/streamsx.kafka/issues/166) Resource leak in MessageHubProducer when reset to initial state in a CR

## v2.0.2
* [IBMStreams/streamsx.kafka/#171](https://github.com/IBMStreams/streamsx.kafka/issues/171) Resetting from checkpoint will fail when sequence id is >1000

## v2.0.1
* [#91](https://github.com/IBMStreams/streamsx.messagehub/issues/91) - Operators fail to parse credentials

## v2.0.0
### Changes and enhancements

* The included Kafka client has been upgraded from version 2.1.1 to 2.2.1, [#98](https://github.com/IBMStreams/streamsx.messagehub/issues/98)
* Support for Kafka broker 2.2 has been added, [IBMStreams/streamsx.kafka/#161](https://github.com/IBMStreams/streamsx.kafka/issues/161)
- The toolkit has enhancements for the **MessageHubConsumer** when it is used in an autonomous region (i.e. not part of a consistent region):
    - The MessageHubConsumer operator can now participate in a consumer group with **startPosition** parameter values `Beginning`, 'End`, and `Time`, [IBMStreams/streamsx.kafka/#94](https://github.com/IBMStreams/streamsx.kafka/issues/94)
    - After re-launch of the PE, the MessageHubConsumer operator does not overwrite the initial fetch offset to what the **startPosition** parameter is, i.e. after PE re-launch the consumer starts consuming at last committed offset, [IBMStreams/streamsx.kafka/#107](https://github.com/IBMStreams/streamsx.kafka/issues/107)

The new **startPosition** handling requires that the application always includes a **JobControlPlane** operator when **startPosition** is different from `Default`.

### Incompatible changes

The behavior of the MessageHubConsumer operator changes when
1. the operator is *not* used in a consistent region, and
1. the **startPosition** parameter is used with `Beginning`, `End`, `Time`, or` Offset`.

In all other cases the behavior of the MessageHubConsumer is unchanged. Details of the changed behavior, including sample code that breaks, can be found in the [Toolkit documentation on Github](https://ibmstreams.github.io/streamsx.kafka/docs/user/kafka_toolkit_1_vs_2/).

## v1.9.4
* [IBMStreams/streamsx.kafka/#171](https://github.com/IBMStreams/streamsx.kafka/issues/171) Resetting from checkpoint will fail when sequence id is >1000

## Older releases
Please consult the [release notes](https://github.com/IBMStreams/streamsx.messagehub/releases) for the release you are interested in.
