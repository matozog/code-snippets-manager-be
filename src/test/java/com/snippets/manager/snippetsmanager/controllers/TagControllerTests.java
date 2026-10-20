package com.snippets.manager.snippetsmanager.controllers;

import com.snippets.manager.snippetsmanager.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.snippets.manager.snippetsmanager.TestUsers.alice;
import static com.snippets.manager.snippetsmanager.TestUsers.bob;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class TagControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createAssignsIdAndRejectsDuplicatesOfTheSameUser() throws Exception {
        mockMvc.perform(post("/api/tag").with(alice()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"idTag": 1, "name": " js "}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("js"))
                .andExpect(jsonPath("$.idTag").isNumber())
                .andExpect(jsonPath("$.addedDate").isNotEmpty());

        mockMvc.perform(post("/api/tag").with(alice()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "js"}"""))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Tag 'js' already exists."));

        // the same name is free for another user
        mockMvc.perform(post("/api/tag").with(bob()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "js"}"""))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/tag").with(bob()))
                .andExpect(jsonPath("$[*].name", contains("js")));
    }

    @Test
    void createRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/tag").with(alice()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": " "}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("Tag name is required."));
    }
}
