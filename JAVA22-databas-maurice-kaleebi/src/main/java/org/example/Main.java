package org.example;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static ArrayList<Integer> loggedInAccountsIDsArr = new ArrayList<>();

    private static ArrayList<Integer> receiverAccountsIDsArr = new ArrayList<>();

    private static ArrayList<Integer> allUsersIdExceptForLoggedInArr = new ArrayList<>();

    private static int loggedInID;

    private static int loggedInPersonalNumber;


    static int getLoggedInID() {
        return loggedInID;
    }

    static void setLoggedInID(int newID) {
        loggedInID = newID;
    }

    static int getLoggedInPersonalNumber() {
        return loggedInPersonalNumber;
    }

    static void setLoggedInPersonalNumber(int newPersonalNumber) {
        loggedInPersonalNumber = newPersonalNumber;
    }

    static ArrayList<Integer> getLoggedInAccountsIDsArr() {
        return loggedInAccountsIDsArr;
    }

    static void setLoggedInAccountsIDsArr(int id) {
        loggedInAccountsIDsArr.add(id);
    }


    static void setToRemoveIDFromLoggedInAccountsIDsArr(int id) {
        loggedInAccountsIDsArr.remove(Integer.valueOf(id));
    }

    static void setToEmptyLoggedInAccountIDsArr (){
        loggedInAccountsIDsArr.clear();
    }


        static ArrayList<Integer> getReceiverAccountsIDsArr() {
        return receiverAccountsIDsArr;
    }

    static void setReceiverAccountsIDsArr(int id) {
        receiverAccountsIDsArr.add(id);
    }


    static ArrayList<Integer> getAllUsersIdExceptForLoggedInArr() {
        return allUsersIdExceptForLoggedInArr;
    }

    static void setAllUsersIdExceptForLoggedInArr(int id) {
        allUsersIdExceptForLoggedInArr.add(id);
    }

    static Scanner scanner;
    static MysqlDataSource dataSource;
    static String url = "localhost";
    static int port = 3308;
    static String database = "finalproject";
    static String username = "root";
    static String password = "";


    public static void main(String[] args) throws SQLException {
        InitializeDatabase();
        Connection connection = GetConnection();
        if (connection == null || connection.isClosed()) {
            System.out.println("Failed to get a connection to the database.");
        } else {
            System.out.println("Successfully connected to the database.");
        }

        CreateUserTable();
        CreateAccountTable();
        CreateTransactionsTable();

        scanner = new Scanner(System.in);
        InitializeDatabase();

        boolean run = true;
        boolean run2;

        while (run) {
            System.out.println("Välj vad du vill göra.");
            System.out.println("1. Skapa användare");
            System.out.println("2. Logga in");
            System.out.println("3. Avsluta");


            switch (scanner.nextLine().trim()) {
                case "1" -> CreateUser();
                case "2" -> {
                    System.out.println("1. Ange personnummer");
                    int personalNumber = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println("2. Ange lösenord");
                    String password = scanner.nextLine().trim();
                    {
                        if (CheckCredentials(personalNumber, password)) {
                            run2 = true;
                            while (run2) {
                                System.out.println("1.Skapa konto");
                                System.out.println("2.Ta bort konto");
                                System.out.println("3.Skicka belopp");
                                System.out.println("4.Visa transaktioner");
                                System.out.println("5.Uppdatera användare info");
                                System.out.println("6.Ta bort användare");
                                System.out.println("7.Visa dina användare uppgifter");
                                System.out.println("8.Logga ut");

                                switch (scanner.nextLine().trim()) {
                                    case "1" -> CreateAccount();
                                    case "2" -> DeleteAccount();
                                    case "3" -> MakeTransactionBetweenAccounts();
                                    case "4" -> WhenTransactionWasMade();
                                    case "5" -> UpdateUserInfo();
                                    case "6" -> {
                                        DeleteUser();
                                        run2 = false;
                                    }
                                    case "7" -> {
                                        ShowLoggedInUserInfo();
                                        ShowLoggedInAccounts();
                                    }
                                    case "8" -> run2 = false;

                                }
                            }

                        } else {
                            System.out.println("Användaren gick ej att hitta");
                        }
                    }
                }
                case "3" -> run = false;
                default -> {
                }
            }

        }

    }


    //Konfigurerar kopplingar mot databasen
    public static void InitializeDatabase() {
        try {
            dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setUrl("jdbc:mysql://" + url + ":" + port + "/" + database + "?serverTimezone=UTC");
            dataSource.setUseSSL(false);
        } catch (SQLException e) {
            System.out.print("failed!\n");
            PrintSQLException(e);
            System.exit(0);
        }
    }

    //Skapar en tillfällig koppling till databasen
    public static Connection GetConnection() {
        try {
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException e) {
            System.out.printf("failed!\n");
            PrintSQLException(e);
            System.exit(0);
            return null;
        }
    }

    public static void PrintSQLException(SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }

    // tar user-id och password, jämför password med det i databasen och sparar id och personalnumber med setter
    public static boolean CheckCredentials(int personalNumber, String password) throws SQLException {
        Connection connection = GetConnection();
        String query = "SELECT user_id, password FROM users WHERE personal_number = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1,personalNumber);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String passwordDatabase = resultSet.getString("password");
            preparedStatement.close();
            connection.close();
            setLoggedInID(userId);
            setLoggedInPersonalNumber(personalNumber);
            return passwordDatabase.equals(password);
        } else {
            preparedStatement.close();
            connection.close();
            return false;
        }
    }

    //Skapar användare tabell
    public static void CreateUserTable() throws SQLException {
        Connection connection = GetConnection();
        Statement statement = connection.createStatement();
        String query = "CREATE TABLE IF NOT EXISTS users (user_id INT PRIMARY KEY AUTO_INCREMENT," +
                " name VARCHAR(100)," +
                " password VARCHAR(100)," +
                " personal_number INT," +
                " email VARCHAR(100)," +
                "phone VARCHAR(30)," +
                "address VARCHAR(100)," +
                "created DATE Default (CURRENT_DATE)) ";

        statement.executeUpdate(query);
        connection.close();
    }


    // Skapar konto tabell
    public static void CreateAccountTable() throws SQLException {

        Connection connection = GetConnection();
        Statement statement = connection.createStatement();

        String query = "CREATE TABLE IF NOT EXISTS accounts (" +
                "account_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "bban INT, " +
                "balance DOUBLE, " +
                "user_id INT, " +
                "created DATE Default (CURRENT_DATE) " +
                ");";

        //Kör SQL-query och returnera resultatet (Antalet påverkade rader returneras)
        statement.executeUpdate(query);

        //Stäng databaskoppling och returnera den till databaspoolen
        connection.close();

    }

    //Skapar transaktion tabell
    public static void CreateTransactionsTable() throws SQLException {

        Connection connection = GetConnection();
        Statement statement = connection.createStatement();
        String query = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "senderAccount_id INT, " +
                "receiverAccount_id INT, " +
                "amount DOUBLE, " +
                "date TIMESTAMP Default (CURRENT_TIMESTAMP) " +
                ");";

        //Kör SQL-query och returnera resultatet (Antalet påverkade rader returneras)
        statement.executeUpdate(query);

        //Stäng databaskoppling och returnera den till databaspoolen
        connection.close();
    }

    //Skapar användare med angiven information
    public static void CreateUser() throws SQLException {
        System.out.print("Ange namn: ");
        String name = scanner.nextLine().trim();
        System.out.print("Ange lösenord: ");
        String password = scanner.nextLine().trim();
        System.out.print("Ange personnummer: ");
        int personal_number = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ange email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Ange telefonnummer: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Ange address: ");
        String address = scanner.nextLine().trim();

        InsertToUsersTable(name, password,personal_number, email, phone, address);

    }
    // tillägger användare information till userstable i databasen
    public static void InsertToUsersTable(String name, String password, int personal_number, String email, String phone, String address) throws SQLException {

        Connection connection = GetConnection();
        String query = "INSERT INTO users (name, password, personal_number, email, phone, address) VALUES (?,?,?,?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        preparedStatement.setInt(3, personal_number);
        preparedStatement.setString(4, email);
        preparedStatement.setString(5, phone);
        preparedStatement.setString(6, address);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }


        //Skapar konto med angiven information
    public static void CreateAccount() throws SQLException {

        System.out.print("Ange kontonummer: ");
        int bban = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ange saldo: ");
        double balance = scanner.nextDouble();
        scanner.nextLine();
        InsertToAccountsTable(getLoggedInPersonalNumber(), bban, balance);
    }

    // lägger till kontoinformation och userid till databasen
    public static void InsertToAccountsTable(int personal_number, int bban, Double balance) throws SQLException {
        Connection connection = GetConnection();
        int id = GetUserIdByPersonalNumber(personal_number);

        String query = "INSERT INTO accounts (user_id, bban, balance) VALUES (?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, bban);
        preparedStatement.setDouble(3, balance);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    //hämtar och returnerar userid genom personalnumber
    public static int GetUserIdByPersonalNumber(int personalNumber) throws SQLException {
        Connection connection = GetConnection();
        String query = "SELECT user_id FROM users WHERE personal_number = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, personalNumber);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            connection.close();
            return userId;
        } else {
            connection.close();
            return -1;
        }
    }

    //metod för att visa transaktioner för ett konto mellan två datum
    public static void WhenTransactionWasMade() throws SQLException {
        Connection connection = GetConnection();
        ShowLoggedInAccounts();
        if (!getLoggedInAccountsIDsArr().isEmpty()){


            System.out.print("Ange ID på kontot för att se dess transaktioner: ");
            int accountId = scanner.nextInt();
            scanner.nextLine();
            if (IdIsInArray(getLoggedInAccountsIDsArr(), accountId)) {

                System.out.print("Välj start datum (yyyy-mm-dd): ");
                String startDateStr = scanner.nextLine();
                LocalDate startDate = LocalDate.parse(startDateStr);

                System.out.print("Välj slut datum (yyyy-mm-dd): ");
                String endDateStr = scanner.nextLine();
                LocalDate endDate = LocalDate.parse(endDateStr);

                String query = "SELECT accounts.account_id, accounts.bban, accounts.balance, transactions.senderAccount_id, transactions.receiverAccount_id, transactions.amount, transactions.date " +
                        "FROM accounts " +
                        "INNER JOIN transactions ON accounts.account_id = transactions.senderAccount_id OR accounts.account_id = transactions.receiverAccount_id " +
                        "WHERE accounts.account_id = ? AND DATE(transactions.date) BETWEEN ? AND ? " +
                        "ORDER BY transactions.date DESC";

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, accountId);
                preparedStatement.setObject(2, startDate);
                preparedStatement.setObject(3, endDate);
                ResultSet result = preparedStatement.executeQuery();

                if (result.next()) {
                    int accountID = result.getInt("account_id");
                    int accountBBAN = result.getInt("bban");
                    double balance = result.getDouble("balance");
                    System.out.println(accountID + ", " + accountBBAN + ", " + balance + "\n");


                    do {
                        int senderAccountID = result.getInt("senderAccount_id");
                        String receiverAccountID = result.getString("receiverAccount_id");
                        String amount = result.getString("amount");
                        String date = result.getString("date");
                        System.out.println("Sändare ID: " + senderAccountID + "  Mottagare ID: " + receiverAccountID + "  Belopp: " + amount + " Datum: " + date);

                    } while (result.next());
                } else {
                    System.out.println("Finns inga transaktioner för detta kontot mellan datumen");
                }
                preparedStatement.close();
            } else {
                System.out.println("ID:t på kontot du har angett finns inte");
            }
        } else{
            System.out.println("Inga konton tillgängliga");
        }
        connection.close();
    }

    //kollar om det valda idet finns i array
    public static boolean IdIsInArray(ArrayList<Integer> array, int id) {
        for (int element : array) {
            if (element == id) {
                return true;
            }
        }
        return false;
    }

    //metod för att göra transaktioner mellan konton som finns i databasen
    public static void MakeTransactionBetweenAccounts() throws SQLException {
        ShowLoggedInAccounts();
        System.out.println("Ange sändarens konto ID: ");
        int senderAccountID = Integer.parseInt(scanner.nextLine().trim());

        if (IdIsInArray(getLoggedInAccountsIDsArr(), senderAccountID)) {
            ShowAllUserNames();
            System.out.println("Välj mottagare: ");
            int receiverID = scanner.nextInt();
            ShowAllReceiverAccounts(receiverID);
            scanner.nextLine();
            if (IdIsInArray(getAllUsersIdExceptForLoggedInArr(), receiverID)) {
                System.out.println("Ange mottagares konto ID: ");
                int receiverAccountID = Integer.parseInt(scanner.nextLine().trim());
                if (IdIsInArray(getReceiverAccountsIDsArr(), receiverAccountID)) {
                    System.out.println("Ange belopp: ");
                    double amount = Double.parseDouble(scanner.nextLine().trim());
                    boolean checker = BalanceChecker(senderAccountID, amount);

                    if (checker) {

                        Connection connection = GetConnection();
                        String query = "INSERT INTO transactions (senderAccount_id, receiverAccount_id, amount) VALUES (?,?,?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                        preparedStatement.setInt(1, senderAccountID);
                        preparedStatement.setInt(2, receiverAccountID);
                        preparedStatement.setDouble(3, amount);
                        int result = preparedStatement.executeUpdate();

                        if (result > 0) {
                            UpdateBalance(senderAccountID, -amount);
                            UpdateBalance(receiverAccountID, amount);

                            System.out.println("Betalning genomförd");
                        } else {
                            System.out.println("Betalning misslyckades");
                        }

                        preparedStatement.close();
                        connection.close();

                    } else {
                        System.out.println("Inte tillräckligt med saldo");
                    }
                } else {
                    System.out.println("Mottagaren har inget konto eller du skrev ett ID som inte finns i listan");
                }
            } else {
                System.out.println("Du måste välja ett ID som finns i listan");
            }
        } else {
            System.out.println("Du måste välja ett ID som finns i listan");
        }
    }

    //kollar om det finns tillräckligt med saldo på kontot för att transaktionen ska bli genomförd
    public static boolean BalanceChecker(int id, double amount) throws SQLException {
        Connection connection = GetConnection();
        String query = "SELECT balance FROM accounts WHERE account_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet result = preparedStatement.executeQuery();

        if (result.next()) {
            double balance = result.getDouble("balance");

            balance = balance - amount;
            preparedStatement.close();
            connection.close();
            return balance >= 0.0;
        } else {
            preparedStatement.close();
            connection.close();
            return false;
        }
    }

    //metod för att uppdatera saldo på konton
    public static void UpdateBalance(int account_id, double balance) throws SQLException {
        Connection connection = GetConnection();
        String query = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setDouble(1, balance);
        preparedStatement.setInt(2, account_id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        connection.close();
    }

    //uppdaterar användare information
    public static void UpdateUserInfo() throws SQLException {
        ShowLoggedInUserInfo();
        System.out.print("Ange nytt namn: ");
        String name = scanner.nextLine().trim();
        System.out.print("Ange nytt lösenord: ");
        String password = scanner.nextLine().trim();
        System.out.print("Ange ny email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Ange nytt telefonnummer: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Ange ny address: ");
        String address = scanner.nextLine().trim();
        String query = "UPDATE users SET name = ?, password = ?, email = ?, phone = ?, address = ? WHERE user_id = ?";
        Connection connection = GetConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, phone);
        preparedStatement.setString(5, address);
        preparedStatement.setInt(6, getLoggedInID());
        preparedStatement.executeUpdate();
        connection.close();
    }

    //tar bort användare från databasen och konton som tillhör användare
    public static void DeleteUser() throws SQLException {
        Connection connection = GetConnection();
        ShowLoggedInUserInfo();
        System.out.println("Skriv ja för att bekräfta borttagning");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("ja")) {
            String query = "DELETE users, accounts FROM users LEFT JOIN accounts ON users.user_id = accounts.user_id WHERE users.user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getLoggedInID());
            preparedStatement.executeUpdate();
            System.out.println("Borttagning genomförd");
            preparedStatement.close();
        } else {
            System.out.println("Borttagning nekad");
        }
        connection.close();
    }

    // tar bort konto från databasen
    public static void DeleteAccount() throws SQLException {
        Connection connection = GetConnection();
        ShowLoggedInAccounts();
        if (!getLoggedInAccountsIDsArr().isEmpty()) {
            System.out.print("Ange konto ID: ");
            int account_id = Integer.parseInt(scanner.nextLine().trim());
            if (IdIsInArray(getLoggedInAccountsIDsArr(), account_id)) {

                System.out.println("Skriv ja bekräfta borttagning ");
                String confirmation = scanner.nextLine();

                if (confirmation.equalsIgnoreCase("ja")) {
                    setToRemoveIDFromLoggedInAccountsIDsArr(account_id);
                    String query = "DELETE FROM accounts WHERE account_id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, account_id);
                    preparedStatement.executeUpdate();
                    System.out.println("Borttagning genomförd");
                } else {
                    System.out.println("Borttagning nekad");
                }
            } else{
                System.out.println("Du måste ange ett Konto ID från listan");
            }
        } else {
            System.out.println("Användaren har inget konto");
        }
        connection.close();
    }

    //visar namn och id på alla i databasen förutom den som är inloggad
    public static void ShowAllUserNames() throws SQLException {
        String query = "SELECT user_id, name FROM users";
        Connection connection = GetConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        System.out.println("Tillgängliga Namn: ");

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int userid = resultSet.getInt("user_id");
            if (getLoggedInID() != userid) {
                setAllUsersIdExceptForLoggedInArr(userid);
                System.out.println(userid + "." + name);
            }
        }
        resultSet.close();
        statement.close();
        connection.close();
    }

    // visar info på användaren som är inloggad
    public static void ShowLoggedInUserInfo() throws SQLException {
        String query = "SELECT * FROM users";
        Connection connection = GetConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        System.out.println("Aktiv användare: ");

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int userid = resultSet.getInt("user_id");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");
            String phone = resultSet.getString("phone");
            String address = resultSet.getString("address");

            if (getLoggedInID() == userid) {
                System.out.println(name + " - " + password + " - " + email + " - " + phone + " - " + address);
            }
        }
        resultSet.close();
        statement.close();
        connection.close();
    }

    // visar alla konto för den som är inloggad
    public static void ShowLoggedInAccounts() throws SQLException {
        Connection connection = GetConnection();
        String query = "SELECT * FROM accounts WHERE user_id = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, getLoggedInID());
        ResultSet result = preparedStatement.executeQuery();
        setToEmptyLoggedInAccountIDsArr();
        while (result.next()) {
            int accountId = result.getInt("account_id");
            int bban = result.getInt("bban");
            double balance = result.getDouble("balance");
            setLoggedInAccountsIDsArr(accountId);
            System.out.println("Konto ID: " + accountId + " Kontonummer: " + bban + " Tillgängligt belopp: " + balance);
        }
        result.close();
        preparedStatement.close();
        connection.close();
    }

    //visar konton för mottagaren
    public static void ShowAllReceiverAccounts(int id) throws SQLException {
        Connection connection = GetConnection();
        String query = "SELECT account_id, bban FROM accounts WHERE user_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        System.out.println("Tillgängliga Konton: ");

        while (resultSet.next()) {
            int bban = resultSet.getInt("bban");
            int account_id = resultSet.getInt("account_id");
            setReceiverAccountsIDsArr(account_id);
            System.out.println(account_id + "." + bban);
        }
        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}
