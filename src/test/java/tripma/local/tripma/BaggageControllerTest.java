package tripma.local.tripma;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tripma.local.tripma.controller.BaggageController;
import tripma.local.tripma.dto.Baggage.BaggageRequest;
import tripma.local.tripma.dto.Baggage.BaggageResponse;
import tripma.local.tripma.exception.ResourceNotFoundException;
import tripma.local.tripma.service.BaggageService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @WebMvcTest: Chỉ load web layer (controller + exception handler), KHÔNG load service/repo/DB
// → nhanh hơn @SpringBootTest rất nhiều, phù hợp cho unit test controller
@WebMvcTest(BaggageController.class)
class BaggageControllerTest {

    // MockMvc: công cụ gửi HTTP request giả lập mà không cần start server thật
    @Autowired
    private MockMvc mockMvc;

    // @MockitoBean: tạo mock object thay thế BaggageService thật
    // → controller sẽ gọi mock này thay vì service thật (không cần DB)
    @MockitoBean
    private BaggageService baggageService;

    // === POSITIVE TEST ===
    // Kịch bản: GET /api/baggage/1 → service tìm thấy → trả 200 + JSON body
    @Test
    void findById_found_returns200() throws Exception {
        // 1. ARRANGE: Giả lập service trả về 1 BaggageResponse khi gọi findById(1)
        BaggageResponse response = new BaggageResponse(1, 10, 20, 2, 50.0);
        when(baggageService.findById(1)).thenReturn(response);

        // 2. ACT + ASSERT: Gửi GET request và kiểm tra kết quả
        mockMvc.perform(get("/api/baggage/1"))                  // Gửi GET /api/baggage/1
                .andExpect(status().isOk())                     // Status phải là 200
                .andExpect(jsonPath("$.baggageId").value(1))    // JSON field "baggageId" = 1
                .andExpect(jsonPath("$.passengerId").value(10)) // JSON field "passengerId" = 10
                .andExpect(jsonPath("$.flightId").value(20))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.fee").value(50.0));
    }

    // === NEGATIVE TEST ===
    // Kịch bản: GET /api/baggage/999 → service throw ResourceNotFoundException → trả 404
    @Test
    void findById_notFound_returns404() throws Exception {
        // 1. ARRANGE: Giả lập service throw exception khi gọi findById(999)
        when(baggageService.findById(999))
                .thenThrow(new ResourceNotFoundException("Baggage not found with id: 999"));

        // 2. ACT + ASSERT: Gửi GET request và kiểm tra trả 404
        mockMvc.perform(get("/api/baggage/999"))
                .andExpect(status().isNotFound());  // Status phải là 404
    }
    @Test
    void create_validRequest_returns201() throws Exception {
        BaggageResponse response = new BaggageResponse(1, 10, 20, 2, 50.0);
        when(baggageService.create(any(BaggageRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/baggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passengerId\":10,\"flightId\":20,\"quantity\":2,\"fee\":50.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.baggageId").value(1))
                .andExpect(jsonPath("$.passengerId").value(10))
                .andExpect(jsonPath("$.flightId").value(20))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.fee").value(50.0));
    }

    @Test
    void create_missingField_returns400() throws Exception {
        mockMvc.perform(post("/api/baggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"flightId\":20,\"quantity\":2,\"fee\":50.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_negativeQuantity_returns400() throws Exception {
        mockMvc.perform(post("/api/baggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passengerId\":10,\"flightId\":20,\"quantity\":-3,\"fee\":50.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validRequest_returns200() throws Exception {
        BaggageResponse response = new BaggageResponse(1, 10, 20, 3, 75.0);
        when(baggageService.update(eq(1), any(BaggageRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/baggage/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"passengerId\":10,\"flightId\":20,\"quantity\":3,\"fee\":75.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baggageId").value(1))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.fee").value(75.0));
    }

    @Test
    void delete_found_returns204() throws Exception {
        doNothing().when(baggageService).delete(1);

        mockMvc.perform(delete("/api/baggage/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Baggage not found with id: 999"))
                .when(baggageService).delete(999);

        mockMvc.perform(delete("/api/baggage/999"))
                .andExpect(status().isNotFound());
    }
}
