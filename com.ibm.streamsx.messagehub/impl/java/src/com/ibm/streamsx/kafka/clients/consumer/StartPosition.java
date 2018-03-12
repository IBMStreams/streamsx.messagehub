package com.ibm.streamsx.kafka.clients.consumer;
// This enum is required here even if the same enum is in the Kafka project because the SPL toolkit indexer needs it in this project.
public enum StartPosition {
    Beginning, End, Default, Time, Offset;
}
