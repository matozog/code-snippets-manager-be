package com.snippets.manager.snippetsmanager.controllers;

import com.jayway.jsonpath.JsonPath;
import com.snippets.manager.snippetsmanager.TestcontainersConfiguration;
import com.snippets.manager.snippetsmanager.models.AppUser;
import com.snippets.manager.snippetsmanager.models.Tag;
import com.snippets.manager.snippetsmanager.repositories.AppUserRepository;
import com.snippets.manager.snippetsmanager.repositories.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.snippets.manager.snippetsmanager.TestUsers.alice;
import static com.snippets.manager.snippetsmanager.TestUsers.bob;
import static com.snippets.manager.snippetsmanager.TestUsers.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
class CodeSnippetControllerTests {

    private static final String SNIPPETS_URL = "/api/code-snippets";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void requestsWithoutTokenAreRejected() throws Exception {
        mockMvc.perform(get(SNIPPETS_URL)).andExpect(status().isUnauthorized());
        mockMvc.perform(post(SNIPPETS_URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "anonymous"}"""))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tag")).andExpect(status().isUnauthorized());
    }

    @Test
    void firstRequestCreatesUserAndLaterRequestsUpdateProfile() throws Exception {
        mockMvc.perform(get(SNIPPETS_URL).with(user("google-sub-carol", "carol@example.com", "Carol")))
                .andExpect(status().isOk());
        mockMvc.perform(get(SNIPPETS_URL).with(user("google-sub-carol", "carol@new.example", "Carol B.")))
                .andExpect(status().isOk());

        AppUser carol = appUserRepository.findByGoogleSub("google-sub-carol").orElseThrow();
        assertThat(carol.getEmail()).isEqualTo("carol@new.example");
        assertThat(carol.getName()).isEqualTo("Carol B.");
        assertThat(carol.getCreatedAt()).isNotNull();
    }

    @Test
    void usersSeeAndChangeOnlyTheirOwnSnippets() throws Exception {
        Number aliceSnippetId = JsonPath.read(createAndGetBody(alice(), """
                {"name": "alice's snippet"}"""), "$.idSnippet");
        create(bob(), """
                {"name": "bob's snippet"}""").andExpect(status().isCreated());

        mockMvc.perform(get(SNIPPETS_URL).with(alice()))
                .andExpect(jsonPath("$[*].name", contains("alice's snippet")));
        mockMvc.perform(get(SNIPPETS_URL).with(bob()))
                .andExpect(jsonPath("$[*].name", contains("bob's snippet")));

        // another user's snippet is reported as missing
        String aliceSnippetUrl = SNIPPETS_URL + "/" + aliceSnippetId;
        mockMvc.perform(get(aliceSnippetUrl).with(bob())).andExpect(status().isNotFound());
        mockMvc.perform(put(aliceSnippetUrl).with(bob()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "hijacked"}"""))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(aliceSnippetUrl).with(bob())).andExpect(status().isNotFound());

        mockMvc.perform(get(aliceSnippetUrl).with(alice()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("alice's snippet"));
    }

    @Test
    void tagsAreSeparatePerUser() throws Exception {
        Number aliceTagId = JsonPath.read(createAndGetBody(alice(), """
                {"name": "a", "tags": [{"name": "js"}]}"""), "$.tags[0].idTag");
        Number bobTagId = JsonPath.read(createAndGetBody(bob(), """
                {"name": "b", "tags": [{"name": "js"}]}"""), "$.tags[0].idTag");

        assertThat(bobTagId.longValue()).isNotEqualTo(aliceTagId.longValue());
        mockMvc.perform(get("/api/tag").with(bob()))
                .andExpect(jsonPath("$[*].idTag", contains(bobTagId.intValue())));
    }

    @Test
    void createIgnoresClientSuppliedIdAndDate() throws Exception {
        create(alice(), """
                {"idSnippet": 1760980000000, "addedDate": "1970-01-01", "name": "debounce",
                 "programmingLanguage": "js", "tags": [{"name": "js"}]}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSnippet", not(1760980000000L)))
                .andExpect(jsonPath("$.addedDate", not(startsWith("1970"))))
                .andExpect(jsonPath("$.tags[0].name").value("js"))
                .andExpect(jsonPath("$.tags[0].idTag").isNumber())
                .andExpect(jsonPath("$.tags[0].addedDate").isNotEmpty());
    }

    @Test
    void addedDateIsUtcTimestampThatSurvivesDatabaseRoundTrip() throws Exception {
        String created = createAndGetBody(alice(), """
                {"name": "dated"}""");
        Number id = JsonPath.read(created, "$.idSnippet");
        String addedDate = JsonPath.read(created, "$.addedDate");

        assertThat(addedDate).endsWith("Z");
        assertThat(Instant.parse(addedDate)).isCloseTo(Instant.now(), within(1, ChronoUnit.MINUTES));

        // force the next read to come from the database, not from the persistence context
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get(SNIPPETS_URL + "/" + id).with(alice()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addedDate").value(addedDate));
    }

    @Test
    void createAcceptsMissingTags() throws Exception {
        create(alice(), """
                {"name": "no tags", "tags": null}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags", hasSize(0)));
    }

    @Test
    void createReusesExistingTagsByNameAndIgnoresClientTagIds() throws Exception {
        Number existingTagId = JsonPath.read(createAndGetBody(alice(), """
                {"name": "first", "tags": [{"name": "js"}]}"""), "$.tags[0].idTag");

        create(alice(), """
                {"name": "second", "tags": [{"idTag": 999, "name": "js"}, {"name": " js "}, {"name": "react"}]}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tags[*].name", contains("js", "react")))
                .andExpect(jsonPath("$.tags[0].idTag").value(existingTagId.longValue()));

        assertThat(tagRepository.findAll()).extracting(Tag::getName).containsExactlyInAnyOrder("js", "react");
    }

    @Test
    void createRejectsInvalidSnippetWithFieldErrors() throws Exception {
        create(alice(), """
                {"name": "  ", "tags": [{"name": "js"}, {"name": ""}]}""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed."))
                .andExpect(jsonPath("$.errors.name").value("Name is required."))
                .andExpect(jsonPath("$.errors['tags[1].name']").value("Tag name is required."));
    }

    @Test
    void createRejectsMalformedJson() throws Exception {
        create(alice(), "{\"name\": ").andExpect(status().isBadRequest());
    }

    @Test
    void updateReplacesFieldsAndTagsButKeepsIdAndAddedDate() throws Exception {
        String created = createAndGetBody(alice(), """
                {"name": "original", "tags": [{"name": "js"}]}""");
        Number id = JsonPath.read(created, "$.idSnippet");
        String addedDate = JsonPath.read(created, "$.addedDate");

        mockMvc.perform(put(SNIPPETS_URL + "/" + id).with(alice()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"idSnippet": 5, "addedDate": "1970-01-01", "name": "renamed", "favorite": true,
                         "tags": [{"name": "java"}]}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSnippet").value(id.longValue()))
                .andExpect(jsonPath("$.name").value("renamed"))
                .andExpect(jsonPath("$.favorite").value(true))
                .andExpect(jsonPath("$.addedDate").value(addedDate))
                .andExpect(jsonPath("$.tags[*].name", contains("java")));
    }

    @Test
    void missingSnippetReturnsNotFoundProblem() throws Exception {
        mockMvc.perform(get(SNIPPETS_URL + "/999").with(alice()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Snippet 999 not found."));
        mockMvc.perform(put(SNIPPETS_URL + "/999").with(alice()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name": "missing"}"""))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(SNIPPETS_URL + "/999").with(alice()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRemovesSnippet() throws Exception {
        Number id = JsonPath.read(createAndGetBody(alice(), """
                {"name": "to remove"}"""), "$.idSnippet");

        mockMvc.perform(delete(SNIPPETS_URL + "/" + id).with(alice())).andExpect(status().isNoContent());
        mockMvc.perform(get(SNIPPETS_URL + "/" + id).with(alice())).andExpect(status().isNotFound());
    }

    @Test
    void findAllReturnsSnippetsWithTags() throws Exception {
        create(alice(), """
                {"name": "listed", "tags": [{"name": "b"}, {"name": "a"}]}""");

        mockMvc.perform(get(SNIPPETS_URL).with(alice()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("listed"))
                .andExpect(jsonPath("$[0].tags[*].name", contains("a", "b")));
    }

    @Test
    void corsAllowsOnlyConfiguredOriginsWithoutRequiringToken() throws Exception {
        mockMvc.perform(options(SNIPPETS_URL)
                        .header(HttpHeaders.ORIGIN, "https://allowed.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "authorization,content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://allowed.example"))
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, containsStringIgnoringCase("authorization")));

        mockMvc.perform(options("/api/tag")
                        .header(HttpHeaders.ORIGIN, "https://evil.example")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }

    private ResultActions create(RequestPostProcessor user, String json) throws Exception {
        return mockMvc.perform(post(SNIPPETS_URL).with(user).contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private String createAndGetBody(RequestPostProcessor user, String json) throws Exception {
        return create(user, json).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }
}
