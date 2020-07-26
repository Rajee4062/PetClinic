package org.springframework.samples.petclinic.web;


import org.assertj.core.util.Lists;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

/**
 *
 * @author Rajeshbabu
 */

@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
class OwnerTests {

    private static final int OWNER_ID = 2373810;

    @Autowired
    private OwnerController ownerController;

    @Autowired
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private Owner rajesh;

    @BeforeEach
    void setup() 
    {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ownerController).build();

        rajesh = new Owner();
        rajesh.setId(OWNER_ID);
        rajesh.setFirstName("rajesh babu");
        rajesh.setLastName("Mekathoti");
        rajesh.setAddress("193,miyapur");
        rajesh.setCity("Hyderabad");
        rajesh.setTelephone("8919325812");
        given(this.clinicService.findOwnerById(OWNER_ID)).willReturn(rajesh);

    }
    
     //Testing owner object created or not
    @Test	
    void ownerObjectCreationTest() throws Exception 
    {
        mockMvc.perform(get("/owners/new"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
    
    //process creation form
    @Test
    void ProcessCreationFormSuccessTest() throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "ram")
            .param("lastName", "babu")
            .param("address", "123 bapatla")
            .param("city", "guntur")
            .param("telephone", "12346789")
        )
            .andExpect(status().is3xxRedirection());
    }
    
    
    
    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "ram")
            .param("lastName", "Babu")
            .param("city", "guntur")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
    
    
    //findFrom
    @Test
    void initFormTest() throws Exception
    {
    	mockMvc.perform(get("/owners/find"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("owner"))
    	.andExpect(view().name("owners/findOwners"));
    	
    }
    

    // processFindForm
    
    @Test
    void processFindFormTest() throws Exception
    {
    	mockMvc.perform(get("/owners"))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("owner"))
    	.andExpect(model().attribute("owner",hasProperty("lastName", is(""))))
    	.andExpect(view().name("owners/findOwners"));

    }
    
    
    
    @Test
    void findOwnerTest() throws Exception 
    {
        given(this.clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(rajesh, new Owner()));

        mockMvc.perform(get("/owners"))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownersList"));
    }
    
    //Testing owner object
    @Test
    void showOwnerTest() throws Exception 
    {
    	
        mockMvc.perform(get("/owners/{ownerId}", OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attribute("owner", hasProperty("lastName", is("Mekathoti"))))
            .andExpect(model().attribute("owner", hasProperty("firstName", is("rajesh babu"))))
            .andExpect(model().attribute("owner", hasProperty("address", is("193,miyapur"))))
            .andExpect(model().attribute("owner", hasProperty("city", is("Hyderabad"))))
            .andExpect(model().attribute("owner", hasProperty("telephone", is("8919325812"))))
            .andExpect(view().name("owners/ownerDetails"));
    }
    

    @Test
    void testInitUpdateOwnerForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/edit", OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", hasProperty("lastName", is("Mekathoti"))))
            .andExpect(model().attribute("owner", hasProperty("firstName", is("rajesh babu"))))
            .andExpect(model().attribute("owner", hasProperty("address", is("193,miyapur"))))
            .andExpect(model().attribute("owner", hasProperty("city", is("Hyderabad"))))
            .andExpect(model().attribute("owner", hasProperty("telephone", is("8919325812"))))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }
    
    @Test
    void updateOwnerSuccessTest() throws Exception 
    {
        mockMvc.perform(post("/owners/{ownerId}/edit", OWNER_ID)
            .param("firstName", "raghav")
            .param("lastName", "nanda")
            .param("address", "110,kanuru")
            .param("city", "Vijayawada")
            .param("telephone", "46464")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name("redirect:/owners/{ownerId}"));
    }
    
    
    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", OWNER_ID)
            .param("firstName", "ram")
            .param("lastName", "babu")
            .param("city", "guntur")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    
    @Test
    void FindByLastNameTest() throws Exception {
        given(this.clinicService.findOwnerByLastName(rajesh.getLastName())).willReturn(Lists.newArrayList(rajesh));

        mockMvc.perform(get("/owners")
            .param("lastName", "leela")
        );
    }
    
}