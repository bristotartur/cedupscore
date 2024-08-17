package com.bristotartur.cedupscore_api.domain.registrations;

import com.bristotartur.cedupscore_api.domain.events.TaskEvent;
import com.bristotartur.cedupscore_api.domain.people.Participant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "TB_TASK_REGISTRATION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TaskRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "task_event_id", nullable = false)
    private TaskEvent taskEvent;

    @Override
    public String toString() {
        return "TaskRegistration{" +
                "id=" + id +
                ", participant=" + participant +
                ", taskEvent=" + taskEvent +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskRegistration that = (TaskRegistration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
