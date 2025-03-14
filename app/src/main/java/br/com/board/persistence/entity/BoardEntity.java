package br.com.board.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
public class BoardEntity {

    private Long id;
    private String name;
    @ToString.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();

}
