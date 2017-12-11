package front;

import db.Database;
import db.Account;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import http.server.Request;
import http.server.Response;
import http.server.servlet.AbstractServlet;

public class MainServlet extends AbstractServlet {

	int count;
	Database db;

	@Override
	public void init() {
		FileInputStream fis;
		ObjectInputStream oin;
		try {
			fis = new FileInputStream("Data.db");
			oin = new ObjectInputStream(fis);
			db = (Database) oin.readObject();
			oin.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}

		// db = new Database();
		// db.add(new Account("Boris"));
		// rewriteDB();

		System.out.println(db);
		System.out.println("---Servelt init---");
	}

	@Override
	public void service(Request request, Response response) throws IOException {
		System.out.println("---Servlet work---");
		try (PrintWriter out = response.getWriter()) {

			out.print("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n");

			String cc = "";
			double bal = 0.0;
			String name = "";
			String pin = "";
			String unblock = "";
			String block = "";
			int log = 0;
			String npin = "";
			String tries = "";
			String del = "";
			String gname = "";

			if (request.getParameter("cc") != null)
				cc = request.getParameter("cc");
			if (request.getParameter("bal") != null)
				bal = Double.parseDouble(request.getParameter("bal"));
			if (request.getParameter("name") != null)
				name = request.getParameter("name");
			if (request.getParameter("pin") != null)
				pin = request.getParameter("pin");
			if (request.getParameter("unblock") != null)
				unblock = request.getParameter("unblock");
			if (request.getParameter("block") != null)
				block = request.getParameter("block");
			if (request.getParameter("log") != null)
				log = Integer.parseInt(request.getParameter("log"));
			if (request.getParameter("npin") != null)
				npin = request.getParameter("npin");
			if (request.getParameter("tries") != null)
				tries = request.getParameter("tries");
			if (request.getParameter("del") != null)
				del = request.getParameter("del");
			if (request.getParameter("gname") != null)
				gname = request.getParameter("gname");

			if (!(log == 0)) {
				out.print(db);
				return;
			}

			if (!gname.equals("")) {
				if (db.findById(gname) != null) {
					out.print(db.findById(gname).getName());
				} else
					out.print("false");
			}

			if (!cc.equals("")) {
				if (db.findById(cc) != null) {
					if (db.findById(cc).getTries() == 0)
						db.findById(cc).setBlocked(true);
					if (db.findById(cc).isBlocked()) {
						out.print("blocked");
						return;
					}

					if (bal != 0.0) {
						db.findById(cc).addBalance(bal);
					} else if (!pin.equals("")) {
						if (db.findById(cc).getPin().equals(pin)) {
							db.findById(cc).setTries(3);
							if (!npin.equals("")) {
								db.findById(cc).setPin(npin);
							} else
								out.print(db.findById(cc).getBalance());
						} else {
							db.findById(cc).addTries(-1);
							out.print("wrong");
							return;
						}
					} else
						out.print("true");
				} else {
					out.print("false");
				}
			}

			if (!name.equals("")) {
				Account acc = new Account(name);
				db.add(acc);
				String rep = "cc=" + acc.getId() + "&pin=" + acc.getPin();
				out.print(rep);
			}

			if (!block.equals("")) {
				if (db.findById(block) != null) {
					out.print(db.findById(block).getName() + " - blocked!");
					db.findById(block).setBlocked(true);
					db.findById(block).setTries(0);
				}
			}

			if (!unblock.equals("")) {
				if (db.findById(unblock) != null) {
					out.print(db.findById(unblock).getName() + " - unblocked!");
					db.findById(unblock).setBlocked(false);
					db.findById(unblock).setTries(3);
				}
			}

			if (!tries.equals("")) {
				if (db.findById(tries) != null) {
					out.print(db.findById(tries).getTries());
				}
			}

			if (!del.equals("")) {
				if (db.findById(del) != null) {
					out.print(db.findById(del).getName() + " - deleted!");
					db.deleteById(del);
				}
			}

			debug();
		}

	}

	@Override
	public void destroy() {
		System.out.println("---Servlet destroyed---");
	}

	private void debug() {
		System.out.println(db);
		rewriteDB();
	}

	private void rewriteDB() {
		File file = new File("Data.db");
		FileOutputStream fos;
		ObjectOutputStream oos;
		file.delete();
		try {
			fos = new FileOutputStream("Data.db");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(db);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

	}
}