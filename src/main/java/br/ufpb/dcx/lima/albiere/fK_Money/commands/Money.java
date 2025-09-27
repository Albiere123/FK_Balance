package br.ufpb.dcx.lima.albiere.fK_Money.commands;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerEconomy;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerCustom;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager.Manager;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.InventoryModuleInterface;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CommandAlias("Money|Dinheiro")
public class Money extends BaseCommand {

    private final String prefix =  FK_Balance.getOptions().getColouredString("essential.prefix");
    private final Manager ecoManager = FK_Balance.getEconomyManager();

    @Default
    @Subcommand("ver")
    public void onClear(CommandSender sender, @Optional OnlinePlayer arg1) {
        PlayerCustom p = new PlayerCustom((Player) sender);
        PlayerEconomy ecoPlayer = ecoManager.getPlayerBalance(arg1 != null ? arg1.getPlayer().getUniqueId() : ((Player) sender).getUniqueId());
        if(arg1 != null && arg1.getPlayer().getUniqueId() != ((Player) sender).getUniqueId()) p.sendColouredMessage(prefix + "&e"+arg1.getPlayer().getName()+ " &fpossue &eR$ " + bigDecimaltoString(String.valueOf(ecoPlayer.getMoney())) + "&f.");
        else p.sendColouredMessage(prefix + "Você possue &eR$ " + bigDecimaltoString(String.valueOf(ecoPlayer.getMoney())) + "&f.");

    }

    @Subcommand("pay")
    public void onPay(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        PlayerCustom p = new PlayerCustom((Player) sender);
        if (arg2 == null) {

            p.sendColouredMessage(prefix + "Comando inválido. Use &e/money pay <jogador> <valor>");
            return;

        } else if(!arg1.getPlayer().isOnline()) {
            p.sendColouredMessage(prefix + "O jogador não foi encontrado ou está offline.");
            return;
        }

        BigDecimal money;
        try {

            money = stringtoBigDecimal(arg2);
        } catch (NumberFormatException e) {
            p.sendColouredMessage(prefix + "Insira Apenas Números!");
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            p.sendColouredMessage(prefix + "Insira apenas números positivos.");
            return;

        }


        PlayerCustom destiny = new PlayerCustom(arg1.getPlayer());
        PlayerEconomy ecoPlayer = ecoManager.getPlayerBalance(((Player) sender).getUniqueId());
        if (p.getPlayer() == destiny.getPlayer()) {

            p.sendColouredMessage(prefix + "Você não pode transferir dinheiro para &cvocê mesmo&f.");
            return;

        }

        if (ecoPlayer.getMoney().compareTo(money) < 0) {
            p.sendColouredMessage(prefix + "Você não possue &cdinheiro o suficiente.");
            return;
        }
        ecoManager.transferirMoney(p.getPlayer().getUniqueId(), destiny.getPlayer().getUniqueId(), money);

        p.sendColouredMessage(prefix + "Transação enviada.");
        destiny.sendColouredMessage(prefix + "Você recebeu &eR$ " + bigDecimaltoString(String.valueOf(money)) + "&f de " + p.getPlayer().getName());

    }

    @Subcommand("forcereload")
    public void onReload(CommandSender sender) {
        if(!sender.hasPermission("FK_Balance.forcereload")) return;
        FK_Balance.loadConfig();
        (new PlayerCustom((Player) sender)).sendColouredMessage(prefix + "Configurações atualizadas!");
    }

    @Subcommand("give")
    public void onGive(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        PlayerCustom p = new PlayerCustom((Player) sender);

        if (!sender.hasPermission("FK_Balance.give")) {
            p.sendColouredMessage(prefix + "Você não possue &cpermissão&f.");

        }
        if (arg2 == null) {

            p.sendColouredMessage(prefix + "Comando inválido. Use &e/money give <jogador> <valor>");
            return;

        } else if(!arg1.getPlayer().isOnline()) {
            p.sendColouredMessage(prefix + "O jogador não foi encontrado ou está offline.");
            return;
        }

        BigDecimal money;
        try {
            money = stringtoBigDecimal(arg2);//.replace(arg1.getPlayer().getName()+" ", ""));
        } catch (NumberFormatException e) {
            p.sendColouredMessage(prefix + "Insira Apenas Números!");
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            p.sendColouredMessage(prefix + "Insira apenas números positivos.");

        }

        FK_Balance.getEconomyManager().adicionarMoney(arg1.getPlayer(), money);
        if (Objects.requireNonNull(arg1).getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            p.sendColouredMessage(prefix + "Adicionado &eR$ " + bigDecimaltoString(String.valueOf(money)) + " &fa &esua &fconta.");
        } else {
            p.sendColouredMessage(prefix + "Adicionado &eR$ " + bigDecimaltoString(String.valueOf(money)) + " &fa conta de &e" + arg1.getPlayer().getName() + "&f.");
            (new PlayerCustom(arg1.getPlayer())).sendColouredMessage(prefix + "O staff &e" + sender.getName() + " &fadicionou &eR$ " + bigDecimaltoString(String.valueOf(money)) + " &fa sua conta.");
        }
    }

