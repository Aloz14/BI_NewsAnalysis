package org.bi;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class KafkaAdminApp {

    private static final String BOOTSTRAP_SERVERS = "localhost:9092";

    public static void main(String[] args) {
        KafkaAdminApp kafkaAdminApp = new KafkaAdminApp();
        kafkaAdminApp.listTopics();
    }

    // 查看主题
    private void listTopics() {
        try (AdminClient adminClient = createAdminClient()) {
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);

            Set<String> topics = adminClient.listTopics(options).names().get();
            System.out.println("Topics:");
            topics.forEach(System.out::println);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // 添加主题
    private void createTopic(String topicName, int partitions, short replicationFactor) {
        try (AdminClient adminClient = createAdminClient()) {
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            System.out.println("Topic " + topicName + " created successfully.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // 删除主题
    private void deleteTopic(String topicName) {
        try (AdminClient adminClient = createAdminClient()) {
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            System.out.println("Topic " + topicName + " deleted successfully.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private AdminClient createAdminClient() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        return AdminClient.create(props);
    }
}
