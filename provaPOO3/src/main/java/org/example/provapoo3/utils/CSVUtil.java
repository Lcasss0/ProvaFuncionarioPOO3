package org.example.provapoo3.utils;

import org.example.provapoo3.model.Endereco;
import org.example.provapoo3.model.Funcionario;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    private static final String FILE_PATH = "funcionarios.csv";

    public static List<Funcionario> carregar() {
        List<Funcionario> lista = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                String[] parts = linha.split(";");
                // ordem: matricula;nome;cpf;dataNascimento;cargo;salario;dataContratacao;logradouro;numero;complemento;bairro;cidade;estado;cep
                if (parts.length < 14) continue;
                Funcionario func = new Funcionario();
                func.setMatricula(parts[0]);
                func.setNome(parts[1]);
                func.setCpf(parts[2]);
                func.setDataNascimento(LocalDate.parse(parts[3]));
                func.setCargo(parts[4]);
                func.setSalario(new BigDecimal(parts[5]));
                func.setDataContratacao(LocalDate.parse(parts[6]));
                Endereco e = new Endereco(parts[7], parts[8], parts[9], parts[10], parts[11], parts[12], parts[13]);
                func.setEndereco(e);
                lista.add(func);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public static void salvar(List<Funcionario> funcionarios) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Funcionario f : funcionarios) {
                String[] parts = new String[] {
                        f.getMatricula(),
                        f.getNome(),
                        f.getCpf(),
                        f.getDataNascimento().toString(),
                        f.getCargo(),
                        f.getSalario().toString(),
                        f.getDataContratacao().toString(),
                        f.getEndereco().getLogradouro(),
                        f.getEndereco().getNumero(),
                        f.getEndereco().getComplemento(),
                        f.getEndereco().getBairro(),
                        f.getEndereco().getCidade(),
                        f.getEndereco().getEstado(),
                        f.getEndereco().getCep()
                };
                bw.write(String.join(";", parts));
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}