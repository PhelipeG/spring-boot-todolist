package br.com.todolist.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String description;
    private String priority;

    @Column(length = 50) // limita o tamanho do campo no banco de dados
    private String title;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private UUID userId;

    // throws Exception é uma forma de tratar exceções de forma genérica
    public void setTitle(String title) throws Exception {
        if (title.length() > 50) {
            throw new Exception("Título deve ter no máximo 50 caracteres");
        }
        this.title = title;
    }

}
