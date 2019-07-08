package com.heetch.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heetch.entity.DriverLocationMessageEntity;
import com.heetch.service.DriverLocationService;
import com.snowplowanalytics.client.nsq.NSQConsumer;
import com.snowplowanalytics.client.nsq.lookup.DefaultNSQLookup;
import com.snowplowanalytics.client.nsq.lookup.NSQLookup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class Worker {

    @Value("${application.nsq.host}")
    private String host;

    @Value("${application.nsq.port}")
    private int port;

    @Value("${application.nsq.topic}")
    private String topic;

    @Value("${application.nsq.channel}")
    private String channel;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private DriverLocationService driverLocationService;

    @Bean
    public void runWorker() {
        NSQLookup nsqLookup = new DefaultNSQLookup();
        nsqLookup.addLookupAddress(host, port);
        NSQConsumer consumer = new NSQConsumer(nsqLookup, topic, channel, (message) -> {
            String driverLocStr = new String(message.getMessage(), StandardCharsets.UTF_8);
            log.info("Received message {}", driverLocStr);

            DriverLocationMessageEntity messageEntity = null;
            try {
                messageEntity = objectMapper.readValue(driverLocStr, DriverLocationMessageEntity.class);
            } catch (IOException e) {
                log.error("Cannot convert message {} into class {}", driverLocStr, DriverLocationMessageEntity.class.getName());
            }
            if (messageEntity == null) {
                message.finished();
                return;
            }

            driverLocationService.saveDriverLocationMessageEntity(messageEntity);

            //now mark the message as finished.
            message.finished();

            //or you could requeue it, which indicates a failure and puts it back on the queue.
            //message.requeue();
        });

        consumer.setExecutor(threadPoolTaskExecutor);
        consumer.start();
    }
}
