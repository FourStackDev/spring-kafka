package org.fourstack.kafka.libraryeventconsumer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.dao.FailureRecordRepository;
import org.fourstack.kafka.libraryeventconsumer.model.FailureRecord;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FailureRecordService {

    private FailureRecordRepository failureRecordRepository;

    public FailureRecordService(FailureRecordRepository failureRecordRepository) {
        this.failureRecordRepository = failureRecordRepository;
    }

    public void saveFailedRecord(ConsumerRecord<Integer,String> consumerRecord, Exception exception, String status) {
        FailureRecord record = FailureRecord.builder()
                .topic(consumerRecord.topic())
                .key(consumerRecord.key())
                .errorRecord(consumerRecord.value())
                .partition(consumerRecord.partition())
                .offset_value(consumerRecord.offset())
                .exception(exception.getMessage())
                .status(status)
                .build();

        failureRecordRepository.save(record);
    }
}
