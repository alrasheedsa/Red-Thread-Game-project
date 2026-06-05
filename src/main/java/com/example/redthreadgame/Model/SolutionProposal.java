package com.example.redthreadgame.Model;

import com.example.redthreadgame.Enums.SolutionProposalStatusType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "solution_proposals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SolutionProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SolutionProposalStatusType status;

    @Column(columnDefinition = "int not null")
    private Integer rejectCount;

    @Column(columnDefinition = "int not null")
    private Integer acceptCount;

    @ManyToOne
    @JoinColumn(name = "game_session_id", referencedColumnName = "id")
    @JsonIgnore
    private GameSession gameSession;

    @ManyToOne
    @JoinColumn(name = "player_id", referencedColumnName = "id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "suspect_id", referencedColumnName = "id")
    private Suspect suspect;

    @OneToMany(mappedBy = "solutionProposal", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProposalVote> proposalVotes;
}
