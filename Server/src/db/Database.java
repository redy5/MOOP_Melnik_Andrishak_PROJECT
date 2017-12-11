package db;

import java.io.Serializable;

public class Database implements Serializable {

	Account[] accounts = new Account[10];
	int count = 0;

	public void resize() {
		Account[] temp = accounts.clone();
		accounts = new Account[accounts.length * 2];
		for (int i = 0; i < count; i++)
			accounts[i] = temp[i];
	}

	public void add(Account acc) {
		if (count+2 == accounts.length)
			resize();
		accounts[count++] = acc;
	}

	public void deleteByName(String name) {
		int c=-1;
		for (int i = 0; i < count; i++) {
			if (accounts[i].getName().equals(name))
				c=i;
		}
		for(int i=c;i<count-1;i++)
			accounts[i]=accounts[i+1];
		accounts[--count]=null;
	}

	public void deleteById(String id) {
		int c=-1;
		for (int i = 0; i < count; i++) {
			if (accounts[i].getId().equals(id))
				c=i;
		}
		for(int i=c;i<count-1;i++)
			accounts[i]=accounts[i+1];
		accounts[--count]=null;
	}

//	public Account findByName(String name) {
//		for (int i = 0; i < count; i++) {
//			if (accounts[i].getName().equals(name))
//				return accounts[i];
//		}
//		return null;
//	}
	
	public Account findById(String id) {
		for (int i = 0; i < count; i++) {
			if (accounts[i].getId().equals(id))
				return accounts[i];
		}
		return null;
	}

	@Override
	public String toString() {
		String res="Database\n";
		for(int i=0;i<count;i++)
			res+=("[Name: " + accounts[i].getName() + ", Card number: " + accounts[i].getId() + ", Pin: " + accounts[i].getPin()+", Balance: "+accounts[i].getBalance()+", Is blocked: "+accounts[i].isBlocked()+", Tries:"+accounts[i].getTries()+"]\n");
		return res;
	}
}