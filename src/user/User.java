package user;

import controller.LoginController;
import data.AES;
import model.PasswordManagerModel;

import data.DatabaseHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * The User object holds all account info, including the account info for
 * logging into the password manager and all Internet account info.
 *
 * 
 */

public class User {

    private Account account;
    private HashMap<String, InternetAccount> internetAccounts;
    String encrypted;
    String decrypted;

    // take account as parameter because maybe we want to add more info to account in future
    public User(Account account) throws SQLException {
        this.account = account;
        this.internetAccounts = new HashMap<>();

        DatabaseHandler dbHandler = new DatabaseHandler();
        ResultSet result = dbHandler.getData(account.getId());
        String data;
        String user;
        String dom;

        while (result.next()) {
            data = result.getString("password_data");
            user = result.getString("username");
            dom = result.getString("domain");
            decrypted = AES.decrypt(data, LoginController.getKey());
            System.out.println(decrypted);
            internetAccounts.put(dom, new InternetAccount(dom, user, decrypted));
        }
    }

    /**
     * @param domain
     * @return Returns the InternetAccount corresponding to the input domain.
     */
    public InternetAccount getInternetAccount(String domain) {return internetAccounts.get(domain);}

    /**
     * @return Returns this User's Acccount.
     */
    public Account getAccount() {return account;}

    /**
     * @return Returns a HashMap of InternetAccounts.
     */
    public HashMap<String, InternetAccount> getInternetAccounts() {return internetAccounts;}

    /**
     * Adds an InternetAccount to the HashMap and writes the domain, username, and password in the corresponding .txt
     * file.
     *
     * @param internetAccount
     */
    public void addInternetAccount(InternetAccount internetAccount) {
        try {
            String domain = internetAccount.getDomain();
            String username = internetAccount.getUserName();
            String password = internetAccount.getPassword();
            File file = new File(PasswordManagerModel.DATA_DIRECTORY + account.getUserName() + ".txt");
            FileWriter writer = new FileWriter(file, true);
            writer.write("\n" + domain + " " + username + " " + password);
            writer.close();
            internetAccounts.put(domain, new InternetAccount(domain, username, password));

            encrypted = AES.encrypt(password, LoginController.getKey());
            System.out.println(encrypted);

            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.insertData(account.getId(), encrypted, domain, username);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
