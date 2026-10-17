package com.snippets.manager.snippetsmanager.models;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
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

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "SNIPPET_TAG",
            joinColumns = {@JoinColumn(name = "ID_SNIPPET")},
            inverseJoinColumns = {@JoinColumn(name = "ID_TAG")})
    private Set<Tag> tags = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CodeSnippet other = (CodeSnippet) o;
        return getIdSnippet() != null && Objects.equals(getIdSnippet(), other.getIdSnippet());
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
