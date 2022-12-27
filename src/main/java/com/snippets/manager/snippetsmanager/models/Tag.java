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
@Getter
@Setter
@EqualsAndHashCode(exclude = {"codeSnippets","idTag"} )
@Table(name = "TAG")
public class Tag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID_TAG", nullable = false)
    private Long idTag;

    @Column(name = "name")
    private String name;

    @Column(name = "added_date")
    private Date addedDate;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name="SNIPPET_TAG",
            joinColumns={@JoinColumn(name="ID_TAG")},
            inverseJoinColumns={@JoinColumn(name="ID_SNIPPET")})
    private Set<CodeSnippet> codeSnippets = new HashSet<>();
}
