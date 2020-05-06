package org.snomed.snowstorm.fhir.services;

import org.hl7.fhir.r4.model.*;
import org.hl7.fhir.r4.model.Parameters.ParametersParameterComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snomed.snowstorm.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfig.class)
public class CodeSystemProviderLookupTest extends AbstractFHIRTest {

	@Test
	public void testSingleConceptRecovery() throws FHIROperationException {
		String url = "http://localhost:" + port + "/fhir/CodeSystem/$lookup?system=http://snomed.info/sct&code=" + sampleSCTID + "&_format=json";
		Parameters p = get(url);
		assertNotNull(p);
	}
	
	@Test
	public void testSinglePropertiesRecovery() throws FHIROperationException {
		String url = "http://localhost:" + port + "/fhir/CodeSystem/$lookup?system=http://snomed.info/sct&code=" + sampleSCTID + "&property=normalForm&_format=json";
		Parameters p = get(url);
		/*for (ParametersParameterComponent parameter : p.getParameter()) {
			logger.info(toString(parameter, ""));
		}*/
		String normalFormProperty = toString(getProperty(p, "normalForm"));
		assertNotNull(normalFormProperty);
	}

	@Test
	public void testMultipleConceptPropertiesRecovery() throws FHIROperationException {
		String url = "http://localhost:" + port + "/fhir/CodeSystem/$lookup?system=http://snomed.info/sct&code=" + sampleSCTID + "&property=normalForm&property=sufficientlyDefined&_format=json";
		Parameters p = get(url);
		
		String normalFormProperty = toString(getProperty(p, "normalForm"));
		assertNotNull(normalFormProperty);
		
		String sdProperty = toString(getProperty(p, "sufficientlyDefined"));
		assertNotNull(sdProperty);
	}
	
}
