package ru.v_and_a.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.v_and_a.controller.OrderController;
import ru.v_and_a.enums.OrderStatus;

import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerApiTest {
    private static final String uuidPattern = "\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createOrder_shouldReturnValidUuid() throws Exception {
        mockMvc.perform(post("/orders/createOrder"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesRegex(uuidPattern)));
    }

    @Test
    void getStatusByUuid_shouldReturnStatusMessage() throws Exception {
        mockMvc.perform(get("/orders/getStatusByUuid"))
                .andExpect(status().isOk())
                .andExpect(content().string(OrderStatus.CREATED.getValue()));
    }
}
