package org.springframework.samples.petclinic.system;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for {@link CrashController}
 *
 * @author Colin But
 */
@RunWith(SpringRunner.class)
// Waiting https://github.com/spring-projects/spring-boot/issues/5574
@Ignore
@WebMvcTest(controllers = CrashController.class)
public class CrashControllerTests {

	@Autowired
    private CrashController crashController;

    @Autowired
    private SimpleMappingExceptionResolver simpleMappingExceptionResolver;
    private MockMvc mockMvc;

    @Before
    public void setup() {
    	this.mockMvc = MockMvcBuilders
                .standaloneSetup(crashController)
                .setHandlerExceptionResolvers(simpleMappingExceptionResolver)
                .build();    	
    }
    
    @Test
    public void testTriggerException() throws Exception {
        mockMvc.perform(get("/oups")).andExpect(view().name("exception"))
                .andExpect(model().attributeExists("exception"))
                .andExpect(forwardedUrl("exception")).andExpect(status().isOk());
    }
}
