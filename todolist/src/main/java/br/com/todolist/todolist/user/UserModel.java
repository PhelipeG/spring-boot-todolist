package br.com.todolist.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_users") // nome da tabela no banco de dados
public class UserModel {
    @Id // indica que o atributo é uma chave primária
    @GeneratedValue(generator = "UUID") // gera um UUID automaticamente para o id usando a estratégia UUID
    private UUID id;

    @Column(unique = true) // indica que o atributo é único
    private String userName;
    private String name;
    private String password;

    @CreationTimestamp // indica que o atributo é preenchido automaticamente com a data e hora de
                       // criação
    private LocalDateTime createdAt;

    // getters and setters -> servem para acessar e modificar os atributos da classe
    // public String getName() {
    // return name;
    // }

    // public void setName(String name) {
    // this.name = name;
    // }

    // public String getUserName() {
    // return userName;
    // }

    // public void setUserName(String userName) {
    // this.userName = userName;
    // }

    // public String getPassword() {
    // return password;
    // }

    // public void setPassword(String password) {
    // this.password = password;
    // }

}
