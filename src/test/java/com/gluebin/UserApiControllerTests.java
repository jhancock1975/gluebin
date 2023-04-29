package com.gluebin;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(UserApiController.class)
public class UserApiControllerTests {
	private static final String END_POINT_PATH = "/users";
	
	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@MockBean private UserService service;
	
	@Test
	public void testAddShouldReturn400BadRequest() throws Exception {
		User newUser = new User().email("").firstName("");
		
		String requestBody = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isBadRequest())
				.andDo(print())
		;
	}
	
	@Test
	public void testAddShouldReturn201Created() throws Exception {
		User newUser = new User().email("david.parker@gmail.com")
								 .firstName("David").lastName("Parker")
								 .password("avid808");
		
		Mockito.when(service.add(newUser)).thenReturn(newUser.id(1L));
		
		String requestBody = objectMapper.writeValueAsString(newUser);
		
		mockMvc.perform(post(END_POINT_PATH).contentType("application/json")
				.content(requestBody))
				.andExpect(status().isCreated())
				.andDo(print())
		;
		
		Mockito.verify(service, times(1)).add(newUser);
	}	
	
	@Test
	public void testGetShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.when(service.get(userId)).thenThrow(UserNotFoundException.class);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());
	}
	
	@Test
	public void testGetShouldReturn200OK() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		String email = "david.parker@gmail.com";
		
		User user = new User().email(email)
				 .firstName("David").lastName("Parker")
				 .password("avid808")
				 .id(userId);
		
		Mockito.when(service.get(userId)).thenReturn(user);
		
		mockMvc.perform(get(requestURI))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$.email", is(email)))
			.andDo(print());
	}
	
	@Test
	public void testListShouldReturn204NoContent() throws Exception {
		Mockito.when(service.list()).thenReturn(new ArrayList<>());
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isNoContent())
			.andDo(print());		
	}
	
	@Test
	public void testListShouldReturn200OK() throws Exception {
		User user1 = new User().email("david.parker@gmail.com")
				 .firstName("David").lastName("Parker")
				 .password("avid808")
				 .id(1L);
		
		User user2 = new User().email("john.doe@gmail.com")
				 .firstName("John").lastName("Doe")
				 .password("johnoho2")
				 .id(2L);
		
		List<User> listUser = List.of(user1, user2);
		
		Mockito.when(service.list()).thenReturn(listUser);
		
		mockMvc.perform(get(END_POINT_PATH))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json"))
			.andExpect(jsonPath("$[0].email", is("david.parker@gmail.com")))
			.andExpect(jsonPath("$[1].email", is("john.doe@gmail.com")))
			.andDo(print());		
	}	
	
	@Test
	public void testDeleteShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.doThrow(UserNotFoundException.class).when(service).delete(userId);;
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNotFound())
			.andDo(print());		
	}
	
	@Test
	public void testDeleteShouldReturn200OK() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		Mockito.doNothing().when(service).delete(userId);;
		
		mockMvc.perform(delete(requestURI))
			.andExpect(status().isNoContent())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn404NotFound() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		User user = new User().email("david.parker@gmail.com")
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		Mockito.when(service.update(user)).thenThrow(UserNotFoundException.class);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isNotFound())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn400BadRequest() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		User user = new User().email("david.parker")
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isBadRequest())
			.andDo(print());		
	}
	
	@Test
	public void testUpdateShouldReturn200OK() throws Exception {
		Long userId = 123L;
		String requestURI = END_POINT_PATH + "/" + userId;
		
		String email = "david.parker@gmail.com";		
		User user = new User().email(email)
							 .firstName("David").lastName("Parker")
							 .password("avid808")
							 .id(userId);
		
		Mockito.when(service.update(user)).thenReturn(user);
		
		String requestBody = objectMapper.writeValueAsString(user);
		
		mockMvc.perform(put(requestURI).contentType("application/json").content(requestBody))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email", is(email)))
			.andDo(print());		
	}	
}
