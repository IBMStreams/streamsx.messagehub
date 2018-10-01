package com.ibm.streamsx.kafka.clients.producer;

/**
 * Keep this enum in sync with the same enum in the Kafka toolkit!
 * The SPL toolkit indexer needs it in this project.
 */
public enum ConsistentRegionPolicy {
    AtLeastOnce,
    NonTransactional,
    Transactional;
}
