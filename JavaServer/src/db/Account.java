package db;

import java.io.Serializable;
import java.util.Random;

public class Account implements Serializable {

	private String name;
	private String id;
	private String pin;
	private double balance;
	private boolean isBlocked = false;
	private int tries = 3;

	public Account(String name) {
		this.name = name;
		Random r = new Random();
		this.id = "" + (r.nextInt(8)+1);		
		for (int i = 0; i < 15; i++)
			this.id += r.nextInt(9);
		this.pin="";
		for(int i=0;i<4;i++)
			this.pin+=r.nextInt(9);
		this.balance = 0.0;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "[name=" + name + ", id=" + id + ", pin=" + pin + ", balance=" + balance + "]";
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void addBalance(double summ) {
		this.balance += summ;
	}

	public void setBlocked(boolean b) {
		this.isBlocked = b;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public int getTries() {
		return tries;
	}

	public void setTries(int n) {
		this.tries = n;
	}

	public void addTries(int n) {
		this.tries += n;
	}
}