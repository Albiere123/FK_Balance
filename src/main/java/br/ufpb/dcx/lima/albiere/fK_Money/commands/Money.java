package br.ufpb.dcx.lima.albiere.fK_Money.commands;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerEconomy;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerCustom;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager.Manager;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.SimpleInventory;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@CommandAlias("Money|Dinheiro")
public class Money extends BaseCommand {

    private final String prefix =  FK_Balance.getOptions().getColouredString("essential.prefix");
    private final Manager ecoManager = FK_Balance.getEconomyManager();

    @Default
    @Subcommand("ver")
    public void onClear(CommandSender sender, @Optional OnlinePlayer arg1) {
        PlayerCustom p = new PlayerCustom((Player) sender);
        PlayerEconomy ecoPlayer = ecoManager.getPlayerBalance(arg1 != null ? arg1.getPlayer().getUniqueId() : ((Player) sender).getUniqueId());
        if(arg1 != null && arg1.getPlayer().getUniqueId() != ((Player) sender).getUniqueId()) p.sendColouredMessage(prefix + "&e"+arg1.getPlayer().getName()+ " &fpossue &eR$ " + ecoPlayer.getMoney() + "&f.");
        else p.sendColouredMessage(prefix + "Você possue &eR$ " + ecoPlayer.getMoney() + "&f.");

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

            money = new BigDecimal(arg2.replace(arg1.getPlayer().getName()+" ", ""));
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
        destiny.sendColouredMessage(prefix + "Você recebeu &eR$ " + money + "&f de " + p.getPlayer().getName());

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
            money = new BigDecimal(arg2);//.replace(arg1.getPlayer().getName()+" ", ""));
        } catch (NumberFormatException e) {
            p.sendColouredMessage(prefix + "Insira Apenas Números!");
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            p.sendColouredMessage(prefix + "Insira apenas números positivos.");

        }

        FK_Balance.getEconomyManager().adicionarMoney(arg1.getPlayer(), money);
        if (Objects.requireNonNull(arg1).getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            p.sendColouredMessage(prefix + "Adicionado &eR$ " + money + " &fa &esua &fconta.");
        } else {
            p.sendColouredMessage(prefix + "Adicionado &eR$ " + money + " &fa conta de &e" + arg1.getPlayer().getName() + "&f.");
            (new PlayerCustom(arg1.getPlayer())).sendColouredMessage(prefix + "O staff &e" + sender.getName() + " &fadicionou &eR$ " + money + " &fa sua conta.");
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

            money = new BigDecimal(arg2);//.replace(arg1.getPlayer().getName()+" ", ""));
        } catch (NumberFormatException e) {
            p.sendColouredMessage(prefix + "Insira Apenas Números!");
            return;
        }

        if (money.compareTo(BigDecimal.valueOf(0)) < 0) {

            p.sendColouredMessage(prefix + "Insira apenas números positivos.");

        }

        FK_Balance.getEconomyManager().setMoney(arg1.getPlayer(), money);
        if (arg1.getPlayer().getUniqueId() == ((Player) sender).getUniqueId()) {
            p.sendColouredMessage(prefix + "Novo saldo da sua conta é &eR$ " + money + "&f.");
        } else {
            p.sendColouredMessage(prefix + "Novo saldo da conta do jogador &e" + arg1.getPlayer().getName() + " &fé &eR$ " + money + "&f.");
            (new PlayerCustom(arg1.getPlayer())).sendColouredMessage(prefix + "O staff &e" + sender.getName() + " &fdeterminou o valor da sua conta para &eR$ " + money + "&f.");
        }
    }

    @Subcommand("top")
    @CommandAlias("rank")
    public void onTop(CommandSender sender) {
        FK_Balance.loadTopMoney((Player) sender);
        SimpleInventory inventory = FK_Balance.getManager().getInventory("topMoney"+((Player) sender).getUniqueId());

        int rankUser = FK_Balance.getEconomyManager().getYTopMoney((Player) sender);
        String name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(rankUser)).replaceAll("\\{Player}", sender.getName());
        List<String> lore = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore"));
        lore.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance(((Player) sender).getUniqueId()).getMoney())));
        inventory.addPlayerHead(4, 1, name, lore, ((Player) sender).getUniqueId());


        inventory.open((Player) sender);


    }

    }
