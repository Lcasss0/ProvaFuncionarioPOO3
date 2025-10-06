package org.example.provapoo3.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

public class Validador {

    // Matrícula: exatamente 6 dígitos numéricos
    public static boolean validarMatricula(String m) {
        return m != null && m.matches("\\d{6}");
    }

    // Nome: não vazio, pelo menos 3 caracteres
    public static boolean validarNome(String nome) {
        return nome != null && nome.trim().length() >= 3;
    }

    // CPF: validação dos dígitos verificadores (aceita com ou sem pontuação)
    public static boolean validarCPF(String cpf) {
        if (cpf == null) return false;
        String s = cpf.replaceAll("\\D", "");
        if (s.length() != 11) return false;
        if (s.matches("(\\d)\\1{10}")) return false; // todos iguais

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) sum += Character.getNumericValue(s.charAt(i)) * (10 - i);
            int dv1 = 11 - (sum % 11);
            if (dv1 >= 10) dv1 = 0;
            if (dv1 != Character.getNumericValue(s.charAt(9))) return false;

            sum = 0;
            for (int i = 0; i < 10; i++) sum += Character.getNumericValue(s.charAt(i)) * (11 - i);
            int dv2 = 11 - (sum % 11);
            if (dv2 >= 10) dv2 = 0;
            return dv2 == Character.getNumericValue(s.charAt(10));
        } catch (Exception ex) {
            return false;
        }
    }

    // Idade mínima de 18 anos
    public static boolean validarIdade(LocalDate nascimento) {
        if (nascimento == null) return false;
        return Period.between(nascimento, LocalDate.now()).getYears() >= 18;
    }

    // Salário > 0
    public static boolean validarSalario(BigDecimal salario) {
        return salario != null && salario.compareTo(BigDecimal.ZERO) > 0;
    }

    // CEP: 8 dígitos (com ou sem hífen)
    public static boolean validarCEP(String cep) {
        if (cep == null) return false;
        String s = cep.replaceAll("\\D", "");
        return s.matches("\\d{8}");
    }
}