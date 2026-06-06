package ec.edu.epn.integration;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import ec.edu.epn.dto.AirportRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AirportControllerIT {
    
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateAirport() throws Exception {
        AirportRequest airport = new AirportRequest();
        airport.setName("Mariscal Sucre");
        airport.setCity(null);
        airport.setCode("UIO");
        airport.setCountry("Ecuador");

        mockMvc.perform(post("/api/airports"));
    }

}
