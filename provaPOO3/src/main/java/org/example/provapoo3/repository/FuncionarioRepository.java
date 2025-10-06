package org.example.provapoo3.repository;

import org.example.provapoo3.model.Funcionario;
import org.example.provapoo3.utils.CSVUtil;

import java.util.List;

public class FuncionarioRepository {
    private List<Funcionario> lista;

    public FuncionarioRepository() {
        this.lista = CSVUtil.carregar();
    }

    public List<Funcionario> listarTodos() {
        return lista;
    }

    public void adicionar(Funcionario f) {
        lista.add(f);
        CSVUtil.salvar(lista);
    }

    public void remover(String matricula) {
        lista.removeIf(f -> f.getMatricula().equals(matricula));
        CSVUtil.salvar(lista);
    }

    public Funcionario buscarPorMatricula(String matricula) {
        return lista.stream().filter(f -> f.getMatricula().equals(matricula)).findFirst().orElse(null);
    }
}