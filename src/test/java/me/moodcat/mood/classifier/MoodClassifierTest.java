package me.moodcat.mood.classifier;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import me.moodcat.database.embeddables.AcousticBrainzData;
import me.moodcat.database.embeddables.VAVector;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jan-Willem Gmelig Meyling
 */
@RunWith(Parameterized.class)
public class MoodClassifierTest {

    // Decrease the value for more precise testing
    private final static double EPSILON = 200.0;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "/acousticbrainz/testData/100019264.json", new VAVector(-1, 1)
            }
        });
    }

    private final AcousticBrainzData data;

    private final VAVector expected;

    private final MoodClassifier moodClassifier;

    public MoodClassifierTest(final String path, final VAVector expected) throws IOException {
        this.data = parseData(path);
        this.expected = expected;
        this.moodClassifier = new MoodClassifier();
    }

    @Test
    public void testMoodClassifierClassifiesExpectedValue() {
        final VAVector actual = this.moodClassifier.predict(this.data);

        assertVAVectorEquals(this.expected, actual);
    }

    public static void assertVAVectorEquals(final VAVector expected, final VAVector actual) {
        assertEquals(expected.getArousal(), actual.getArousal(), EPSILON);
        assertEquals(expected.getValence(), actual.getValence(), EPSILON);
    }

    public static AcousticBrainzData parseData(final String path) throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream in = MoodClassifierTest.class.getResourceAsStream(path)) {
            return objectMapper.readValue(in, AcousticBrainzData.class);
        }
    }

}