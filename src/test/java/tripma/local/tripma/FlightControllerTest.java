package tripma.local.tripma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import tripma.local.tripma.dto.Flight.FlightRequest;
import tripma.local.tripma.dto.Flight.FlightResponse;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.service.FlightService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class FlightControllerTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
                this.objectMapper = new ObjectMapper();
                this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        }

        @MockitoBean
        private FlightService flightService;

        @Test
        void shouldFetchAllFlights() throws Exception {
                FlightResponse response = new FlightResponse(1L, "FL123", 1, 1, 2, LocalDateTime.now(),
                                LocalDateTime.now().plusHours(2), new BigDecimal("100.00"), 1);
                Page<FlightResponse> page = new PageImpl<>(List.of(response));
                when(flightService.findAll(any(Pageable.class))).thenReturn(page);

                mockMvc.perform(get("/api/v1/flights"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].flightNumber").value("FL123"));
        }

        @Test
        void shouldFetchFlightById() throws Exception {
                FlightResponse response = new FlightResponse(1L, "FL123", 1, 1, 2, LocalDateTime.now(),
                                LocalDateTime.now().plusHours(2), new BigDecimal("100.00"), 1);
                when(flightService.findById(1L)).thenReturn(response);

                mockMvc.perform(get("/api/v1/flights/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.flightNumber").value("FL123"));
        }

        @Test
        void shouldReturn404WhenFlightNotFound() throws Exception {
                when(flightService.findById(99L)).thenThrow(new ResourceNotFoundException("Flight not found"));

                mockMvc.perform(get("/api/v1/flights/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        void shouldCreateFlight() throws Exception {
                LocalDateTime departure = LocalDateTime.now().plusDays(1);
                LocalDateTime arrival = departure.plusHours(2);
                FlightRequest request = new FlightRequest("FL123", 1, 1, 2, departure, arrival,
                                new BigDecimal("100.00"), 1);
                FlightResponse response = new FlightResponse(1L, "FL123", 1, 1, 2, departure, arrival,
                                new BigDecimal("100.00"),
                                1);

                when(flightService.create(any(FlightRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.flightNumber").value("FL123"));
        }

        @Test
        void shouldReturn400WhenCreateFlightWithInvalidData() throws Exception {
                FlightRequest request = new FlightRequest("", null, null, null, null, null, new BigDecimal("-10.00"),
                                null);

                mockMvc.perform(post("/api/v1/flights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void shouldUpdateFlight() throws Exception {
                LocalDateTime departure = LocalDateTime.now().plusDays(1);
                LocalDateTime arrival = departure.plusHours(2);
                FlightRequest request = new FlightRequest("FL123", 1, 1, 2, departure, arrival,
                                new BigDecimal("100.00"), 1);
                FlightResponse response = new FlightResponse(1L, "FL123", 1, 1, 2, departure, arrival,
                                new BigDecimal("100.00"),
                                1);

                when(flightService.update(eq(1L), any(FlightRequest.class))).thenReturn(response);

                mockMvc.perform(put("/api/v1/flights/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.flightNumber").value("FL123"));
        }

        @Test
        void shouldDeleteFlight() throws Exception {
                doNothing().when(flightService).delete(1L);

                mockMvc.perform(delete("/api/v1/flights/1"))
                                .andExpect(status().isNoContent());

                verify(flightService, times(1)).delete(1L);
        }
}
