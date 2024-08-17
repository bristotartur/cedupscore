package com.bristotartur.cedupscore_api.domain.scores;

import com.bristotartur.cedupscore_api.domain.events.TaskEvent;
import com.bristotartur.cedupscore_api.domain.people.Team;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "TB_TASK_SCORE")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class TaskScore extends Score {

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "task_event_id", nullable = false)
    private TaskEvent taskEvent;

    public TaskScore(Long id, Integer score, Team team, TaskEvent taskEvent) {
        super(id, score);
        this.team = team;
        this.taskEvent = taskEvent;
    }

    @Override
    public String toString() {
        return "TaskScore{" +
                "id=" + getId() +
                ", score=" + getScore() +
                ", team=" + team +
                ", taskEvent=" + taskEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskScore taskScore = (TaskScore) o;
        return Objects.equals(getId(), taskScore.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
