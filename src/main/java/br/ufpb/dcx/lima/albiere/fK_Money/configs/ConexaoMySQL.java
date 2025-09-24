package br.ufpb.dcx.lima.albiere.fK_Money.configs;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoMySQL {

    private static final String ip = FK_Balance.getOptions().getConfig().getString("sql.ip");
    private static final String database = FK_Balance.getOptions().getConfig().getString("sql.database");
    private static final String URL = "jdbc:mysql://"+ip+":3306/"+database+"?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = FK_Balance.getOptions().getConfig().getString("sql.user");
    private static final String SENHA = FK_Balance.getOptions().getConfig().getString("sql.password");

    // Método para obter uma nova conexão com o banco de dados
    public static Connection getConexao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Erro na conexão com o banco de dados: " + e.getMessage(), e);
        }
    }
}