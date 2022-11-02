package org.fourstack.kafka.libraryeventconsumer.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.fourstack.kafka.libraryeventconsumer.constants.EventsConsumerConstants;
import org.fourstack.kafka.libraryeventconsumer.dao.FailureRecordRepository;
import org.fourstack.kafka.libraryeventconsumer.model.FailureRecord;
import org.fourstack.kafka.libraryeventconsumer.service.LibraryEventService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.fourstack.kafka.libraryeventconsumer.constants.EventsConsumerConstants.RETRY;

@Component
@Slf4j
public class RetryScheduler {

    private FailureRecordRepository failureRecordRepository;
    private LibraryEventService libraryEventService;

    public RetryScheduler(FailureRecordRepository failureRecordRepository, LibraryEventService libraryEventService) {
        this.failureRecordRepository = failureRecordRepository;
        this.libraryEventService = libraryEventService;
    }

    // foe every 10 seconds the scheduler will get triggered.
    @Scheduled(fixedRate = 10_000)
    public void retryFailedRecords() {
        log.info("Retrying Failed Records started!");
        failureRecordRepository.findAllByStatus(RETRY)
                .stream()
                .forEach(record -> {
                    ConsumerRecord<Integer, String> consumerRecord = buildConsumerRecord(record);
                    libraryEventService.processLibraryEvent(consumerRecord);
                    record.setStatus(EventsConsumerConstants.SUCCESS);
                });

        log.info("Processed the Failed Records !!!");
    }

    private ConsumerRecord<Integer, String> buildConsumerRecord(FailureRecord failureRecord) {
        return new ConsumerRecord<>(
                failureRecord.getTopic(),
                failureRecord.getPartition(),
                failureRecord.getOffset_value(),
                failureRecord.getKey(),
                failureRecord.getErrorRecord()
        );
    }
}
