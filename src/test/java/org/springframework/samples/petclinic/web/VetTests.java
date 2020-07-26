package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringJUnitWebConfig(locations = {"classpath:spring/mvc-test-config.xml", "classpath:spring/mvc-core-config.xml"})
public class VetTests 
{
	 @Autowired
	    private VetController vetController;

	    @Autowired
	    private ClinicService clinicService;

	    private MockMvc mockMvc;

	    private VetRepository vetRepository;

	    @BeforeEach
	    void setup() 
	    {
	        this.mockMvc = MockMvcBuilders.standaloneSetup(vetController).build();
	        Vet james = new Vet();
	        james.setFirstName("James");
	        james.setLastName("Carter");
	        james.setId(1);
	        Vet helen = new Vet();
	        helen.setFirstName("Helen");
	        helen.setLastName("Leary");
	        helen.setId(2);
	        Specialty radiology = new Specialty();
	        radiology.setId(1);
	        radiology.setName("radiology");
	        helen.addSpecialty(radiology);
	        given(this.clinicService.findVets()).willReturn(Lists.newArrayList(james,helen));
			//given(this.vetRepository.findAll()).willReturn(Lists.newArrayList(helen));

	    }
	    
	    @Test
	    void showVetListTest() throws Exception
	    {
	    	mockMvc.perform(get("/vets.html"))
	    	.andExpect(status().isOk())
	        .andExpect(model().attributeExists("vets"))
	        .andExpect(view().name("vets/vetList"));
	    
	    }
}
