package br.com.board.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

import static br.com.board.persistence.config.ConnectionConfig.getConnection;
import br.com.board.persistence.entity.BoardColumnEntity;
import br.com.board.persistence.entity.BoardColumnKindEnum;
import static br.com.board.persistence.entity.BoardColumnKindEnum.CANCEL;
import static br.com.board.persistence.entity.BoardColumnKindEnum.FINAL;
import static br.com.board.persistence.entity.BoardColumnKindEnum.INITIAL;
import static br.com.board.persistence.entity.BoardColumnKindEnum.PENDING;
import br.com.board.persistence.entity.BoardEntity;
import br.com.board.service.BoardQueryService;
import br.com.board.service.BoardService;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n").useLocale(Locale.US);

    public void execute() throws SQLException {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");
        var option = -1;
        while (true) {
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");

            String token = scanner.next();
            System.out.println("Token lido: '" + token + "'");
            try {
                option = Integer.parseInt(token.trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                continue;
            }

            switch (option) {
                case 1 ->
                    createBoard();
                case 2 ->
                    selectBoard();
                case 3 ->
                    deleteBoard();
                case 4 ->
                    System.exit(0);
                default ->
                    System.out.println("Opção inválida, informe uma opção do menu");
            }
        }
    }

    private void createBoard() throws SQLException {
        var entity = new BoardEntity();
        System.out.println("Informe o nome do seu board");
        entity.setName(scanner.next());

        System.out.println("Seu board terá colunas além das 3 padrões? Se sim informe quantas, senão digite '0'");
        int additionalColumns;
        try {
            additionalColumns = Integer.parseInt(scanner.next().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o número de colunas adicionais. Utilizando 0 por padrão.");
            additionalColumns = 0;
        }

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++) {
            System.out.println("Informe o nome da coluna de tarefa pendente do board");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, PENDING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Informe o nome da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna de cancelamento do board");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            service.insert(entity);
        }
    }

    private void selectBoard() throws SQLException {
        System.out.println("Informe o id do board que deseja selecionar");
        long id;
        try {
            id = Long.parseLong(scanner.next().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o id. Retornando ao menu.");
            return;
        }
        try (var connection = getConnection()) {
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () -> System.out.printf("Não foi encontrado um board com id %s\n", id)
            );
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Informe o id do board que será excluido");
        long id;
        try {
            id = Long.parseLong(scanner.next().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida para o id. Retornando ao menu.");
            return;
        }
        try (var connection = getConnection()) {
            var service = new BoardService(connection);
            if (service.delete(id)) {
                System.out.printf("O board %s foi excluido\n", id);
            } else {
                System.out.printf("Não foi encontrado um board com id %s\n", id);
            }
        }
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);
        return boardColumn;
    }
}
