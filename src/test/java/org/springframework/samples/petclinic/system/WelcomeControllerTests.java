package org.springframework.samples.petclinic.system;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link WelcomeController}
 *
 * @author Mukulika Dey
 */

public class WelcomeControllerTests {
		
	@Test
	public void testWelcome() {
		WelcomeController wmock = mock(WelcomeController.class);
	    when(wmock.welcome()).thenReturn("welcome");
	    String expected = "welcome";
	    assertEquals(expected, wmock.welcome());
	}

}
