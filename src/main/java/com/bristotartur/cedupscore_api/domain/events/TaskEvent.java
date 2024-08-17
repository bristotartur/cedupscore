package com.bristotartur.cedupscore_api.domain.events;

import com.bristotartur.cedupscore_api.enums.Status;
import com.bristotartur.cedupscore_api.enums.TaskType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "TB_TASK_EVENT")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class TaskEvent extends Event {

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Lob
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "edition_id", nullable = false)
    private Edition edition;

    @OneToMany(mappedBy = "taskEvent", cascade = CascadeType.ALL)
    private Set<TaskScore> taskScores = new HashSet<>();

    @OneToMany(mappedBy = "taskEvent", cascade = CascadeType.ALL)
    private Set<TaskRegistration> taskRegistrations = new HashSet<>();

    public TaskEvent(Long id, Status status, LocalDateTime startedAt, LocalDateTime endedAt, TaskType type, String description, Edition edition) {
        super(id, status, startedAt, endedAt);
        this.type = type;
        this.description = description;
        this.edition = edition;
    }

    @Override
    public String toString() {
        return "TaskEvent{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", startedAt=" + getStartedAt() +
                ", endedAt=" + getEndedAt() +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", edition=" + edition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskEvent taskEvent = (TaskEvent) o;
        return Objects.equals(getId(), taskEvent.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
