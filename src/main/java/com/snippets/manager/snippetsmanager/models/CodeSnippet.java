package com.snippets.manager.snippetsmanager.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(exclude = {"gitHubUsers", "tags", "categories", "idSnippet", "gist", "favorite"})
@Getter
@Setter
@Table(name = "CODE_SNIPPET")
public class CodeSnippet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_SNIPPET", nullable = false)
    private Long idSnippet;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "favorite")
    private boolean favorite = false;

    @Column(name = "programming_language")
    private String programmingLanguage;

    @Column(name = "type")
    private String type;

    @Column(name = "added_from")
    private String addedFrom;

    @Column(name = "added_date")
    private Date addedDate;

    @ManyToMany(mappedBy = "codeSnippets", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Tag> tags = new HashSet<>();
}
