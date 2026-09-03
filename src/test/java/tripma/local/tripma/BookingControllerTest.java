package tripma.local.tripma;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tripma.local.tripma.dto.Booking.BookingRequest;
import tripma.local.tripma.dto.Booking.BookingResponse;
import tripma.local.tripma.dto.Booking.FlightLegRequest;
import tripma.local.tripma.dto.HotelBooking.HotelBookingRequest;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.service.BookingService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer integration tests for BookingController.
 *
 * <p>
 * Chiến lược: @SpringBootTest + @MockitoBean cho BookingService để kiểm thử
 * đúng tầng MVC (validation, serialisation, HTTP status) mà không cần DB thật,
 * nhất quán với FlightControllerTest và HotelControllerTest.
 */
@SpringBootTest
class BookingControllerTest {

        private MockMvc mockMvc;

        @Autowired
        private WebApplicationContext webApplicationContext;

        @MockitoBean
        private BookingService bookingService;

        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
                this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
                this.objectMapper = new ObjectMapper();
                this.objectMapper.registerModule(new JavaTimeModule());
        }

        // ─── Helpers ──────────────────────────────────────────────────────────────

        private BookingResponse sampleFlightBookingResponse() {
                return new BookingResponse(1, 1, "BK-001", "FLIGHT", "PENDING",
                                null, BigDecimal.ZERO, new BigDecimal("500000"), List.of());
        }

        private BookingResponse sampleHotelBookingResponse() {
                return new BookingResponse(2, 1, "HOTEL-12345", "HOTEL", "PENDING",
                                null, BigDecimal.ZERO, new BigDecimal("1500000"), List.of());
        }

        // ─── POST /api/v1/bookings/flight — Positive
        // ──────────────────────────────────

        /**
         * [Positive] Gửi request hợp lệ với 1 flight leg → 201 Created,
         * body phải chứa đúng bookingCode và type = "FLIGHT".
         */
        @Test
        void createFlightBooking_validRequest_returns201() throws Exception {
                FlightLegRequest leg = new FlightLegRequest(1, "OUTBOUND", "ECONOMY", new BigDecimal("500000"));
                BookingRequest request = new BookingRequest(1, "BK-001", null, null, null, List.of(leg));

                when(bookingService.createFlightBooking(any(BookingRequest.class)))
                                .thenReturn(sampleFlightBookingResponse());

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.bookingCode").value("BK-001"))
                                .andExpect(jsonPath("$.type").value("FLIGHT"))
                                .andExpect(jsonPath("$.status").value("PENDING"));
        }

        /**
         * [Positive] Request có 2 flight legs — service được gọi đúng 1 lần.
         */
        @Test
        void createFlightBooking_multipleLegs_serviceCalledOnce() throws Exception {
                FlightLegRequest leg1 = new FlightLegRequest(1, "OUTBOUND", "ECONOMY", new BigDecimal("500000"));
                FlightLegRequest leg2 = new FlightLegRequest(2, "RETURN", "BUSINESS", new BigDecimal("900000"));
                BookingRequest request = new BookingRequest(1, "BK-002", null, null, null, List.of(leg1, leg2));

                when(bookingService.createFlightBooking(any(BookingRequest.class)))
                                .thenReturn(sampleFlightBookingResponse());

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated());

                verify(bookingService, times(1)).createFlightBooking(any(BookingRequest.class));
        }

        // ─── POST /api/v1/bookings/flight — Negative ─────────────────────────────────

        /**
         * [Negative] userId = null → @NotNull vi phạm → phải trả về 400 Bad Request.
         */
        @Test
        void createFlightBooking_nullUserId_returns400() throws Exception {
                // userId null, bookingCode hợp lệ, flights không rỗng
                FlightLegRequest leg = new FlightLegRequest(1, "OUTBOUND", "ECONOMY", new BigDecimal("100"));
                String body = """
                                {
                                  "userId": null,
                                  "bookingCode": "BK-NULL",
                                  "flights": [
                                    {"flightId": 1, "direction": "OUTBOUND", "cabinClass": "ECONOMY", "price": 100}
                                  ]
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] bookingCode là chuỗi rỗng → @NotBlank vi phạm → 400.
         */
        @Test
        void createFlightBooking_blankBookingCode_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "bookingCode": "",
                                  "flights": [
                                    {"flightId": 1, "direction": "OUTBOUND", "cabinClass": "ECONOMY", "price": 100}
                                  ]
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] Danh sách flights rỗng → @NotEmpty vi phạm → 400.
         */
        @Test
        void createFlightBooking_emptyFlightsList_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "bookingCode": "BK-003",
                                  "flights": []
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] flightId trong leg = null → @NotNull cascaded qua @Valid → 400.
         */
        @Test
        void createFlightBooking_nullFlightIdInLeg_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "bookingCode": "BK-004",
                                  "flights": [
                                    {"flightId": null, "direction": "OUTBOUND", "cabinClass": "ECONOMY", "price": 100}
                                  ]
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] Service ném ResourceNotFoundException (flight ID không tồn tại) →
         * 404.
         */
        @Test
        void createFlightBooking_unknownFlightId_returns404() throws Exception {
                FlightLegRequest leg = new FlightLegRequest(999, "OUTBOUND", "ECONOMY", new BigDecimal("100"));
                BookingRequest request = new BookingRequest(1, "BK-999", null, null, null, List.of(leg));

                when(bookingService.createFlightBooking(any(BookingRequest.class)))
                                .thenThrow(new ResourceNotFoundException("Không tìm thấy chuyến bay với ID 999"));

                mockMvc.perform(post("/api/v1/bookings/flight")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }

        // ─── POST /api/v1/bookings/hotel — Positive ──────────────────────────────────

        /**
         * [Positive] Request khách sạn hợp lệ → 201 Created, type = "HOTEL".
         */
        @Test
        void createHotelBooking_validRequest_returns201() throws Exception {
                HotelBookingRequest request = new HotelBookingRequest(
                                1, 5,
                                LocalDate.of(2026, 10, 1),
                                LocalDate.of(2026, 10, 4),
                                2, null);

                when(bookingService.createHotelBooking(any(HotelBookingRequest.class)))
                                .thenReturn(sampleHotelBookingResponse());

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.type").value("HOTEL"))
                                .andExpect(jsonPath("$.status").value("PENDING"));
        }

        // ─── POST /api/v1/bookings/hotel — Negative ──────────────────────────────────

        /**
         * [Negative] userId = null → @NotNull vi phạm → 400.
         */
        @Test
        void createHotelBooking_nullUserId_returns400() throws Exception {
                String body = """
                                {
                                  "userId": null,
                                  "hotelId": 5,
                                  "checkIn": "2026-10-01",
                                  "checkOut": "2026-10-04",
                                  "guests": 2
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] hotelId = null → @NotNull vi phạm → 400.
         */
        @Test
        void createHotelBooking_nullHotelId_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "hotelId": null,
                                  "checkIn": "2026-10-01",
                                  "checkOut": "2026-10-04",
                                  "guests": 2
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] guests = 0 → @Positive vi phạm (phải > 0) → 400.
         */
        @Test
        void createHotelBooking_zeroGuests_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "hotelId": 5,
                                  "checkIn": "2026-10-01",
                                  "checkOut": "2026-10-04",
                                  "guests": 0
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] checkIn = null → @NotNull vi phạm → 400.
         */
        @Test
        void createHotelBooking_nullCheckIn_returns400() throws Exception {
                String body = """
                                {
                                  "userId": 1,
                                  "hotelId": 5,
                                  "checkIn": null,
                                  "checkOut": "2026-10-04",
                                  "guests": 2
                                }
                                """;

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isBadRequest());

                verifyNoInteractions(bookingService);
        }

        /**
         * [Negative] Service ném ResourceNotFoundException (hotel ID không tồn tại) →
         * 404.
         */
        @Test
        void createHotelBooking_unknownHotelId_returns404() throws Exception {
                HotelBookingRequest request = new HotelBookingRequest(
                                1, 999,
                                LocalDate.of(2026, 10, 1),
                                LocalDate.of(2026, 10, 4),
                                2, null);

                when(bookingService.createHotelBooking(any(HotelBookingRequest.class)))
                                .thenThrow(new ResourceNotFoundException("Không tìm thấy khách sạn với ID 999"));

                mockMvc.perform(post("/api/v1/bookings/hotel")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound());
        }
}
