package br.felipstein.tardis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import br.felipstein.tardis.utils.Cryptography;

public class Account {
	
	private UUID uniqueId;
	
	private String passwordEncrypted, lastAddress;
	
	public Account(UUID uniqueId, String passwordEncrypted, String lastAddress) {
		this.uniqueId = uniqueId;
		this.passwordEncrypted = passwordEncrypted;
		this.lastAddress = lastAddress;
	}
	
	public UUID getUniqueId() {
		return uniqueId;
	}
	
	public String getPasswordEncrypted() {
		return passwordEncrypted;
	}
	
	public String getLastAddress() {
		return lastAddress;
	}
	
	public void updateAddress(String address) {
		lastAddress = address;
		handleUpdate();
	}
	
	public void setPassword(String password) {
		passwordEncrypted = Cryptography.SHA_256.encrypt(password);
		handleUpdate();
	}
	
	public boolean matchPassword(String password) {
		return Cryptography.SHA_256.encrypt(password).equals(passwordEncrypted);
	}
	
	private void handleUpdate() {
		Main.getInstance().getSettings().updateAccount(this);
	}
	
	public static List<Account> getAccounts() {
		return Collections.unmodifiableList(Main.getInstance().getSettings().getAccountsRegistred());
	}
	
	public static Account getAccount(UUID uniqueId) {
		for(Account account : getAccounts()) {
			if(account.getUniqueId().equals(uniqueId)) {
				return account;
			}
		}
		return null;
	}
	
	public static Account getAccount(Player player) {
		return getAccount(player.getUniqueId());
	}
	
	public static List<Account> getAccounts(String address) {
		List<Account> accounts = new ArrayList<>();
		for(Account account : getAccounts()) {
			if(account.getLastAddress().equals(address)) {
				accounts.add(account);
			}
		}
		return accounts;
	}
	
	public static Account registerAccount(Player player, String password) {
		return registerAccount(player.getUniqueId(), password, player.getAddress().getAddress().getHostAddress());
	}
	
	public static Account registerAccount(UUID uniqueId, String password, String lastAddress) {
		Validate.isTrue(getAccount(uniqueId) == null, "Já existe um registro com o UUID " + uniqueId.toString() + ".");
		Account account = new Account(uniqueId, Cryptography.SHA_256.encrypt(password), lastAddress);
		Main.getInstance().getSettings().updateAccount(account);
		return account;
	}
	
	public static void unregisterAccount(UUID uniqueId) {
		Main.getInstance().getSettings().unregisterAccount(uniqueId);
	}
	
}