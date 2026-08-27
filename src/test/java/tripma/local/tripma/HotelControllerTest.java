package tripma.local.tripma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tripma.local.tripma.dto.Hotel.HotelRequest;
import tripma.local.tripma.dto.Hotel.HotelResponse;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.service.HotelService;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class HotelControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper;

    @MockitoBean
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.objectMapper = new ObjectMapper();
    }

    private HotelResponse sampleResponse() {
        return new HotelResponse(1, "Grand Palace", "123 Main St", "Hanoi",
                "Vietnam", 5, new BigDecimal("150.00"), "Luxury hotel in Hanoi");
    }

    private HotelRequest sampleRequest() {
        return new HotelRequest("Grand Palace", "123 Main St", "Hanoi",
                "Vietnam", 5, new BigDecimal("150.00"), "Luxury hotel in Hanoi");
    }

    @Test
    void shouldFetchAllHotels() throws Exception {
        Page<HotelResponse> page = new PageImpl<>(List.of(sampleResponse()));
        when(hotelService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Grand Palace"))
                .andExpect(jsonPath("$.content[0].city").value("Hanoi"))
                .andExpect(jsonPath("$.content[0].rating").value(5));
    }

    @Test
    void shouldFetchHotelById() throws Exception {
        when(hotelService.findById(1)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/v1/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hotel_id").value(1))
                .andExpect(jsonPath("$.name").value("Grand Palace"));
    }

    @Test
    void shouldReturn404WhenHotelNotFound() throws Exception {
        when(hotelService.findById(99)).thenThrow(new ResourceNotFoundException("Hotel not found with id: 99"));

        mockMvc.perform(get("/api/v1/hotels/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateHotel() throws Exception {
        when(hotelService.create(any(HotelRequest.class))).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/v1/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hotel_id").value(1))
                .andExpect(jsonPath("$.name").value("Grand Palace"));
    }

    @Test
    void shouldUpdateHotel() throws Exception {
        HotelRequest updatedRequest = new HotelRequest("Updated Palace", "456 New Rd", "Ho Chi Minh",
                "Vietnam", 4, new BigDecimal("120.00"), "Updated description");
        HotelResponse updatedResponse = new HotelResponse(1, "Updated Palace", "456 New Rd", "Ho Chi Minh",
                "Vietnam", 4, new BigDecimal("120.00"), "Updated description");

        when(hotelService.update(eq(1), any(HotelRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/hotels/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Palace"))
                .andExpect(jsonPath("$.city").value("Ho Chi Minh"));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentHotel() throws Exception {
        when(hotelService.update(eq(99), any(HotelRequest.class)))
                .thenThrow(new ResourceNotFoundException("Hotel not found with id: 99"));

        mockMvc.perform(put("/api/v1/hotels/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteHotel() throws Exception {
        doNothing().when(hotelService).delete(1);

        mockMvc.perform(delete("/api/v1/hotels/1"))
                .andExpect(status().isNoContent());

        verify(hotelService, times(1)).delete(1);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentHotel() throws Exception {
        doThrow(new ResourceNotFoundException("Hotel not found with id: 99"))
                .when(hotelService).delete(99);

        mockMvc.perform(delete("/api/v1/hotels/99"))
                .andExpect(status().isNotFound());
    }
}