    @Subcommand("set")
    public void onSet(CommandSender sender, @Optional OnlinePlayer arg1, @Optional String arg2) {
        PlayerCustom p = new PlayerCustom((Player) sender);
        if (!sender.hasPermission("FK_Balance.set")) {
            p.sendColouredMessage(prefix + "Você não possue &cpermissão&f.");

        }

        if (arg2 == null) {

            p.sendColouredMessage(prefix + "Comando inválido. Use &e/money set <jogador> <valor>");
            return;

        }else if(!arg1.getPlayer().isOnline()) {
            p.sendColouredMessage(prefix + "O jogador não foi encontrado ou está offline.");
            return;
        }

        BigDecimal money;
        try {

            money = stringtoBigDecimal(arg2);
        } catch (NumberFormatException e) {
            p.sendColouredMessage(prefix + "Insira Apenas Números!");
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            p.sendColouredMessage(prefix + "Insira apenas números positivos.");

        }

        FK_Balance.getEconomyManager().setMoney(arg1.getPlayer(), money);
        if (arg1.getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            p.sendColouredMessage(prefix + "Novo saldo da sua conta é &eR$ " + bigDecimaltoString(String.valueOf(money)) + "&f.");
        } else {
            p.sendColouredMessage(prefix + "Novo saldo da conta do jogador &e" + arg1.getPlayer().getName() + " &fé &eR$ " + bigDecimaltoString(String.valueOf(money)) + "&f.");
            (new PlayerCustom(arg1.getPlayer())).sendColouredMessage(prefix + "O staff &e" + sender.getName() + " &fdeterminou o valor da sua conta para &eR$ " + bigDecimaltoString(String.valueOf(money)) + "&f.");
        }
    }

    @Subcommand("top")
    @CommandAlias("rank")
    public void onTop(CommandSender sender) {
        InventoryModuleInterface inventory = FK_Balance.loadTopMoney(0);

        int rankUser = FK_Balance.getEconomyManager().getYTopMoney((Player) sender);
        String name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(rankUser)).replaceAll("\\{Player}", sender.getName());
        List<String> lore = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore"));
        lore.replaceAll(s -> s.replaceAll("\\{Money}", bigDecimaltoString(String.valueOf(new BigDecimal(String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance(((Player) sender).getUniqueId()).getMoney()))))));
        inventory.addPlayerHead(4, 1, name, lore, ((Player) sender).getUniqueId());


        inventory.open((Player) sender);


    }


    public BigDecimal stringtoBigDecimal(String s) {
        Pattern CURRENCY_PATTERN = Pattern.compile(
                "^(\\d+(\\.\\d+)?)(K|M|B|T|Q|QI|S)?$",
                Pattern.CASE_INSENSITIVE
        );

        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("A string não pode ser nula ou vazia.");
        }

        Matcher matcher = CURRENCY_PATTERN.matcher(s.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Formato inválido: \"" + s + "\".");
        }

        String numberPart = matcher.group(1);
        String suffixPart = matcher.group(3);

        BigDecimal value = new BigDecimal(numberPart);

        if (suffixPart != null) {
            switch (suffixPart.toUpperCase()) {
                case "K":
                    value = value.multiply(new BigDecimal("1000"));
                    break;
                case "M":
                    value = value.multiply(new BigDecimal("1000000"));
                    break;
                case "B":
                    value = value.multiply(new BigDecimal("1000000000"));
                    break;
                case "T":
                    value = value.multiply(new BigDecimal("1000000000000"));
                    break;
                case "Q":
                    value = value.multiply(new BigDecimal("1000000000000000"));
                    break;
                case "QI":
                    value = value.multiply(new BigDecimal("1000000000000000000"));
                    break;
                case "S":
                    value = value.multiply(new BigDecimal("1000000000000000000000"));
                    break;
            }
        }

        return value;
    }

    private static final BigDecimal KILO = new BigDecimal("1000");
    private static final BigDecimal MILHAO = new BigDecimal("1000000");
    private static final BigDecimal BILHAO = new BigDecimal("1000000000");
    private static final BigDecimal TRILHAO = new BigDecimal("1000000000000");
    private static final BigDecimal QUATRILHAO = new BigDecimal("1000000000000000");
    private static final BigDecimal QUINTILHAO = new BigDecimal("1000000000000000000");
    private static final BigDecimal SEXTILHAO = new BigDecimal("1000000000000000000000");

    // Método principal corrigido
    public static String bigDecimaltoString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "0";
        }

        BigDecimal numberValue;
        try {
            numberValue = new BigDecimal(value);
        } catch (NumberFormatException e) {
            return "0";
        }

        if (numberValue.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        String sign = numberValue.compareTo(BigDecimal.ZERO) < 0 ? "-" : "";
        BigDecimal absValue = numberValue.abs();

        if (absValue.compareTo(SEXTILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, SEXTILHAO, "S");
        }
        if (absValue.compareTo(QUINTILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, QUINTILHAO, "QI");
        }
        if (absValue.compareTo(QUATRILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, QUATRILHAO, "Q");
        }
        if (absValue.compareTo(TRILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, TRILHAO, "T");
        }
        if (absValue.compareTo(BILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, BILHAO, "B");
        }
        if (absValue.compareTo(MILHAO) >= 0) {
            return sign + formatWithSuffix(absValue, MILHAO, "M");
        }
        if (absValue.compareTo(KILO) >= 0) {
            return sign + formatWithSuffix(absValue, KILO, "K");
        }

        return numberValue.stripTrailingZeros().toPlainString();
    }

    private static String formatWithSuffix(BigDecimal value, BigDecimal divisor, String suffix) {
        BigDecimal result = value.divide(divisor, 2, RoundingMode.HALF_UP);
        return result + suffix;
    }
    }
