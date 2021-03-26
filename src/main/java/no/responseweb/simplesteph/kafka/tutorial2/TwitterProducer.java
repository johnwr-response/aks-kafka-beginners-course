package no.responseweb.simplesteph.kafka.tutorial2;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

    public TwitterProducer() {}

    public static void main(String[] args) {
        new TwitterProducer().run();
    }

    public void run() {
        logger.info("Setup");
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);
        Client client = createTwitterClient(msgQueue);
        client.connect();

        // create kafka producer

        // loop to send tweets to kafka
        while (!client.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                client.stop();
            }
            if  (msg != null) {
                logger.info(msg);
            }
        }
        logger.info("End of application");

    }

    public Client createTwitterClient(BlockingQueue<String> msgQueue) {
        Hosts hoseBirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hoseBirdEndpoint = new StatusesFilterEndpoint();
        List<String> terms = Lists.newArrayList("bitcoin");
        hoseBirdEndpoint.trackTerms(terms);

        String consumerKey = "something1";
        String consumerSecret = "something2";
        String token = "something3";
        String secret = "something4";
        Authentication hoseBirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);

        return new ClientBuilder()
                .name("HoseBird-Client-01")
                .hosts(hoseBirdHosts)
                .authentication(hoseBirdAuth)
                .endpoint(hoseBirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .build();
    }
}
