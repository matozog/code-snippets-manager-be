package com.snippets.manager.snippetsmanager.models;

import com.fasterxml.jackson.annotation.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"codeSnippets", "idTag"})
@Table(name = "TAG")
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TAG", nullable = false)
    private Long idTag;

    @Column(name = "name", unique = true        )
    private String name;

    @Column(name = "added_date")
    private Date addedDate;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private Set<CodeSnippet> codeSnippets = new HashSet<>();
}
