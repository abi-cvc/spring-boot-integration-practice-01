package ec.edu.epn.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.epn.dto.AirportRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AirportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAirport() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Aeropuerto Mariscal Sucre");
        request.setCode("UIO");
        request.setCity("Quito");
        request.setCountry("ECU");

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Aeropuerto Mariscal Sucre"))
                .andExpect(jsonPath("$.code").value("UIO"))
                .andExpect(jsonPath("$.city").value("Quito"))
                .andExpect(jsonPath("$.country").value("ECU"));
    }

    @Test
    void shouldDeleteAirport() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Aeropuerto Mariscal Sucre");
        request.setCode("GYE");
        request.setCity("Guayaquil");
        request.setCountry("ECU");

        String response = createAirport(request);

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/airports/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/airports/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAirport() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Aeropuerto Mariscal Sucre");
        request.setCode("CUE");
        request.setCity("Cuenca");
        request.setCountry("ECU");

        String response = createAirport(request);

        long id = objectMapper.readTree(response).get("id").asLong();

        request.setName("Pepe");

        mockMvc.perform(put("/api/airports/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pepe"))
                .andExpect(jsonPath("$.code").value("CUE"));
    }

    private String createAirport(AirportRequest request) throws Exception {
        return mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
    @Test
    void shouldRejectDuplicateAirportCode() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Mariscal Sucre");
        request.setCode("UIO");
        request.setCity("Quito");
        request.setCountry("ECU");

        createAirport(request);

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindAllAirports() throws Exception {
        AirportRequest airport1 = new AirportRequest();
        airport1.setName("Mariscal Sucre");
        airport1.setCode("UIO");
        airport1.setCity("Quito");
        airport1.setCountry("ECU");

        AirportRequest airport2 = new AirportRequest();
        airport2.setName("Jose Joaquin");
        airport2.setCode("GYE");
        airport2.setCity("Guayaquil");
        airport2.setCountry("ECU");

        createAirport(airport1);
        createAirport(airport2);

        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldFindAirportById() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Mariscal Sucre");
        request.setCode("UIO");
        request.setCity("Quito");
        request.setCountry("ECU");

        String response = createAirport(request);

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/airports/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Mariscal Sucre"))
                .andExpect(jsonPath("$.code").value("UIO"));
    }

    @Test
    void shouldFindAirportByCode() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("Mariscal Sucre");
        request.setCode("UIO");
        request.setCity("Quito");
        request.setCountry("ECU");

        createAirport(request);

        mockMvc.perform(get("/api/airports/code/UIO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mariscal Sucre"))
                .andExpect(jsonPath("$.code").value("UIO"));
    }

    @Test
    void shouldReturn404WhenAirportNotFound() throws Exception {
        mockMvc.perform(get("/api/airports/9999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldRejectInvalidAirportRequest() throws Exception {
        AirportRequest request = new AirportRequest();

        request.setName("");
        request.setCode("AB");
        request.setCity("");
        request.setCountry("");

        mockMvc.perform(post("/api/airports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}