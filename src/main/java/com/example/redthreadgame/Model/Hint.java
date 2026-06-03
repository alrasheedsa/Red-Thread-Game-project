package com.example.redthreadgame.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hints")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Hint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(500) not null")
    private String content;

    @ManyToOne
    @JoinColumn(name = "game_session_id", referencedColumnName = "id")
    @JsonIgnore
    private GameSession gameSession;
}
