package com.snippets.manager.snippetsmanager.models;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "TAG")
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TAG", nullable = false)
    private Long idTag;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "added_date")
    private Date addedDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<CodeSnippet> codeSnippets = new HashSet<>();

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
