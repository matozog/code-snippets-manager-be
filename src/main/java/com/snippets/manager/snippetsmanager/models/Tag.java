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
@Table(name = "TAG", uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TAG", nullable = false)
    private Long idTag;

    // unique per owner
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "added_date")
    private Instant addedDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<CodeSnippet> codeSnippets = new HashSet<>();

    @PrePersist
    void onCreate() {
        // database keeps microseconds, so the value returned on create matches later reads
        if (addedDate == null) addedDate = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Tag other = (Tag) o;
        return getIdTag() != null && Objects.equals(getIdTag(), other.getIdTag());
    }

    @Override
    public final int hashCode() {
        return Hibernate.getClass(this).hashCode();
    }
}
