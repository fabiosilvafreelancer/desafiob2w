import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import br.com.swapi.StarWarsApiApplication;
import br.com.swapi.exception.NoSuchPlanetInSwapiException;
import br.com.swapi.exception.PlanetNotFoundException;
import br.com.swapi.externals.swapiexternal.service.SwapiService;
import br.com.swapi.model.Planet;
import br.com.swapi.repository.PlanetRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoEmbendedForTests.class, StarWarsApiApplication.class, EmbeddedMongoAutoConfiguration.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestService {
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockTeste;    
    
    @Autowired
    private WebApplicationContext webContext;

    @SuppressWarnings("rawtypes")
	private HttpMessageConverter converter;
    
    @Autowired
    private PlanetRepository planetRepository;
    
    @Autowired
    private SwapiService swapiService;
    
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.converter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);
        assertNotNull("not empty", this.converter);
    }    
    
    
    @SuppressWarnings("unchecked")
	private String planetToJsonForRestRequest(Object object) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.converter.write(object, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Before
    public void setup() throws Exception {
        this.mockTeste = webAppContextSetup(webContext).build();
    }    
    
    @Test 
    public void findAll() throws Exception {
    	Planet[] planets = new Planet[] {getExistingPlanetByName("Coruscant"), getExistingPlanetByName("Endor"), getExistingPlanetByName("Bespin")};
    	String planetsJson = planetToJsonForRestRequest(planets);
    	
    	this.mockTeste.perform(get("/planets").contentType(contentType))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(contentType))
    		.andExpect(content().json(planetsJson));    	
    }
    
    @Test
    public void findPlanetById() throws Exception {    	
    	Planet planet = getExistingPlanet();
    	
    	this.mockTeste.perform(get("/planets/" + planet.getId())).andExpect(status().isOk()).andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.id", is(planet.getId())))
            .andExpect(jsonPath("$.name", is(planet.getName())))
            .andExpect(jsonPath("$.climate", is(planet.getClimate())))
    		.andExpect(jsonPath("$.terrain", is(planet.getTerrain())));
    }
    
    @Test
    public void findPlanetByIdNotFound() throws Exception {
    	String id = "thisidnotexists";
    	this.mockTeste.perform(get("/planets/" + id)).andExpect(status().isNotFound());        	
    }
    
    @Test 
    public void findPlanetByName() throws Exception {
    	Planet planet = getExistingPlanet();
    	
    	this.mockTeste.perform(get("/planets/find?name=" + planet.getName())).andExpect(status().isOk()).andExpect(content().contentType(contentType))
            .andExpect(jsonPath("$.id", is(planet.getId())))
            .andExpect(jsonPath("$.name", is(planet.getName())))
            .andExpect(jsonPath("$.climate", is(planet.getClimate())))
            .andExpect(jsonPath("$.apparitionsCount", is(planet.getApparitionsCount())))
    		.andExpect(jsonPath("$.terrain", is(planet.getTerrain())));    	
    }
    
    @Test
    public void findPlanetByNameNotFound() throws Exception {
    	String name = "thisnamenotexists";
    	this.mockTeste.perform(get("/planets/find?name=" + name)).andExpect(status().isNotFound());        	
    }    

    @Test
    public void createPlanet() throws Exception {
    	Planet planet = getExistingPlanet();
    	String name = planet.getName();
    	String climate = planet.getClimate();
    	String terrain = planet.getTerrain();

    	int apparitionsCount = planet.getApparitionsCount(); 
    	    	
    	String json = planetToJsonForRestRequest(new Planet(name, climate, terrain));
    	
    	this.mockTeste.perform(
			post("/planets").contentType(contentType).content(json)).andExpect(status().isCreated()).andExpect(content().contentType(contentType))
	        .andExpect(jsonPath("$.name", is(name)))
	        .andExpect(jsonPath("$.climate", is(climate)))
	        .andExpect(jsonPath("$.terrain", is(terrain)))
	        .andExpect(jsonPath("$.apparitionsCount", is(apparitionsCount)));    	        		
    }
    
    @Test
    public void createPlanetWithDuplicatedName() throws Exception {    
    	Planet existingPlanet = this.getExistingPlanet();
    	String json = planetToJsonForRestRequest(new Planet(existingPlanet.getName(), "climate for test", "terrain for test"));
    	this.mockTeste.perform(post("/planets").contentType(contentType).content(json)).andExpect(status().isConflict());
    }
    
    @Test
    public void updatePlanet() throws Exception {
    	Planet planet = getExistingPlanet();
    	String name = planet.getName();
    	String climate = planet.getClimate();
    	String terrain = planet.getTerrain();

    	int apparitionsCount = planet.getApparitionsCount(); 
    	    	
    	String json = planetToJsonForRestRequest(new Planet(name, climate, terrain));
    	
    	this.mockTeste.perform(
    		put("/planets/" + planet.getId()).contentType(contentType).content(json)).andExpect(status().isOk()).andExpect(content().contentType(contentType))
	        .andExpect(jsonPath("$.name", is(name)))
	        .andExpect(jsonPath("$.climate", is(climate)))
	        .andExpect(jsonPath("$.terrain", is(terrain)))
	        .andExpect(jsonPath("$.apparitionsCount", is(apparitionsCount))
	    );    	        		
    }
    
    @Test
    public void updatePlanetWithDuplicatedName() throws Exception {    
    	Planet existingPlanet = this.getExistingPlanet();
    	String json = planetToJsonForRestRequest(new Planet(existingPlanet.getName(), "climate for test", "terrain for test"));
    	this.mockTeste.perform(put("/planets" + existingPlanet.getId()).contentType(contentType).content(json)).andExpect(status().isConflict());
    }

    @Test(expected = NoSuchElementException.class)
    public void deleteById() throws Exception {
    	Planet planet = getExistingPlanet();
    	this.mockTeste.perform(delete("/planets/" + planet.getId()).contentType(contentType)).andExpect(status().isNoContent());    
    	planetRepository.findById(planet.getId()).get();
    }
    
    @Test
    public void deleteByIdNotExists() throws Exception {
    	String id = "thisidnotexists";
    	this.mockTeste.perform(delete("/planets/" + id).contentType(contentType)).andExpect(status().isNotFound());      	
    }

    private Planet getExistingPlanet() throws RestClientException {
    	String name = "Kamino";
    	return getExistingPlanetByName(name);
    }
    
    private Planet getExistingPlanetByName(String name) {
    	Planet planet;
    	if (planetRepository.findByName(name).size() > 0) {
    		planet = planetRepository.findByName(name).get(0);
    	} else {
    		planet = planetRepository.insert(new Planet(name, "climate for test", "terrain for test"));
    	}
    	
    	try {
			planet.setApparitionsCount(swapiService.getSwapiPlanet(planet.getName()).getApparitionsCount());
		} catch (NoSuchPlanetInSwapiException e) {
			planet.setApparitionsCount(0);
		} catch (PlanetNotFoundException e) {
			throw new Error(e);
		} 
    	
    	return planet;
    }
}
