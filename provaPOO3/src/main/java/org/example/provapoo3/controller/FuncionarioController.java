package org.example.provapoo3.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.provapoo3.model.Endereco;
import org.example.provapoo3.model.Funcionario;
import org.example.provapoo3.repository.FuncionarioRepository;
import org.example.provapoo3.utils.Validador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FuncionarioController {

    // Campos do formulário
    @FXML private TextField tfMatricula, tfNome, tfCPF, tfCargo, tfSalario;
    @FXML private DatePicker dpNascimento, dpContratacao;
    @FXML private TextField tfLogradouro, tfNumero, tfComplemento, tfBairro, tfCidade, tfEstado, tfCEP;

    // Tabela
    @FXML private TableView<Funcionario> tvFuncionarios;
    @FXML private TableColumn<Funcionario, String> colMatricula, colNome, colCPF, colCargo, colCidade;
    @FXML private TableColumn<Funcionario, String> colSalario;

    // Relatório UI
    @FXML private TextField tfFiltroCargo;
    @FXML private TextField tfFiltroMinSalario, tfFiltroMaxSalario;
    @FXML private TextArea taRelatorio;

    private FuncionarioRepository repo;
    private ObservableList<Funcionario> lista;

    @FXML
    public void initialize() {
        repo = new FuncionarioRepository();
        List<Funcionario> todos = repo.listarTodos();
        lista = FXCollections.observableArrayList(todos);

        // Define colunas
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCPF.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colCidade.setCellValueFactory(cell -> {
            String cidade = (cell.getValue().getEndereco() != null)
                    ? cell.getValue().getEndereco().getCidade() : "";
            return javafx.beans.property.SimpleStringProperty.stringExpression(
                    javafx.beans.binding.Bindings.createStringBinding(() -> cidade)
            );
        });
        colSalario.setCellValueFactory(cell -> {
            BigDecimal sal = cell.getValue().getSalario();
            String s = sal != null ? sal.toString() : "";
            return javafx.beans.property.SimpleStringProperty.stringExpression(
                    javafx.beans.binding.Bindings.createStringBinding(() -> s)
            );
        });

        tvFuncionarios.setItems(lista);
    }

    @FXML
    private void adicionarFuncionario() {
        try {
            String matricula = tfMatricula.getText().trim();
            String nome = tfNome.getText().trim();
            String cpf = tfCPF.getText().trim();
            LocalDate nasc = dpNascimento.getValue();
            String cargo = tfCargo.getText().trim();
            BigDecimal salario = new BigDecimal(tfSalario.getText().trim());
            LocalDate contratacao = dpContratacao.getValue();

            // validações
            if (!Validador.validarMatricula(matricula))
                throw new Exception("Matrícula inválida. Use 6 dígitos, ex: 000123");

            if (!Validador.validarNome(nome))
                throw new Exception("Nome inválido (mínimo 3 caracteres).");

            if (!Validador.validarCPF(cpf))
                throw new Exception("CPF inválido. Digite 11 dígitos ou com pontuação.");

            // Verificação robusta da data de nascimento
            if (nasc == null) {
                throw new Exception("Selecione uma data de nascimento válida no calendário.");
            }

            // Cálculo confiável da idade
            int idade;
            try {
                idade = Period.between(nasc, LocalDate.now()).getYears();
            } catch (DateTimeParseException e) {
                throw new Exception("Erro ao interpretar a data de nascimento. Use o calendário.");
            }

            if (idade < 18) {
                throw new Exception("Idade inválida: " + idade + " anos. Mínimo permitido: 18 anos.");
            }

            if (!Validador.validarSalario(salario))
                throw new Exception("Salário inválido (deve ser maior que 0).");

            if (!Validador.validarCEP(tfCEP.getText().trim()))
                throw new Exception("CEP inválido (8 dígitos).");

            Endereco e = new Endereco(
                    tfLogradouro.getText(),
                    tfNumero.getText(),
                    tfComplemento.getText(),
                    tfBairro.getText(),
                    tfCidade.getText(),
                    tfEstado.getText(),
                    tfCEP.getText()
            );

            Funcionario f = new Funcionario(
                    matricula,
                    nome,
                    cpf.replaceAll("\\D", ""),
                    nasc,
                    cargo,
                    salario,
                    contratacao,
                    e
            );

            // garantir matrícula única
            if (repo.buscarPorMatricula(matricula) != null)
                throw new Exception("Matrícula já existe.");

            repo.adicionar(f);
            lista.add(f);
            limparCampos();
            showAlert("Sucesso", "Funcionário cadastrado com sucesso!");
        } catch (NumberFormatException nfe) {
            showAlert("Erro", "Salário deve ser número válido.");
        } catch (Exception ex) {
            showAlert("Erro", ex.getMessage());
        }
    }

    @FXML
    private void removerFuncionario() {
        Funcionario sel = tvFuncionarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert("Atenção", "Selecione um funcionário para remover.");
            return;
        }
        repo.remover(sel.getMatricula());
        lista.remove(sel);
    }

    @FXML
    private void consultarPorMatricula() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Consultar");
        dialog.setHeaderText("Consultar por matrícula");
        dialog.setContentText("Matrícula (6 dígitos):");
        dialog.showAndWait().ifPresent(m -> {
            Funcionario f = repo.buscarPorMatricula(m);
            if (f == null) {
                showAlert("Resultado", "Funcionário não encontrado.");
            } else {
                taRelatorio.setText("Encontrado:\n" + f.getNome() + " - " + f.getCargo() +
                        "\nCPF: " + f.getCpf() + "\nSalário: " + f.getSalario());
            }
        });
    }

    @FXML
    private void gerarRelatorioPorCargo() {
        String cargo = tfFiltroCargo.getText().trim();
        if (cargo.isEmpty()) {
            showAlert("Atenção", "Digite um cargo para filtrar.");
            return;
        }
        List<Funcionario> filtrados = lista.stream()
                .filter(f -> f.getCargo() != null && f.getCargo().equalsIgnoreCase(cargo))
                .collect(Collectors.toList());
        StringBuilder sb = new StringBuilder("Funcionários com cargo '" + cargo + "':\n");
        filtrados.forEach(f ->
                sb.append(f.getMatricula()).append(" - ").append(f.getNome())
                        .append(" - R$ ").append(f.getSalario()).append("\n"));
        taRelatorio.setText(sb.toString());
    }

    @FXML
    private void gerarRelatorioFaixaSalarial() {
        try {
            BigDecimal min = new BigDecimal(tfFiltroMinSalario.getText().trim());
            BigDecimal max = new BigDecimal(tfFiltroMaxSalario.getText().trim());
            List<Funcionario> filtrados = lista.stream()
                    .filter(f -> f.getSalario() != null &&
                            f.getSalario().compareTo(min) >= 0 &&
                            f.getSalario().compareTo(max) <= 0)
                    .collect(Collectors.toList());
            StringBuilder sb = new StringBuilder("Funcionários com salário entre " + min + " e " + max + ":\n");
            filtrados.forEach(f ->
                    sb.append(f.getMatricula()).append(" - ").append(f.getNome())
                            .append(" - R$ ").append(f.getSalario()).append("\n"));
            taRelatorio.setText(sb.toString());
        } catch (NumberFormatException ex) {
            showAlert("Erro", "Digite valores numéricos válidos para salário.");
        }
    }

    @FXML
    private void calcularMediaPorCargo() {
        Map<String, Double> media = lista.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getCargo,
                        Collectors.averagingDouble(f -> f.getSalario().doubleValue())
                ));
        StringBuilder sb = new StringBuilder("Média salarial por cargo:\n");
        media.forEach((c, m) ->
                sb.append(c).append(": R$ ").append(String.format("%.2f", m)).append("\n"));
        taRelatorio.setText(sb.toString());
    }

    @FXML
    private void agruparPorCidade() {
        Map<String, Long> contagem = lista.stream()
                .collect(Collectors.groupingBy(f -> f.getEndereco().getCidade(), Collectors.counting()));
        StringBuilder sb = new StringBuilder("Funcionários por cidade:\n");
        contagem.forEach((c, q) -> sb.append(c).append(": ").append(q).append("\n"));
        taRelatorio.setText(sb.toString());
    }

    private void limparCampos() {
        tfMatricula.clear(); tfNome.clear(); tfCPF.clear(); tfCargo.clear(); tfSalario.clear();
        dpNascimento.setValue(null); dpContratacao.setValue(null);
        tfLogradouro.clear(); tfNumero.clear(); tfComplemento.clear(); tfBairro.clear();
        tfCidade.clear(); tfEstado.clear(); tfCEP.clear();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}