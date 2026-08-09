package tripma.local.tripma;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tripma.local.tripma.repository.FlightRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class FlightControllerTest {

    @Autowired
    private FlightRepository flightRepository;

    @Test
    void shouldFetchFlights() {
        long count = flightRepository.count();
        assertNotNull(count);
        System.out.println("Total flights in DB: " + count);
    }
}
