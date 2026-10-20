package com.snippets.manager.snippetsmanager.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "CODE_SNIPPET")
public class CodeSnippet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SNIPPET", nullable = false)
    private Long idSnippet;

    @Column(name = "name", nullable = false)
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
    private Instant addedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "SNIPPET_TAG",
            joinColumns = {@JoinColumn(name = "ID_SNIPPET")},
            inverseJoinColumns = {@JoinColumn(name = "ID_TAG")})
    private Set<Tag> tags = new HashSet<>();

    @PrePersist
    void onCreate() {
        // database keeps microseconds, so the value returned on create matches later reads
        if (addedDate == null) addedDate = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

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
