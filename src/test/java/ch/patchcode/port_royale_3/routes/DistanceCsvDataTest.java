package ch.patchcode.port_royale_3.routes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.number.IsCloseTo.closeTo;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class DistanceCsvDataTest {

    private DistanceCsvData data;

    @Before
    public void setup() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("distances_simple.csv")) {
            data = new DistanceCsvData(is);
        }
    }

    @Test
    public void allPlacesArePresent() {
        assertThat(data.getPlaces(), containsInAnyOrder("1st Place", "2nd Place", "3rd Place"));
    }

    @Test
    public void allDistancesAreCorrect() {
        assertThat(data.getDistance("1st Place", "2nd Place"), closeTo(1.5, 0.01));
        assertThat(data.getDistance("1st Place", "3rd Place"), closeTo(2.0, 0.01));
        assertThat(data.getDistance("2nd Place", "1st Place"), closeTo(1.5, 0.01));
        assertThat(data.getDistance("2nd Place", "3rd Place"), closeTo(1.2, 0.01));
        assertThat(data.getDistance("3rd Place", "1st Place"), closeTo(2.0, 0.01));
        assertThat(data.getDistance("3rd Place", "2nd Place"), closeTo(1.2, 0.01));
    }
}
