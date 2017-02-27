package org.shanoir.ng.controller.rest;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for role controller.
 *
 * @author msimon
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class RoleApiControllerTestIT extends KeycloakControllerTestIT {

	private static final String REQUEST_PATH = "/role";
	private static final String REQUEST_PATH_FOR_ALL = REQUEST_PATH + "/all";
	
    @Autowired
    private TestRestTemplate restTemplate;

	@Test
	public void findRolesProtected() {
		final ResponseEntity<String> response = restTemplate.getForEntity(REQUEST_PATH_FOR_ALL, String.class);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	public void findRolesWithLogin() {
		HttpEntity<String> entity = new HttpEntity<String>(null, getHeadersWithToken());
		
		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH_FOR_ALL, HttpMethod.GET, entity, String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	@Ignore
	public void findRolesWithBadRole() {
		HttpEntity<String> entity = new HttpEntity<String>(null, getHeadersWithToken());
		
		final ResponseEntity<String> response = restTemplate.exchange(REQUEST_PATH_FOR_ALL, HttpMethod.GET, entity, String.class);
		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	}
	
}