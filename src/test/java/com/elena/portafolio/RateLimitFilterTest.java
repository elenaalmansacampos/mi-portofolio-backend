package com.elena.portafolio;

import com.elena.portafolio.security.filter.RateLimitFilter;

import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RateLimitFilterTest {

    private static final String TEST_IP = "203.0.113.77";


    @Test
    void bloqueaCon429TrasSuperarElLimiteYPermiteOtrasIps() throws Exception {
        RateLimitFilter filter = new RateLimitFilter(3, 60);

        for (int attempt = 1; attempt <= 3; attempt++) {
            assertEquals(200, perform(filter, TEST_IP).getStatus());
        }

        MockHttpServletResponse blocked = perform(filter, TEST_IP);

        assertEquals(429, blocked.getStatus());
        assertEquals("60", blocked.getHeader("Retry-After"));

        assertEquals(200, perform(filter, "198.51.100.10").getStatus());
    }


    private MockHttpServletResponse perform(
            RateLimitFilter filter,
            String ip
    ) throws Exception {

        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("POST");
        request.setServletPath("/api/contact");
        request.setRemoteAddr(ip);

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
        });

        return response;
    }
}
