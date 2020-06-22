package com.ksiegarnia;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
//import javax.swing.CheckBoxList;

class UkladKsiegarni extends JFrame {
    // url for connection to DB
    private final String jdbcUrl = "jdbc:mysql://localhost:3306/ksiegarnia";
    private final String jdbcUser = "root";
    private final String jdbcPass = "";

    // log information field
    private final JTextField log = new JTextField();
    private final JTextField polePesel = new JTextField();
    private final JTextField poleImie = new JTextField();
    private final JTextField poleNazwisko = new JTextField();
    private final JTextField poleUrodziny = new JTextField();
    private final JTextField poleMail = new JTextField();
    private final JTextField poleAdres = new JTextField();
    private final JTextField poleTelephone = new JTextField();
    private final JTextField poleISBN = new JTextField();
    private final JTextField poleAutor = new JTextField();
    private final JTextField poleTytul = new JTextField();
    private final JTextField poleWydaw = new JTextField();
    private final JTextField poleRok = new JTextField();
    private final JTextField poleCena = new JTextField();
    private final JTextField poleDataOrder = new JTextField();
    private final JTextField poleNewPrice = new JTextField();

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JPanel panelClient = new JPanel(); // klienci
    private final JPanel panelBooks = new JPanel(); // ksiązki
    private final JPanel panelOrder = new JPanel(); // zamówiemia

    private final DefaultListModel<String> listModelBooks = new DefaultListModel<>();
    private final DefaultListModel<String> listModelClient = new DefaultListModel<>();
    private final JList<String> listClient = new JList<>(listModelClient);
    private final JList<String> listBooks = new JList<>(listModelBooks);
    private final JScrollPane scrollPaneClient = new JScrollPane(listClient);
    private final JScrollPane scrollPaneBooks = new JScrollPane(listBooks);
    private final JScrollPane scrollPaneBookOrder;
    private final JScrollPane scrollPaneKlientOrder;
    private final JScrollPane scrollPaneOrder;

    private final JButton buttonSaveClient = new JButton("Zapisz");
    private final JButton buttonDellClient = new JButton("Usuń");
    private final JButton buttonSaveBook = new JButton("Zapisz");
    private final JButton buttonDellBook = new JButton("Usuń");
    private final JButton buttonNewPrice = new JButton("Zmień cenę");
    private final JButton buttonNewStatusOrder = new JButton("Zmień status");
    private final JButton buttonNewOrder = new JButton("Dodaj zamówienie");


    private final DefaultListModel<String> listModelKlientOrder = new DefaultListModel<>();
    private final DefaultListModel<String> listModelOrder = new DefaultListModel<>();
    private final DefaultListModel<String> listModelBookOrder = new DefaultListModel<>();
    private final JList<String> listBookOrder = new JList<>(listModelBookOrder);
    private final JList<String> listKlientOrder = new JList<>(listModelKlientOrder);
    private final JList<String> listOrder = new JList<>(listModelOrder);

    private final JComboBox comboNewStatusOrder = new JComboBox();
    private final JComboBox comboTyp = new JComboBox();
    private final JComboBox comboStatusOrder = new JComboBox();

