package com.fun.camel;

import com.fun.camel.helpers.FTPHelper;
import com.fun.camel.helpers.FileHelper;
import com.fun.camel.helpers.KafkaHelper;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.fun.camel.helpers.JSONHelper.jsonUserQuote;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class IntegrationTest {

    private static final String FILE_SAMPLES_PATH = "input_samples";

    private static final String CERSEI_LANNISTER = "Cersei Lannister";
    private static final String JON_SNOW = "Jon Snow";
    private static final String NED_STARK = "Ned Stark";
    private static final String TYRION_LANNISTER = "Tyrion Lannister";
    private static final String TYWIN_LANNISTER = "Tywin Lannister";
    private static final String SANSA_STARK = "Sansa Stark";
    private static final String TORMUND_GIANTSBANE = "Tormund Giantsbane";

    private static final String CERSEI_LANNISTER_4_JSON = jsonUserQuote(4, CERSEI_LANNISTER, "If you ever call me 'sister' again, I'll have you strangled in your sleep");
    private static final String JON_SNOW_5_JSON = jsonUserQuote(5, JON_SNOW, "Stick 'em with the pointy end");
    private static final String NED_STARK_7_JSON = jsonUserQuote(7, NED_STARK, "Winter is coming");
    private static final String TYRION_LANNISTER_1_JSON = jsonUserQuote(1, TYRION_LANNISTER, "The powerful have always preyed on the powerless. That's how they became powerful in the first place");
    private static final String TYRION_LANNISTER_8_JSON = jsonUserQuote(8, TYRION_LANNISTER, "Let me give you some advice, bastard. Never forget what you are. The rest of the world will not. Wear it like armor, and it can never be used to hurt you");
    private static final String TYWIN_LANNISTER_6_JSON = jsonUserQuote(6, TYWIN_LANNISTER, "A lion doesn't concern himself with the opinions of a sheep");
    private static final String SANSA_STARK_2 = jsonUserQuote(2, SANSA_STARK, "You're going to die tomorrow, Lord Bolton. Sleep well");
    private static final String TORMUND_GIANTSBANE_3 = jsonUserQuote(3, TORMUND_GIANTSBANE, "Happy shitting");

    @Value("${file.path}")
    private String filePath;
    @Value("${file.delay}")
    private long fileDelayInMillis;

    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.user}")
    private String ftpUser;
    @Value("${ftp.pass}")
    private String ftpPass;
    @Value("${ftp.path}")
    private String ftpPath;

    @Value("${kafka.host}")
    private String kafkaHost;
    @Value("${kafka.port}")
    private int kafkaPort;
    @Value("${kafka.topic}")
    private String kafkaTopic;

    private FTPHelper ftp;
    private KafkaHelper kafka;

    @Before
    public void before() throws IOException {
        ftp = new FTPHelper(ftpHost, ftpPort, ftpUser, ftpPass);
        ftp.deleteFiles(ftpPath);
        assertThat(ftp.listFiles(ftpPath)).isEmpty();

        kafka = new KafkaHelper(kafkaHost, kafkaPort, kafkaTopic);
        kafka.consume(1000);
    }

    @Test
    public void test_fun2_copied_to_input_directory() throws Exception {

        FileHelper.zipMove("fun2.xml", FILE_SAMPLES_PATH, "fun2.zip", filePath);

        Thread.sleep(fileDelayInMillis * 2);

        FTPFile[] ftpFiles = ftp.listFiles(ftpPath);
        assertThat(ftpFiles)
                .extracting("name", "size")
                .containsOnlyOnce(
                        tuple("sansa-stark-2.json", 103L),
                        tuple("tormund-giantsbane-3.json", 71L)
                );

        ConsumerRecords<String, String> kafkaMessages = kafka.consume(1000);
        assertThat(kafkaMessages)
                .extracting("key", "value")
                .containsOnlyOnce(
                        tuple(SANSA_STARK, SANSA_STARK_2),
                        tuple(TORMUND_GIANTSBANE, TORMUND_GIANTSBANE_3)
                );
    }

    @Test
    public void test_fun1_and_fun5_copied_to_input_directory() throws Exception {

        FileHelper.zipMove("fun1.xml", FILE_SAMPLES_PATH, "fun1.zip", filePath);
        FileHelper.zipMove("fun5.xml", FILE_SAMPLES_PATH, "fun5.zip", filePath);

        Thread.sleep(fileDelayInMillis * 3);

        FTPFile[] ftpFiles = ftp.listFiles(ftpPath);
        assertThat(ftpFiles)
                .extracting("name", "size")
                .containsOnlyOnce(
                        tuple("cersei-lannister-4.json", 128L),
                        tuple("jon-snow-5.json", 76L),
                        tuple("ned-stark-7.json", 64L),
                        tuple("tyrion-lannister-1.json", 155L),
                        tuple("tyrion-lannister-8.json", 208L),
                        tuple("tywin-lannister-6.json", 113L)
                );

        ConsumerRecords<String, String> kafkaMessages = kafka.consume(1000);
        assertThat(kafkaMessages)
                .extracting("key", "value")
                .containsOnlyOnce(
                        tuple(CERSEI_LANNISTER, CERSEI_LANNISTER_4_JSON),
                        tuple(JON_SNOW, JON_SNOW_5_JSON),
                        tuple(NED_STARK, NED_STARK_7_JSON),
                        tuple(TYRION_LANNISTER, TYRION_LANNISTER_1_JSON),
                        tuple(TYRION_LANNISTER, TYRION_LANNISTER_8_JSON),
                        tuple(TYWIN_LANNISTER, TYWIN_LANNISTER_6_JSON)
                );
    }
}
