package net.dontcode.prj;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.dontcode.core.project.DontCodeProjectModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class GenerateProjectServiceTest {

    @Inject
    GenerateProjectService service;

    @Test
    public void testSimpleApplication () {

        //DontCodeProjectModel response=service.generateProjectJson("Please create a cooking recipe application");
        //Assertions.assertNotNull(response);
        //Assertions.assertTrue(response.content().creation().entities().length > 0);
    }

}
