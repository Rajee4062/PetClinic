package org.springframework.samples.petclinic.web;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Rajeshbabu
 *
 */
@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-core-config.xml", "classpath:spring/mvc-test-config.xml"})
public class PetTests 
{
	private static final int OWNER_ID = 2373810;
    private static final int PET_ID = 101;

    @Autowired
    private PetController petController;

   /* @Autowired
    private FormattingConversionServiceFactoryBean formattingConversionServiceFactoryBean;
*/
    @Autowired
    private ClinicService clinicService;

    private MockMvc mockMvc;
    private Pet dog;
    private PetType petType;
    private LocalDate birthDate = LocalDate.of(2017, 1, 13);
    @BeforeEach
    void setup() 
    {
    	this.mockMvc = MockMvcBuilders.standaloneSetup(petController).build();

    	//petType.set
    	petType = new PetType();
    	
    	petType.setId(101);
    	petType.setName("dog");    
    	
    	dog = new Pet();	
    	
    	dog.setId(PET_ID);	
    	dog.setName("snoop");
    	dog.setType(petType);
    	dog.setBirthDate(birthDate);
        given(this.clinicService.findPetTypes()).willReturn(Lists.newArrayList(petType));
        given(this.clinicService.findOwnerById(OWNER_ID)).willReturn(new Owner());
        given(this.clinicService.findPetById(PET_ID)).willReturn(new Pet());
        System.out.println("---------dog obj-------->"+dog);
    }

    
    @Test
    void initCreationFormTest() throws Exception
    {
      mockMvc.perform(get("/owners/{ownerId}/pets/new",OWNER_ID,PET_ID))
      .andExpect(status().isOk())
      .andExpect(model().attributeExists("pet"))
      .andExpect(view().name("pets/createOrUpdatePetForm"));
    }
   
    
    @Test
    void processCreationFormSuccessTest() throws Exception 
    {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", OWNER_ID)
           // .contentType(MediaType.APPLICATION_JSON)
            .param("name", "cooper")
            .param("type", "bull Dog")
            .param("birthDate", "2015/02/12")
        )
           .andExpect(status().isOk())
           //.andExpect(status().is3xxRedirection())
          // .andExpect(view().name("redirect:/owners/{ownerId}"));  
           .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessCreationFormSuccessErrorsTest() throws Exception 
    {
        mockMvc.perform(post("/owners/{ownerId}/pets/new",OWNER_ID, PET_ID)
            .param("name", "coop")
            .param("type", "hamster")
        )
        .andExpect(model().attributeExists("pet"))
        .andExpect(model().attributeHasErrors("pet"))
        .andExpect(view().name("pets/createOrUpdatePetForm"));
    }
    
    

    @Test
    void initUpdateFormTest() throws Exception
    {
    	mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit",OWNER_ID,PET_ID))
    	.andExpect(status().isOk())
    	.andExpect(model().attributeExists("pet"))
    	.andExpect(view().name("pets/createOrUpdatePetForm"));
    	
    }
    
    
    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", OWNER_ID, PET_ID)
            .param("name", "Betty")
            .param("type", "hamster")
            .param("birthDate", "2015/02/12")
        )
          //  .andExpect(status().is3xxRedirection())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit",OWNER_ID,PET_ID)
            .param("name", "Betty")
            .param("birthDate", "2015/02/12")
        )
            .andExpect(model().attributeHasNoErrors("owner"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    public static String asJsonString(final MockMvc obj) 
    {
        try 
        {
            return new ObjectMapper().writeValueAsString(obj);
        } 
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
}
