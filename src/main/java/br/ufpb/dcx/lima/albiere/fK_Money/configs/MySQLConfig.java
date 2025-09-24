package br.ufpb.dcx.lima.albiere.fK_Money.configs;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerEconomy;
import org.bukkit.Bukkit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MySQLConfig {

    private static final String table = FK_Balance.getOptions().getConfig().getString("sql.table");
    /**
     * Salva ou atualiza a economia de UM jogador no banco de dados.
     * Usa "INSERT ... ON DUPLICATE KEY UPDATE" para ser eficiente.
     * Se o UUID não existe, ele INSERE. Se já existe, ele ATUALIZA.
     */
    public void saveAccount(UUID uuid, PlayerEconomy economy) {
        String sql = "INSERT INTO"+table+"(player_uuid, money, cash) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE money = VALUES(money), cash = VALUES(cash)";

        try (Connection conn = ConexaoMySQL.getConexao(); // Assume que você tem a classe ConexaoMySQL
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid.toString());
            pstmt.setBigDecimal(2, economy.getMoney());
            pstmt.setBigDecimal(3, economy.getCash());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Falha ao salvar a conta do UUID " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Salva TODAS as contas em memória no banco de dados usando batch update.
     * Isso é MUITO mais rápido do que salvar uma por uma em um loop.
     */
    public void saveAllAccounts(Map<UUID, PlayerEconomy> balances) {
        String sql = "INSERT INTO " + table + " (player_uuid, player_nome, money, cash) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE player_nome = VALUES(player_nome), money = VALUES(money), cash = VALUES(cash)";

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, PlayerEconomy> entry : balances.entrySet()) {
                pstmt.setString(1, entry.getKey().toString());
                pstmt.setString(2, Bukkit.getOfflinePlayer(entry.getKey()).getName());
                pstmt.setBigDecimal(3, entry.getValue().getMoney());
                pstmt.setBigDecimal(4, entry.getValue().getCash());
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            System.err.println("Falha ao salvar todas as contas em lote: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Carrega a conta de UM jogador do banco de dados.
     * Retorna null se o jogador não for encontrado.
     */
    public PlayerEconomy loadAccount(UUID uuid) {
        String sql = "SELECT money, cash FROM "+table+" WHERE player_uuid = ?";
        PlayerEconomy economy = null;

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid.toString());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                BigDecimal money = rs.getBigDecimal("money");
                BigDecimal cash = rs.getBigDecimal("cash");
                economy = new PlayerEconomy(money, cash, Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName());
            }

        } catch (SQLException e) {
            System.err.println("Falha ao carregar a conta do UUID " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
        return economy;
    }


    /**
     * Carrega TODAS as contas do banco de dados para a memória.
     * Geralmente executado quando o plugin é iniciado.
     */
    public Map<UUID, PlayerEconomy> loadAllAccounts() {
        Map<UUID, PlayerEconomy> balances = new HashMap<>();
        String sql = "SELECT player_uuid, money, cash FROM "+table;

        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("player_uuid"));
                BigDecimal money = rs.getBigDecimal("money");
                BigDecimal cash = rs.getBigDecimal("cash");
                balances.put(uuid, new PlayerEconomy(money, cash, Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName()));
            }

        } catch (SQLException e) {
            System.err.println("Falha ao carregar todas as contas: " + e.getMessage());
            e.printStackTrace();
        }
        return balances;
    }
}