    private void updateClientList() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie, adres FROM klienci, kontakty WHERE klienci.pesel = kontakty.pesel ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            listModelClient.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3) + ", " + res.getString(4);
                listModelClient.addElement(s);
            }
        }
        catch (SQLException ex) {
            log.setText("Lista klientow nie zostala zaktualizowana.");
        }
    }


    private boolean isDataValid(String dataInput){
        int year = Integer.parseInt(dataInput.substring(0,4));
        int month = Integer.parseInt(dataInput.substring(5,7));
        int day = Integer.parseInt(dataInput.substring(8,10));

        if (! dataInput.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}")) return false;
        if (year < 1920 || 2006 < year ) return false;
        if (month < 1 || 12 < month) return false;

        return day >= 1 && 31 >= day;
    }

    private final ActionListener akc_zap_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String pesel = polePesel.getText();
            if (! pesel.matches("[0-9]{3,11}")) {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Zły numer PESEL");
                polePesel.setText("");
                polePesel.requestFocus();
                return;
            }

            String imie = poleImie.getText();
            String nazwisko = poleNazwisko.getText();
            String ur = poleUrodziny.getText();
            if (! isDataValid(ur)) {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Data [rok-miesiec-dzien] np.1996-10-12");
                poleUrodziny.setText("");
                poleUrodziny.requestFocus();
                return;
            }
            if (imie.equals("") || nazwisko.equals("") || ur.equals("")) {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Imię, nazwisko badż data urodzenia nie zostały podane");
                return;
            }

            String mail = poleMail.getText();

            String adr = poleAdres.getText();
            String tel = poleTelephone.getText();
            if (mail.equals("") || adr.equals("") || tel.equals(""))
            {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "E-mail lub adres nie jest wypełniony");
                return;
            }

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sqlReqestKlient = "INSERT INTO klienci (pesel, imie, nazwisko, ur) VALUES('" + polePesel.getText() + "', '" + poleImie.getText() + "', '" + poleNazwisko.getText() + "', '" + poleUrodziny.getText() + "')";
                int res = stmt.executeUpdate(sqlReqestKlient);
                if (res == 1)
                {
                    log.setText("OK - klient został dodany do bazy");
                    String sqlRequestKontakt = "INSERT INTO kontakty (pesel, mail, adres, tel) VALUES('" + polePesel.getText() + "', '" + poleMail.getText() + "', '" + poleAdres.getText() + "', '" + poleTelephone.getText() + "')";
                    stmt.executeUpdate(sqlRequestKontakt);
                    updateClientList();
                }
            }
            catch(SQLException ex) { log.setText("Błąd SQL - nie zapisano klienta: " + ex); }
        }
    };

    // ActionListener for buttonDellClient
    private final ActionListener akc_usun_kli = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            log.setText(listClient.getModel().getElementAt(listClient.getSelectionModel().getMinSelectionIndex()));
            if (listClient.getSelectedIndices().length == 0) return; // listClient.getSelectionModel().getSelectedItemsCount() == 0
            String p = listClient.getModel().getElementAt(listClient.getSelectionModel().getMinSelectionIndex());
            System.out.println("\nSelected item from JList:  " + p);
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zamowienia WHERE pesel = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0) {
                    String sql1 = "DELETE FROM klienci WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    String sql2 = "DELETE FROM kontakty WHERE pesel = '" + p + "'";
                    stmt.executeUpdate(sql2);
                    log.setText("Klient usunięty bazy");
                    updateClientList();
                }
                else log.setText("Klient zlozył zamówienie, nie mozna go usunąć");
            }
            catch (SQLException ex) {
                log.setText("Błąd SQL - nie usuięto klienta");
            }
        }
    };

    //function to update date on list BOOKS
    private void updateBooksList() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT `isbn`, `autor`, `tytul`, `typ`, `rok`, `cena` FROM `ksiazki` WHERE 1 ORDER BY autor, tytul";
            ResultSet res = stmt.executeQuery(sql);
            listModelBooks.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + ": " + res.getString(3) + ": " + res.getString(4) + " " + res.getString(5) + " " + res.getString(6);
                listModelBooks.addElement(s);
            }
        }
        catch (SQLException ex) {
            log.setText("Nie udało się zaktualizować listy klientów");
        }
    }

    // ActionListener for buttonSaveBook
    private final ActionListener akc_zap_ksia = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String ISBN = poleISBN.getText();
            if (! ISBN.matches("[0-9]{3,11}")) {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Błąd w polu z ISBN");
                poleISBN.setText("");
                poleISBN.requestFocus();
                return;
            }

            String autor = poleAutor.getText();
            String tytul = poleTytul.getText();

            Ksiazki ksiazki;
            ksiazki = (Ksiazki)comboTyp.getItemAt(comboTyp.getSelectedIndex());
            String typ = ksiazki.name();
            if (autor.equals("") || tytul.equals("") || typ.equals(""))
            {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Nie zostało wypełnione pole z autorem, tytulem lub typem książki");
                return;
            }

            String wydaw = poleWydaw.getText();
            String rok = poleRok.getText();
            String cena = poleCena.getText();
            if (cena.equals(""))
            {
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Nie zostało wypełnione pole z ceną");
                return;
            }

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sqlInsertKsia = "INSERT INTO `ksiazki`(`isbn`, `autor`, `tytul`, `typ`, `wydawnictwo`, `rok`, `cena`) VALUES ('" + ISBN + "','" + autor + "','" + tytul + "','" + typ + "','" + wydaw + "','" + rok + "','" + cena + "')";
                int res = stmt.executeUpdate(sqlInsertKsia);
                if (res == 1) {
                    log.setText("Książka została dodana do bazy");
                    updateBooksList();
                }
            }
            catch(SQLException ex) {
                log.setText("Błąd SQL - nie zapisano klienta: " + ex);
            }
        }
    };

    // ActionListener for buttonDellBook
    private final ActionListener akc_usun_ksia = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            log.setText(listBooks.getModel().getElementAt(listBooks.getSelectionModel().getMinSelectionIndex()));
            if (listBooks.getSelectedIndices().length == 0) // listClient.getSelectionModel().getSelectedItemsCount() == 0
                return;
            String p = listBooks.getModel().getElementAt(listBooks.getSelectionModel().getMinSelectionIndex());
            System.out.println("\nSelected item from JList for delete:  " + p);
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn=DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT COUNT(*) FROM zestawienia WHERE isbn = '" + p + "'";
                ResultSet res = stmt.executeQuery(sql);
                res.next();
                int k = res.getInt(1);
                if (k == 0)
                {
                    String sql1 = "DELETE FROM ksiazki WHERE isbn = '" + p + "'";
                    stmt.executeUpdate(sql1);
                    log.setText("Ksiazka została usunięta z bazy");
                    updateBooksList();
                }
                else log.setText("Ksiazka zostala zamowiona, nie mozna jej usunąć");
            }
            catch (SQLException ex)
            {
                log.setText("Błąd SQL - nie usunięto ksiazki");
            }
        }
    };

    // ------- function to change price ---
    private final ActionListener chenge_price_book = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String newPrice = poleNewPrice.getText();

            log.setText(listBooks.getModel().getElementAt(listBooks.getSelectionModel().getMinSelectionIndex()));
            if (listBooks.getSelectedIndices().length == 0 || newPrice.equals("")) // listClient.getSelectionModel().getSelectedItemsCount() == 0
                return;
            String p = listBooks.getModel().getElementAt(listBooks.getSelectionModel().getMinSelectionIndex());
            System.out.println("\nSelected item from JList for change prise:  " + p);
            p = p.substring(0, p.indexOf(':'));
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                String sql = "UPDATE `ksiazki` SET `cena`='" + newPrice + "' WHERE `isbn`=`" + p + "`";

                int res = stmt.executeUpdate(sql);
                if (res == 1) {
                    log.setText("Cena ksiazki została zmieniona");
                    updateBooksList();
                } else
                    {
                    log.setText("Nie zmieniono ceny ksiazki");
                }
            } catch (SQLException ex) {
                log.setText("Błąd SQL - nie usunięto ksiazki");
            }
        }
    };


    //function to update date on list CLIENT on ORDER panel
    private void updateClientListOrder() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sql = "SELECT klienci.pesel, nazwisko, imie FROM klienci ORDER BY nazwisko, imie";
            ResultSet res = stmt.executeQuery(sql);
            listModelKlientOrder.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3);
                listModelKlientOrder.addElement(s);
            }
        }
        catch (SQLException ex) {
            log.setText("Nie udało się zaktualizować listy klientów");
        }
    }

    //function to update date on list BOOK on ORDER panel
    private void updateBooksListOrder() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sqlSelect = "SELECT isbn, autor, tytul, cena FROM ksiazki ORDER BY tytul";
            ResultSet res = stmt.executeQuery(sqlSelect);
            listModelBookOrder.clear();
            while(res.next()) {
                String s = res.getString(1) + ": " + res.getString(2) + " " + res.getString(3);
                listModelBookOrder.addElement(s);
            }
        }
        catch (SQLException ex) {
            log.setText("Nie udało się zaktualizować listy klientów");
        }
    }

    /* validation for date
     * Date format for SQL INSERT in DB
     * yyy-mm-dd
     * year mast be 2020 or greater
     * TODO add checking by today date (not greater then today)
     */
    private boolean isDataOrderValid(String dataInput){
        int y = Integer.parseInt(dataInput.substring(0,4));
        int m = Integer.parseInt(dataInput.substring(5,7));
        int d = Integer.parseInt(dataInput.substring(8,10));

        /*System.out.println("Input DATE:\ny - " + y +
         *                               "\nm - " + m +
         *                               "\nd - " + d);
        */

        if (! dataInput.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}"))
            return false;
        if (2020 > y)
            return false;
        if (m < 1 || 12 < m)
            return false;
        return d >= 1 && 31 >= d;
    }

    /* ------- function to add new order ---
    *  Tables to update on DB:
    *  `zamowienia`
    *  `zestawienia`
    */
    private final ActionListener akc_add_order = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String orderDate = poleDataOrder.getText();
            if (!isDataOrderValid(orderDate)){
                JOptionPane.showMessageDialog(UkladKsiegarni.this, "Data [rok-miesiec-dzien] np.2020-05-17");
                poleDataOrder.setText("");
                poleDataOrder.requestFocus();
                return;
            }

            Status status;
            status = (Status) comboStatusOrder.getItemAt(comboStatusOrder.getSelectedIndex());
            String orderStatus = status.name();

            if (listBookOrder.getSelectedIndices().length == 0 || listKlientOrder.getSelectedIndices().length == 0)
                return;

            String p = listKlientOrder.getModel().getElementAt(listKlientOrder.getSelectionModel().getMinSelectionIndex());
            System.out.println("\nSelected Client from JList for create an order:  " + p);
            String orderClientKey = p.substring(0, p.indexOf(':'));
            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();

                String sqlInsertOrder = "INSERT INTO `zamowienia`(`pesel`, `kiedy`, `status`) VALUES ('" + orderClientKey + "', '" + orderDate + "', '" + orderStatus + "')";
                int resInsertOrder = stmt.executeUpdate(sqlInsertOrder);

                String sqlSelectOrderKey = "SELECT `id` FROM `zamowienia` WHERE `pesel`= " + orderClientKey + " AND `kiedy` = '" + orderDate + "' AND `status` = '" + orderStatus + "'";
                ResultSet resSelectOrderKey = stmt.executeQuery(sqlSelectOrderKey);
                resSelectOrderKey.next();
                int orderKey = resSelectOrderKey.getInt(1);
                System.out.println("\n Added order with PK: " + orderKey);

                System.out.println("\n Selected " + listBookOrder.getSelectedIndices().length + " books for order\n");
                for (int i = 0; i < listBookOrder.getSelectedIndices().length; i++) {
                    String bookToOrder = listBookOrder.getModel().getElementAt(i);
                    System.out.println(i+1 + " selected book from JList for order:  " + bookToOrder);

                    String keyBookToOrder = bookToOrder.substring(0, bookToOrder.indexOf(':'));
                    System.out.println("Key ordered book: " + keyBookToOrder);
                    String sqlSelectOrderBookPrice = "SELECT `cena` FROM `ksiazki` WHERE `isbn`=" + keyBookToOrder;
                    ResultSet resSelectOrderBookPrice = stmt.executeQuery(sqlSelectOrderBookPrice);
                    resSelectOrderBookPrice.next();
                    double orderBookPrice = resSelectOrderBookPrice.getDouble(1);
                    System.out.println("  witch price: " + orderBookPrice);

                    System.out.println("INSERT INTO `zestawienia`(`id`, `isbn`, `cena`) VALUES (`" + orderKey + "`, `" + keyBookToOrder + "` ,`" + orderBookPrice + "`)");
                    String sqlInsertOrderBook = "INSERT INTO `zestawienia`(`id`, `isbn`, `cena`) VALUES (" + orderKey + ", " + keyBookToOrder + " ," + orderBookPrice + ")";
                    int resInsertOrderBook = stmt.executeUpdate(sqlInsertOrderBook);
                    System.out.println(i + " book added result: " + resInsertOrderBook);
                }

                if (resInsertOrder == 1) {
                    log.setText("Cena książki została");
                    updateOrderList();
                } else {
                    log.setText("Nie zmieniono ceny ksiazki");
                }
            } catch (SQLException ex) {
                log.setText("Błąd SQL - nie usunięto ksiazki");
            }
        }
    };

    //function to update date on list ORDER on ORDER panel
    private void updateOrderList() {
        try (Connection conn= DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
            Statement stmt = conn.createStatement();
            String sqlSelectFromZamow = "SELECT book.id, kiedy, pesel, status, COUNT(*) FROM `zestawienia` book INNER JOIN zamowienia ord ON ord.id = book.id GROUP BY book.id";
            ResultSet resOrder = stmt.executeQuery(sqlSelectFromZamow);
            listModelOrder.clear();

            while(resOrder.next()) {
                String orderListItem = resOrder.getString(1) + ": " + resOrder.getString(2) + " " + resOrder.getString(3) + " " + resOrder.getString(4) + " zamówiono " + resOrder.getString(5) + " książek";
                listModelOrder.addElement(orderListItem);
            }
        }
        catch (SQLException ex) {
            log.setText("Lista zamówień nie została zaktualizowana " + ex);
            System.out.println(ex);
        }
    }

    // ------- function to change status ---
    private final ActionListener akc_change_status = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            Status status;
            status = (Status)comboNewStatusOrder.getItemAt(comboNewStatusOrder.getSelectedIndex());
            String orderStatus = status.name();

            log.setText(listOrder.getModel().getElementAt(listOrder.getSelectionModel().getMinSelectionIndex()));
            if (listOrder.getSelectedIndices().length == 0)
                return;
            String selectedOrder = listOrder.getModel().getElementAt(listOrder.getSelectionModel().getMinSelectionIndex());
            System.out.println("\nSelected item from JList to change status:  " + selectedOrder);
            String keyOrder = selectedOrder.substring(0, selectedOrder.indexOf(':'));

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass)) {
                Statement stmt = conn.createStatement();
                System.out.println("UPDATE `zamowienia` SET `status`='" + orderStatus + "' WHERE `id`='" + keyOrder + "'");
                String sql = "UPDATE `zamowienia` SET `status`='" + orderStatus + "' WHERE `id`='" + keyOrder + "'";
                int res = stmt.executeUpdate(sql);
                if (res == 1) {
                    log.setText("Status zamówienia został zmieniony");
                    updateOrderList();
                } else {
                    log.setText("Status zamówienia nie został zmieniony");
                }
            } catch (SQLException ex) {
                log.setText("Błąd SQL - nie zmieniono statusu zamuwienia");
            }
        }
    };

    public UkladKsiegarni() throws SQLException {
        super("Księgarnia");
        setSize(860, 560);
        setLocation(100, 100);
        setResizable(false);

        // add panels
        tabbedPane.addTab("Klienci", panelClient);
        tabbedPane.addTab("Książki", panelBooks);
        tabbedPane.addTab("Zamówienia", panelOrder);
        // add by border layout container
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        // information on bottom uneditable
        log.setEditable(false);
        getContentPane().add(log, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);



        // Zakładka dla klienta
        panelClient.setLayout(null);

        JLabel lab1 = new JLabel("Numer Pesel:");
        panelClient.add(lab1);
        lab1.setSize(100, 20);
        lab1.setLocation(40, 20);
        lab1.setHorizontalTextPosition(JLabel.RIGHT);

        panelClient.add(polePesel);
        polePesel.setSize(200, 20);
        polePesel.setLocation(160, 20);

        JLabel lab2 = new JLabel("Imię:");
        Component c = panelClient.add(lab2);
        lab2.setSize(100, 20);
        lab2.setLocation(40, 60);
        lab2.setHorizontalTextPosition(JLabel.RIGHT);

        panelClient.add(poleImie);
        poleImie.setSize(200, 20);
        poleImie.setLocation(160, 60);

        JLabel lab3 = new JLabel("Nazwisko:");
        panelClient.add(lab3);
        lab3.setSize(100, 20);
        lab3.setLocation(40, 100);
        lab3.setHorizontalTextPosition(JLabel.RIGHT);

        panelClient.add(poleNazwisko);
        poleNazwisko.setSize(200, 20);
        poleNazwisko.setLocation(160, 100);

        JLabel lab4 = new JLabel("Data urodzenia:");
        panelClient.add(lab4);
        lab4.setSize(100, 20);
        lab4.setLocation(40, 140);
        lab4.setHorizontalTextPosition(JLabel.RIGHT);

        panelClient.add(poleUrodziny);
        poleUrodziny.setSize(200, 20);
        poleUrodziny.setLocation(160, 140);

        JLabel lab5 = new JLabel("Adres e-mail:");
        panelClient.add(lab5);
        lab5.setSize(100, 20);
        lab5.setLocation(40, 180);
        lab5.setHorizontalTextPosition(JLabel.RIGHT);
        panelClient.add(poleMail);
        poleMail.setSize(200, 20);
        poleMail.setLocation(160, 180);

        JLabel lab6 = new JLabel("Adres zamieszkania:");
        panelClient.add(lab6);
        lab6.setSize(100, 20);
        lab6.setLocation(40, 220);
        lab6.setHorizontalTextPosition(JLabel.RIGHT);
        panelClient.add(poleAdres);
        poleAdres.setSize(200, 30);
        poleAdres.setLocation(160, 220);

        JLabel lab7 = new JLabel("Numer telefonu:");
        panelClient.add(lab7);
        lab7.setSize(100, 20);
        lab7.setLocation(40, 260);
        lab7.setHorizontalTextPosition(JLabel.RIGHT);
        panelClient.add(poleTelephone);
        poleTelephone.setSize(200, 20);
        poleTelephone.setLocation(160, 260);

        panelClient.add(buttonSaveClient);
        buttonSaveClient.setSize(200, 30);
        buttonSaveClient.setLocation(160, 300);
        buttonSaveClient.addActionListener(akc_zap_kli);

        panelClient.add(buttonDellClient);
        buttonDellClient.setSize(400, 30);
        buttonDellClient.setLocation(400, 300);
        buttonDellClient.addActionListener(akc_usun_kli);

        panelClient.add(scrollPaneClient);
        scrollPaneClient.setSize(400, 260);
        scrollPaneClient.setLocation(400, 20);
        listClient.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateClientList();



        // Zakładka książki
        panelBooks.setLayout(null);

        JLabel labelISBN = new JLabel("ISBN:");
        panelBooks.add(labelISBN);
        labelISBN.setSize(100, 20);
        labelISBN.setLocation(40, 20);
        labelISBN.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleISBN);
        poleISBN.setSize(200, 20);
        poleISBN.setLocation(160, 20);

        JLabel labelAutor = new JLabel("Autor ksiazki:");
        panelBooks.add(labelAutor);
        labelAutor.setSize(100, 20);
        labelAutor.setLocation(40, 60);
        labelAutor.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleAutor);
        poleAutor.setSize(200, 20);
        poleAutor.setLocation(160, 60);

        JLabel labelTytul = new JLabel("Tytuł:");
        panelBooks.add(labelTytul);
        labelTytul.setSize(100, 20);
        labelTytul.setLocation(40, 100);
        labelTytul.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleTytul);
        poleTytul.setSize(200, 20);
        poleTytul.setLocation(160, 100);

        JLabel labelTyp = new JLabel("Typ książki:");
        panelBooks.add(labelTyp);
        labelTyp.setSize(100, 20);
        labelTyp.setLocation(40, 140);
        labelTyp.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(comboTyp);
        for (Ksiazki ksiazki : Ksiazki.values()) {
            comboTyp.addItem(ksiazki);
        }
        comboTyp.setSize(100,20);
        comboTyp.setLocation(160,140);

        JLabel labelWydaw = new JLabel("Nazwa wydawnictwa:");
        panelBooks.add(labelWydaw);
        labelWydaw.setSize(100, 20);
        labelWydaw.setLocation(40, 180);
        labelWydaw.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleWydaw);
        poleWydaw.setSize(200, 20);
        poleWydaw.setLocation(160, 180);

        JLabel labelRok = new JLabel("Rok wydania :");
        panelBooks.add(labelRok);
        labelRok.setSize(100, 20);
        labelRok.setLocation(40, 220);
        labelRok.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleRok);
        poleRok.setSize(200, 20);
        poleRok.setLocation(160, 220);

        JLabel labelCena = new JLabel("Cena książki:");
        panelBooks.add(labelCena);
        labelCena.setSize(100, 20);
        labelCena.setLocation(40, 260);
        labelCena.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleCena);
        poleCena.setSize(200, 20);
        poleCena.setLocation(160, 260);

        panelBooks.add(scrollPaneBooks);
        scrollPaneBooks.setSize(190, 230);
        scrollPaneBooks.setLocation(400, 20);
        listBooks.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateBooksList();

        panelBooks.add(buttonSaveBook);
        buttonSaveBook.setSize(200, 20);
        buttonSaveBook.setLocation(160, 300);
        buttonSaveBook.addActionListener(akc_zap_ksia);


        panelBooks.add(buttonDellBook);
        buttonDellBook.setSize(200, 20);
        buttonDellBook.setLocation(400, 300);
        buttonDellBook.addActionListener(akc_usun_ksia);

        JLabel labelNewPrice = new JLabel("Nowa cena:");
        panelBooks.add(labelNewPrice);
        labelNewPrice.setSize(100, 20);
        labelNewPrice.setLocation(40, 330);
        labelNewPrice.setHorizontalTextPosition(JLabel.RIGHT);

        panelBooks.add(poleNewPrice);
        poleNewPrice.setSize(200, 20);
        poleNewPrice.setLocation(160, 330);

        panelBooks.add(buttonNewPrice);
        buttonNewPrice.setSize(200, 20);
        buttonNewPrice.setLocation(400, 330);
        buttonNewPrice.addActionListener(chenge_price_book);




        // Zakladka zamówienie
        panelOrder.setLayout(null);

        JLabel labelBooksTitel = new JLabel("Książki:");
        panelOrder.add(labelBooksTitel);
        labelBooksTitel.setSize(100, 20);
        labelBooksTitel.setLocation(40, 20);
        labelBooksTitel.setHorizontalTextPosition(JLabel.RIGHT);


        scrollPaneBookOrder = new JScrollPane(listBookOrder);
        panelOrder.add(scrollPaneBookOrder);
        scrollPaneBookOrder.setSize(350, 100);
        scrollPaneBookOrder.setLocation(40, 50);
        listBookOrder.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        updateBooksListOrder();


        JLabel labelDataOrder = new JLabel("Data:");
        panelOrder.add(labelDataOrder );
        labelDataOrder.setSize(100, 20);
        labelDataOrder.setLocation(40, 160);
        labelDataOrder.setHorizontalTextPosition(JLabel.RIGHT);

        panelOrder.add(poleDataOrder);
        poleDataOrder.setSize(200, 20);
        poleDataOrder.setLocation(160, 160);

        JLabel labelStatusOrder = new JLabel("Status zamówienia:");
        panelOrder.add(labelStatusOrder);
        labelStatusOrder.setSize(100, 20);
        labelStatusOrder.setLocation(40, 190);
        labelStatusOrder.setHorizontalTextPosition(JLabel.RIGHT);

        panelOrder.add(comboStatusOrder);
        comboStatusOrder.setSize(200, 20);
        comboStatusOrder.setLocation(160, 190);
        for (Status status: Status.values()) {
            comboStatusOrder.addItem(status);
        }


        JLabel labelClientTitel = new JLabel("Klient:");
        panelOrder.add(labelClientTitel);
        labelClientTitel.setSize(100, 20);
        labelClientTitel.setLocation(400, 20);
        labelClientTitel.setHorizontalTextPosition(JLabel.RIGHT);

        scrollPaneKlientOrder = new JScrollPane(listKlientOrder);
        panelOrder.add(scrollPaneKlientOrder);
        scrollPaneKlientOrder.setSize(200, 130);
        scrollPaneKlientOrder.setLocation(400, 50);
        listKlientOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateClientListOrder();

        panelOrder.add(buttonNewOrder);
        buttonNewOrder.setSize(200, 20);
        buttonNewOrder.setLocation(400, 190);
        buttonNewOrder.addActionListener(akc_add_order);

        scrollPaneOrder = new JScrollPane(listOrder);
        panelOrder.add(scrollPaneOrder);
        scrollPaneOrder.setSize(560, 110);
        scrollPaneOrder.setLocation(40, 220);
        listOrder.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        updateOrderList();

        JLabel labelNewStatus = new JLabel("Nowy status zamówienia:");
        panelOrder.add(labelNewStatus);
        labelNewStatus.setSize(100, 20);
        labelNewStatus.setLocation(40, 340);
        labelNewStatus.setHorizontalTextPosition(JLabel.RIGHT);

        panelOrder.add(comboNewStatusOrder);
        comboNewStatusOrder.setSize(200, 20);
        comboNewStatusOrder.setLocation(160, 340);
        for (Status status: Status.values()) {
            comboNewStatusOrder.addItem(status);
        }

        panelOrder.add(buttonNewStatusOrder);
        buttonNewStatusOrder.setSize(200, 20);
        buttonNewStatusOrder.setLocation(400, 340);
        buttonNewStatusOrder.addActionListener(akc_change_status);







    }
}