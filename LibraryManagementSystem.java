/*
 *  LIBRARY MANAGEMENT SYSTEM  (Swing GUI Edition - Interactive UI)
 *
 *  DEFAULT LOGIN CREDENTIALS
 *  Librarian:
 *      Username: admin
 *      Password: admin123
 *
 *  Member 1:
 *      ID: M001
 *      Password: member123
 *
 *  Member 2:
 *      ID: M002
 *      Password: member456
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.border.Border;
import javax.swing.border.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.*;

enum BookStatus {
    AVAILABLE,
    ISSUED
}
class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) { super(message); }
}

class MemberNotFoundException extends Exception {
    public MemberNotFoundException(String message) { super(message); }
}

class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) { super(message); }
}

class BorrowLimitException extends Exception {
    public BorrowLimitException(String message) { super(message); }
}

class DuplicateBookException extends Exception {
    public DuplicateBookException(String message) { super(message); }
}

class DuplicateMemberException extends Exception {
    public DuplicateMemberException(String message) { super(message); }
}

class InvalidLoginException extends Exception {
    public InvalidLoginException(String message) { super(message); }
}

class ReturnNotAllowedException extends Exception {
    public ReturnNotAllowedException(String message) { super(message); }
}


abstract class User {
    // private fields -> Encapsulation
    private String id;
    private String name;
    private String password;

    public User(String id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    // public getters/setters -> controlled access to private fields
    public String getId() { return id; }
    public String getName() { return name; }

    protected String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean checkPassword(String attempt) {
        return password.equals(attempt);
    }

    // abstract method -> must be implemented by every subclass
    public abstract String getRole();

    // abstract method -> forces each subclass to define its own profile view
    public abstract void displayProfile();
}

class Librarian extends User {
    public Librarian(String id, String name, String password) {
        super(id, name, password);
    }

    // Method Overriding
    @Override
    public String getRole() {
        return "LIBRARIAN";
    }

    @Override
    public void displayProfile() {
        System.out.println("Librarian ID   : " + getId());
        System.out.println("Name           : " + getName());
        System.out.println("Role           : " + getRole());
    }
}

class Member extends User {
    private String department;
    private List<String> issuedBookIds;   // Collections Framework usage
    private final int maxBooks = 3;       // borrowing limit from requirements

    public Member(String id, String name, String password, String department) {
        super(id, name, password);
        this.department = department;
        this.issuedBookIds = new ArrayList<>();
    }

    public String getDepartment() { return department; }
    public List<String> getIssuedBookIds() { return issuedBookIds; }
    public int getMaxBooks() { return maxBooks; }

    public boolean hasReachedLimit() {
        return issuedBookIds.size() >= maxBooks;
    }

    public void addIssuedBook(String bookId) { issuedBookIds.add(bookId); }
    public void removeIssuedBook(String bookId) { issuedBookIds.remove(bookId); }

    // Method Overriding
    @Override
    public String getRole() {
        return "MEMBER";
    }

    @Override
    public void displayProfile() {
        System.out.println("Member ID      : " + getId());
        System.out.println("Name           : " + getName());
        System.out.println("Department     : " + department);
        System.out.println("Books Issued   : " + issuedBookIds.size());
        System.out.println("Max Allowed    : " + maxBooks);
    }
}

/* ================================================================
 *  Book class  -  Encapsulation + Constructors + Enum usage
 * ================================================================ */
class Book {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private BookStatus status;

    public Book(String bookId, String title, String author, String category, BookStatus status) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public BookStatus getStatus() { return status; }
    public void setStatus(BookStatus status) { this.status = status; }

    // String handling: case-insensitive matching helper
    public boolean matches(String keyword) {
        String k = keyword.toLowerCase();
        return bookId.toLowerCase().contains(k)
                || title.toLowerCase().contains(k)
                || author.toLowerCase().contains(k)
                || category.toLowerCase().contains(k);
    }

    // Used when saving to file
    public String toFileString() {
        return bookId + "|" + title + "|" + author + "|" + category + "|" + status;
    }
}

class IssueRecord {
    private String bookId;
    private String memberId;
    private LocalDate issueDate;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    public IssueRecord(String bookId, String memberId, LocalDate issueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
    }

    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }

    public String toFileString() {
        return bookId + "|" + memberId + "|" + issueDate.format(FMT);
    }
}


interface LibraryOperations {
    void addBook(String bookId, String title, String author, String category) throws DuplicateBookException;
    void removeBook(String bookId) throws BookNotFoundException, BookNotAvailableException;
    List<Book> searchBook(String keyword);
    void issueBook(String bookId, Member member) throws BookNotFoundException, BookNotAvailableException, BorrowLimitException;
    void returnBook(String bookId, String memberId) throws BookNotFoundException, MemberNotFoundException, ReturnNotAllowedException;
}


class Library implements LibraryOperations {

    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private ArrayList<IssueRecord> issueRecords;

    private static final String BOOKS_FILE = "books.txt";
    private static final String MEMBERS_FILE = "members.txt";
    private static final String ISSUES_FILE = "issues.txt";

    public Library() {
        books = new ArrayList<>();
        members = new ArrayList<>();
        issueRecords = new ArrayList<>();
    }

    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<Member> getMembers() { return members; }
    public ArrayList<IssueRecord> getIssueRecords() { return issueRecords; }

    /* ---------- helper lookups ---------- */

    public Book findBookById(String bookId) {
        for (Book b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) return b;
        }
        return null;
    }

    public Member findMemberById(String memberId) {
        for (Member m : members) {
            if (m.getId().equalsIgnoreCase(memberId)) return m;
        }
        return null;
    }

    private boolean bookIdExists(String bookId) {
        return findBookById(bookId) != null;
    }

    private boolean memberIdExists(String memberId) {
        return findMemberById(memberId) != null;
    }


    @Override
    public synchronized void addBook(String bookId, String title, String author, String category)
            throws DuplicateBookException {
        if (bookIdExists(bookId)) {
            throw new DuplicateBookException("Book ID '" + bookId + "' already exists.");
        }
        Book book = new Book(bookId, title, author, category, BookStatus.AVAILABLE);
        books.add(book);
        saveBooks();
    }

    @Override
    public synchronized void removeBook(String bookId) throws BookNotFoundException, BookNotAvailableException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book ID '" + bookId + "' not found.");
        }
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new BookNotAvailableException("Book is currently issued and cannot be removed.");
        }
        books.remove(book);
        saveBooks();
    }

    @Override
    public List<Book> searchBook(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : books) {
            if (b.matches(keyword)) results.add(b);
        }
        return results;
    }

    @Override
    public synchronized void issueBook(String bookId, Member member)
            throws BookNotFoundException, BookNotAvailableException, BorrowLimitException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book ID '" + bookId + "' not found.");
        }
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' is currently unavailable.");
        }
        if (member.hasReachedLimit()) {
            throw new BorrowLimitException("Member has reached the maximum borrowing limit of "
                    + member.getMaxBooks() + " books.");
        }

        book.setStatus(BookStatus.ISSUED);
        IssueRecord record = new IssueRecord(bookId, member.getId(), LocalDate.now());
        issueRecords.add(record);
        member.addIssuedBook(bookId);

        saveBooks();
        saveMembers();
        saveIssues();

        // Multithreading: fire a small background audit/notification task
        runAuditNotification("Book ISSUED -> " + book.getTitle() + " to member " + member.getId());
    }

    @Override
    public synchronized void returnBook(String bookId, String memberId)
            throws BookNotFoundException, MemberNotFoundException, ReturnNotAllowedException {
        Book book = findBookById(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book ID '" + bookId + "' not found.");
        }
        Member member = findMemberById(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Member ID '" + memberId + "' not found.");
        }
        if (book.getStatus() != BookStatus.ISSUED) {
            throw new ReturnNotAllowedException("This book is not currently issued.");
        }
        if (!member.getIssuedBookIds().contains(bookId)) {
            throw new ReturnNotAllowedException("This book was not issued to member " + memberId + ".");
        }

        book.setStatus(BookStatus.AVAILABLE);
        member.removeIssuedBook(bookId);

        // remove the matching issue record
        IssueRecord toRemove = null;
        for (IssueRecord r : issueRecords) {
            if (r.getBookId().equalsIgnoreCase(bookId) && r.getMemberId().equalsIgnoreCase(memberId)) {
                toRemove = r;
                break;
            }
        }
        if (toRemove != null) issueRecords.remove(toRemove);

        saveBooks();
        saveMembers();
        saveIssues();

        // Multithreading: background audit/notification task
        runAuditNotification("Book RETURNED -> " + book.getTitle() + " from member " + memberId);
    }

    /* ---------- registering members (librarian-only action) ---------- */

    public synchronized void registerMember(String id, String name, String password, String department)
            throws DuplicateMemberException {
        if (memberIdExists(id)) {
            throw new DuplicateMemberException("Member ID '" + id + "' already exists.");
        }
        members.add(new Member(id, name, password, department));
        saveMembers();
    }

    private void runAuditNotification(String message) {
        Runnable auditTask = () -> {
            try {
                Thread.sleep(150); // simulate small background delay
            } catch (InterruptedException ignored) {
            }
            synchronized (Library.class) {
                System.out.println("[AUDIT-THREAD] " + message + " | logged at " + LocalDate.now());
            }
        };
        Thread t = new Thread(auditTask);
        t.start();
        try {
            t.join(); // wait so console output stays in order for the demo
        } catch (InterruptedException ignored) {
        }
    }

    public synchronized void saveBooks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            for (Book b : books) {
                bw.write(b.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Could not save books data: " + e.getMessage());
        }
    }

    public synchronized void saveMembers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MEMBERS_FILE))) {
            for (Member m : members) {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getId()).append("|")
                  .append(m.getName()).append("|")
                  .append(m.getPassword()).append("|")
                  .append(m.getDepartment()).append("|");
                // comma separated issued book ids
                List<String> issued = m.getIssuedBookIds();
                for (int i = 0; i < issued.size(); i++) {
                    sb.append(issued.get(i));
                    if (i < issued.size() - 1) sb.append(",");
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Could not save members data: " + e.getMessage());
        }
    }

    public synchronized void saveIssues() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ISSUES_FILE))) {
            for (IssueRecord r : issueRecords) {
                bw.write(r.toFileString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Could not save issue records: " + e.getMessage());
        }
    }

    /** Loads persisted data. Returns true if all three files existed and were loaded. */
    public boolean loadAllData() {
        boolean loadedBooks = loadBooks();
        boolean loadedMembers = loadMembers();
        boolean loadedIssues = loadIssues();
        return loadedBooks && loadedMembers && loadedIssues;
    }

    private boolean loadBooks() {
        File f = new File(BOOKS_FILE);
        if (!f.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 5) continue;
                BookStatus status = parts[4].trim().equals("ISSUED") ? BookStatus.ISSUED : BookStatus.AVAILABLE;
                books.add(new Book(parts[0], parts[1], parts[2], parts[3], status));
            }
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load books data: " + e.getMessage());
            return false;
        }
    }

    private boolean loadMembers() {
        File f = new File(MEMBERS_FILE);
        if (!f.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 5) continue;
                Member m = new Member(parts[0], parts[1], parts[2], parts[3]);
                if (!parts[4].trim().isEmpty()) {
                    String[] issued = parts[4].split(",");
                    for (String bid : issued) {
                        if (!bid.trim().isEmpty()) m.addIssuedBook(bid.trim());
                    }
                }
                members.add(m);
            }
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load members data: " + e.getMessage());
            return false;
        }
    }

    private boolean loadIssues() {
        File f = new File(ISSUES_FILE);
        if (!f.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) continue;
                LocalDate date = LocalDate.parse(parts[2]);
                issueRecords.add(new IssueRecord(parts[0], parts[1], date));
            }
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] Could not load issue records: " + e.getMessage());
            return false;
        }
    }

    /* Loads sample data used the very first time the program runs */
    public void loadSampleData() {
        books.add(new Book("B101", "Java Programming", "Herbert Schildt", "CS", BookStatus.AVAILABLE));
        books.add(new Book("B102", "Data Structures", "Mark Allen Weiss", "CS", BookStatus.AVAILABLE));
        books.add(new Book("B103", "Operating System Concepts", "Silberschatz", "CS", BookStatus.AVAILABLE));
        books.add(new Book("B104", "Computer Networks", "Andrew Tanenbaum", "CS", BookStatus.AVAILABLE));
        books.add(new Book("B105", "Database System Concepts", "Korth", "CS", BookStatus.AVAILABLE));

        members.add(new Member("M001", "Aarav Sharma", "member123", "CSE"));
        members.add(new Member("M002", "Priya Verma", "member456", "IT"));

        saveBooks();
        saveMembers();
        saveIssues();
    }
}

class ZebraCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(241, 245, 249));
            c.setForeground(new Color(30, 41, 59));
        }
        setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return c;
    }
}

class StatusCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                     boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setOpaque(true);
        String text = (value == null) ? "" : value.toString();
        if (!isSelected) {
            if ("AVAILABLE".equals(text)) {
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(21, 128, 61));
            } else {
                label.setBackground(new Color(254, 226, 226));
                label.setForeground(new Color(185, 28, 28));
            }
        } else {
            label.setBackground(table.getSelectionBackground());
            label.setForeground(table.getSelectionForeground());
        }
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }
}

public class LibraryManagementSystem {

    private static final Library library = new Library();

    private static final String LIB_USERNAME = "admin";
    private static final String LIB_PASSWORD = "admin123";
    private static final Librarian librarian = new Librarian(LIB_USERNAME, "Administrator", LIB_PASSWORD);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static JFrame frame;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;

    // ---- Librarian dashboard widgets ----
    private static DefaultTableModel libBookModel;
    private static JTable libBookTable;
    private static DefaultTableModel libMemberModel;
    private static JTable libMemberTable;
    private static DefaultTableModel libIssueModel;
    private static JTable libIssueTable;
    private static JLabel librarianWelcomeLabel;

    // ---- Member dashboard widgets ----
    private static DefaultTableModel memBookModel;
    private static JTable memBookTable;
    private static DefaultTableModel memMyIssuedModel;
    private static JTable memMyIssuedTable;
    private static JLabel memberWelcomeLabel;
    private static JLabel profileIdLabel, profileNameLabel, profileDeptLabel, profileIssuedLabel, profileMaxLabel;
    private static Member currentMember;


    private static final Color COLOR_BG          = new Color(244, 247, 252);
    private static final Color COLOR_CARD        = Color.WHITE;
    private static final Color COLOR_PRIMARY     = new Color(37, 99, 235);   // blue - librarian / primary actions
    private static final Color COLOR_PRIMARY_DARK= new Color(23, 61, 148);
    private static final Color COLOR_ACCENT      = new Color(13, 148, 136);  // teal - member actions
    private static final Color COLOR_ACCENT_DARK = new Color(8, 96, 88);
    private static final Color COLOR_DANGER      = new Color(220, 38, 38);   // red - logout / remove / exit
    private static final Color COLOR_TEXT        = new Color(30, 41, 59);
    private static final Color COLOR_MUTED       = new Color(100, 116, 139);
    private static final Color COLOR_BORDER      = new Color(203, 213, 225);

    private static final Font FONT_TITLE        = new Font("Segoe UI", Font.BOLD, 34);
    private static final Font FONT_SUBTITLE     = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Font FONT_SECTION      = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font FONT_LABEL        = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font FONT_LABEL_BOLD   = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BUTTON       = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TABLE        = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_TAB          = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_ERROR        = new Font("Segoe UI", Font.BOLD, 13);

    public static void main(String[] args) {
        // Load persisted data, or fall back to sample data on first run
        boolean loaded = library.loadAllData();
        if (!loaded) {
            library.loadSampleData();
        }
        SwingUtilities.invokeLater(LibraryManagementSystem::createAndShowGUI);
    }


    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Bump up the default fonts used by stock dialogs (JOptionPane etc.)
        // so pop-ups match the bigger, friendlier feel of the rest of the app.
        UIManager.put("OptionPane.messageFont", FONT_LABEL);
        UIManager.put("OptionPane.buttonFont", FONT_BUTTON);
        UIManager.put("Button.font", FONT_BUTTON);
        UIManager.put("Label.font", FONT_LABEL);
        UIManager.put("TextField.font", FONT_LABEL);
        UIManager.put("PasswordField.font", FONT_LABEL);
        UIManager.put("TabbedPane.font", FONT_TAB);

        frame = new JFrame("Library Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1080, 700);
        frame.setMinimumSize(new Dimension(900, 560));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(COLOR_BG);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(COLOR_BG);

        cardPanel.add(buildWelcomePanel(), "welcome");
        cardPanel.add(buildLibrarianLoginPanel(), "librarianLogin");
        cardPanel.add(buildMemberLoginPanel(), "memberLogin");
        cardPanel.add(buildLibrarianDashboard(), "librarianDashboard");
        cardPanel.add(buildMemberDashboard(), "memberDashboard");

        frame.add(cardPanel);
        frame.setVisible(true);
        showCard("welcome");
    }

    private static void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    /* ============================ STYLE HELPERS ============================ */

    /** Rounded, hover/press reactive button used for every action in the app. */
    private static JButton styledButton(String text, Color base, Color fg) {
        final Color hover = base.brighter();
        final Color press = base.darker();
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(FONT_BUTTON);
        b.setForeground(fg);
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(base); }
            @Override public void mousePressed(MouseEvent e) { b.setBackground(press); }
            @Override public void mouseReleased(MouseEvent e){ b.setBackground(hover); }
        });
        return b;
    }

    private static void styleTextField(JTextField field) {
        field.setFont(FONT_LABEL);
        field.setForeground(COLOR_TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(COLOR_PRIMARY);
        final Border normal = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10));
        final Border focused = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARY, 2, true),
                BorderFactory.createEmptyBorder(7, 9, 7, 9));
        field.setBorder(normal);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.setBorder(focused); }
            @Override public void focusLost(FocusEvent e)   { field.setBorder(normal); }
        });
    }

    /** Applies the shared look (fonts, stripes, header colors) to a table. */
    private static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new ZebraCellRenderer());

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setBackground(COLOR_PRIMARY_DARK);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);
    }

    private static void applyStatusColumnRenderer(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new StatusCellRenderer());
    }

    /** A white rounded "card" container used to hold forms. */
    private static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(COLOR_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                BorderFactory.createEmptyBorder(26, 32, 26, 32)));
        return p;
    }

    private static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION);
        l.setForeground(COLOR_TEXT);
        return l;
    }

    private static JPanel wrapScroll(JComponent scroll) {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(COLOR_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }


    private static JPanel buildWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(COLOR_BG);

        JLabel title = new JLabel("LIBRARY MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_PRIMARY_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Fast, friendly book issuing & tracking", SwingConstants.CENTER);
        subtitle.setFont(FONT_SUBTITLE);
        subtitle.setForeground(COLOR_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        titleBox.add(title);
        titleBox.add(subtitle);
        panel.add(titleBox, BorderLayout.NORTH);

        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new BoxLayout(buttonBox, BoxLayout.Y_AXIS));
        buttonBox.setBackground(COLOR_BG);

        JButton librarianBtn = bigButton("Librarian Login", COLOR_PRIMARY);
        JButton memberBtn = bigButton("Member Login", COLOR_ACCENT);
        JButton exitBtn = bigButton("Exit", COLOR_DANGER);

        librarianBtn.addActionListener(e -> showCard("librarianLogin"));
        memberBtn.addActionListener(e -> showCard("memberLogin"));
        exitBtn.addActionListener(e -> System.exit(0));

        buttonBox.add(Box.createVerticalGlue());
        buttonBox.add(librarianBtn);
        buttonBox.add(Box.createRigidArea(new Dimension(0, 16)));
        buttonBox.add(memberBtn);
        buttonBox.add(Box.createRigidArea(new Dimension(0, 16)));
        buttonBox.add(exitBtn);
        buttonBox.add(Box.createVerticalGlue());

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(COLOR_BG);
        center.add(buttonBox);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    private static JButton bigButton(String text, Color base) {
        JButton b = styledButton(text, base, Color.WHITE);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(300, 52));
        b.setPreferredSize(new Dimension(300, 52));
        return b;
    }


    private static JPanel buildLibrarianLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        JPanel form = card();
        form.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = sectionLabel("Librarian Login");
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; form.add(heading, gc);
        gc.gridwidth = 1;

        JTextField userField = new JTextField(16);
        JPasswordField passField = new JPasswordField(16);
        styleTextField(userField);
        styleTextField(passField);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(COLOR_DANGER);
        errorLabel.setFont(FONT_ERROR);

        JLabel userLbl = new JLabel("Username:"); userLbl.setFont(FONT_LABEL_BOLD);
        JLabel passLbl = new JLabel("Password:"); passLbl.setFont(FONT_LABEL_BOLD);

        gc.gridx = 0; gc.gridy = 1; form.add(userLbl, gc);
        gc.gridx = 1; gc.gridy = 1; form.add(userField, gc);
        gc.gridx = 0; gc.gridy = 2; form.add(passLbl, gc);
        gc.gridx = 1; gc.gridy = 2; form.add(passField, gc);

        JButton loginBtn = styledButton("Login", COLOR_PRIMARY, Color.WHITE);
        JButton backBtn = styledButton("Back", COLOR_MUTED, Color.WHITE);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(COLOR_CARD);
        btnRow.add(loginBtn);
        btnRow.add(backBtn);

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; form.add(btnRow, gc);
        gc.gridy = 4; form.add(errorLabel, gc);

        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            try {
                if (!username.equals(LIB_USERNAME) || !password.equals(LIB_PASSWORD)) {
                    throw new InvalidLoginException("Invalid librarian username or password.");
                }
                errorLabel.setText(" ");
                userField.setText("");
                passField.setText("");
                librarianWelcomeLabel.setText("Welcome, " + librarian.getName() + " (LIBRARIAN)");
                refreshLibrarianTables();
                showCard("librarianDashboard");
            } catch (InvalidLoginException ex) {
                errorLabel.setText(ex.getMessage());
                passField.setText("");
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
        backBtn.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            errorLabel.setText(" ");
            showCard("welcome");
        });

        panel.add(form);
        return panel;
    }

    private static JPanel buildMemberLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        JPanel form = card();
        form.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = sectionLabel("Member Login");
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; form.add(heading, gc);
        gc.gridwidth = 1;

        JTextField idField = new JTextField(16);
        JPasswordField passField = new JPasswordField(16);
        styleTextField(idField);
        styleTextField(passField);
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(COLOR_DANGER);
        errorLabel.setFont(FONT_ERROR);

        JLabel idLbl = new JLabel("Member ID:"); idLbl.setFont(FONT_LABEL_BOLD);
        JLabel passLbl = new JLabel("Password:"); passLbl.setFont(FONT_LABEL_BOLD);

        gc.gridx = 0; gc.gridy = 1; form.add(idLbl, gc);
        gc.gridx = 1; gc.gridy = 1; form.add(idField, gc);
        gc.gridx = 0; gc.gridy = 2; form.add(passLbl, gc);
        gc.gridx = 1; gc.gridy = 2; form.add(passField, gc);

        JButton loginBtn = styledButton("Login", COLOR_ACCENT, Color.WHITE);
        JButton backBtn = styledButton("Back", COLOR_MUTED, Color.WHITE);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setBackground(COLOR_CARD);
        btnRow.add(loginBtn);
        btnRow.add(backBtn);

        gc.gridx = 0; gc.gridy = 3; gc.gridwidth = 2; form.add(btnRow, gc);
        gc.gridy = 4; form.add(errorLabel, gc);

        Runnable doLogin = () -> {
            String id = idField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            try {
                Member member = library.findMemberById(id);
                if (member == null) {
                    throw new MemberNotFoundException("No member registered with ID '" + id + "'.");
                }
                if (!member.checkPassword(password)) {
                    throw new InvalidLoginException("Incorrect password for member '" + id + "'.");
                }
                errorLabel.setText(" ");
                idField.setText("");
                passField.setText("");
                currentMember = member;

                // Polymorphism demo: parent-type reference pointing to a Member object
                User loggedInUser = member;
                System.out.println("[INFO] Logged in as role: " + loggedInUser.getRole());

                memberWelcomeLabel.setText("Welcome, " + member.getName() + " (" + member.getId() + ")");
                refreshMemberTables();
                showCard("memberDashboard");
            } catch (MemberNotFoundException | InvalidLoginException ex) {
                errorLabel.setText(ex.getMessage());
                passField.setText("");
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
        backBtn.addActionListener(e -> {
            idField.setText("");
            passField.setText("");
            errorLabel.setText(" ");
            showCard("welcome");
        });

        panel.add(form);
        return panel;
    }


    private static JPanel buildLibrarianDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(COLOR_PRIMARY_DARK);
        librarianWelcomeLabel = new JLabel("Welcome");
        librarianWelcomeLabel.setFont(FONT_SECTION);
        librarianWelcomeLabel.setForeground(Color.WHITE);
        librarianWelcomeLabel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 10));
        JButton logoutBtn = styledButton("Logout", COLOR_DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            System.out.println("[INFO] Librarian logging out...");
            showCard("welcome");
        });
        JPanel logoutHolder = new JPanel();
        logoutHolder.setBackground(COLOR_PRIMARY_DARK);
        logoutHolder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        logoutHolder.add(logoutBtn);
        top.add(librarianWelcomeLabel, BorderLayout.WEST);
        top.add(logoutHolder, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_TAB);
        tabs.setBackground(COLOR_BG);
        tabs.setForeground(COLOR_TEXT);
        tabs.addTab("  All Books  ", buildLibBooksTab());
        tabs.addTab("  Members  ", buildLibMembersTab());
        tabs.addTab("  Issued Books  ", buildLibIssuedTab());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel buildLibBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        libBookModel = readOnlyModel(new String[]{"ID", "Title", "Author", "Category", "Status"});
        libBookTable = new JTable(libBookModel);
        styleTable(libBookTable);
        applyStatusColumnRenderer(libBookTable, 4);
        JScrollPane scroll = new JScrollPane(libBookTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(wrapScroll(scroll), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.setBackground(COLOR_BG);
        JButton addBtn = styledButton("Add Book", COLOR_PRIMARY, Color.WHITE);
        JButton removeBtn = styledButton("Remove Book", COLOR_DANGER, Color.WHITE);
        JTextField searchField = new JTextField(14);
        styleTextField(searchField);
        JButton searchBtn = styledButton("Search", COLOR_ACCENT, Color.WHITE);
        JButton showAllBtn = styledButton("Show All", COLOR_MUTED, Color.WHITE);

        addBtn.addActionListener(e -> {
            String[] values = promptForFields("Add New Book", "Book ID", "Title", "Author", "Category");
            if (values == null) return;
            String id = values[0], title = values[1], author = values[2], category = values[3];
            if (id.isEmpty() || title.isEmpty() || author.isEmpty() || category.isEmpty()) {
                showError("None of the fields can be empty.");
                return;
            }
            try {
                library.addBook(id, title, author, category);
                showSuccess("Book added successfully.");
                refreshLibrarianTables();
            } catch (DuplicateBookException ex) {
                showError(ex.getMessage());
            }
        });

        removeBtn.addActionListener(e -> {
            String id = getSelectedOrPrompt(libBookTable, 0, "Enter Book ID to remove:");
            if (id == null) return;
            try {
                library.removeBook(id);
                showSuccess("Book removed successfully.");
                refreshLibrarianTables();
            } catch (BookNotFoundException | BookNotAvailableException ex) {
                showError(ex.getMessage());
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            List<Book> results = library.searchBook(keyword);
            refreshBooksModel(libBookModel, results);
            if (results.isEmpty()) showInfo("No matching books found.");
        });

        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            refreshBooksModel(libBookModel, library.getBooks());
        });

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(FONT_LABEL_BOLD);

        controls.add(addBtn);
        controls.add(removeBtn);
        controls.add(searchLbl);
        controls.add(searchField);
        controls.add(searchBtn);
        controls.add(showAllBtn);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel buildLibMembersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        libMemberModel = readOnlyModel(new String[]{"ID", "Name", "Department", "Books Issued"});
        libMemberTable = new JTable(libMemberModel);
        styleTable(libMemberTable);
        JScrollPane scroll = new JScrollPane(libMemberTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(wrapScroll(scroll), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.setBackground(COLOR_BG);
        JButton registerBtn = styledButton("Register New Member", COLOR_PRIMARY, Color.WHITE);
        registerBtn.addActionListener(e -> {
            String[] values = promptForFields("Register New Member", "Member ID", "Name", "Password", "Course/Department");
            if (values == null) return;
            String id = values[0], name = values[1], password = values[2], department = values[3];
            if (id.isEmpty() || name.isEmpty() || password.isEmpty()) {
                showError("Member ID, name and password cannot be empty.");
                return;
            }
            try {
                library.registerMember(id, name, password, department);
                showSuccess("Member registered successfully.");
                refreshLibrarianTables();
            } catch (DuplicateMemberException ex) {
                showError(ex.getMessage());
            }
        });
        controls.add(registerBtn);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel buildLibIssuedTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        libIssueModel = readOnlyModel(new String[]{"Book ID", "Title", "Member ID", "Member Name", "Issue Date"});
        libIssueTable = new JTable(libIssueModel);
        styleTable(libIssueTable);
        JScrollPane scroll = new JScrollPane(libIssueTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(wrapScroll(scroll), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.setBackground(COLOR_BG);
        JButton checkInBtn = styledButton("Check In (Return) Book", COLOR_ACCENT, Color.WHITE);
        checkInBtn.addActionListener(e -> {
            String bookId, memberId;
            int row = libIssueTable.getSelectedRow();
            if (row >= 0) {
                bookId = libIssueModel.getValueAt(row, 0).toString();
                memberId = libIssueModel.getValueAt(row, 2).toString();
            } else {
                String[] values = promptForFields("Check In Book", "Member ID", "Book ID");
                if (values == null) return;
                memberId = values[0];
                bookId = values[1];
            }
            try {
                library.returnBook(bookId, memberId);
                showSuccess("Book checked in (returned) successfully.");
                refreshLibrarianTables();
            } catch (BookNotFoundException | MemberNotFoundException | ReturnNotAllowedException ex) {
                showError(ex.getMessage());
            }
        });
        controls.add(checkInBtn);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    /* ============================ MEMBER DASHBOARD ============================ */

    private static JPanel buildMemberDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(COLOR_ACCENT_DARK);
        memberWelcomeLabel = new JLabel("Welcome");
        memberWelcomeLabel.setFont(FONT_SECTION);
        memberWelcomeLabel.setForeground(Color.WHITE);
        memberWelcomeLabel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 10));
        JButton logoutBtn = styledButton("Logout", COLOR_DANGER, Color.WHITE);
        logoutBtn.addActionListener(e -> {
            System.out.println("[INFO] Member logging out...");
            currentMember = null;
            showCard("welcome");
        });
        JPanel logoutHolder = new JPanel();
        logoutHolder.setBackground(COLOR_ACCENT_DARK);
        logoutHolder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        logoutHolder.add(logoutBtn);
        top.add(memberWelcomeLabel, BorderLayout.WEST);
        top.add(logoutHolder, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_TAB);
        tabs.setBackground(COLOR_BG);
        tabs.setForeground(COLOR_TEXT);
        tabs.addTab("  All Books  ", buildMemAllBooksTab());
        tabs.addTab("  My Issued Books  ", buildMemIssuedTab());
        tabs.addTab("  My Profile  ", buildMemProfileTab());
        panel.add(tabs, BorderLayout.CENTER);

        return panel;
    }

    private static JPanel buildMemAllBooksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        memBookModel = readOnlyModel(new String[]{"ID", "Title", "Author", "Category", "Status"});
        memBookTable = new JTable(memBookModel);
        styleTable(memBookTable);
        applyStatusColumnRenderer(memBookTable, 4);
        JScrollPane scroll = new JScrollPane(memBookTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(wrapScroll(scroll), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controls.setBackground(COLOR_BG);
        JButton issueBtn = styledButton("Issue Selected Book", COLOR_ACCENT, Color.WHITE);
        JTextField searchField = new JTextField(14);
        styleTextField(searchField);
        JButton searchBtn = styledButton("Search", COLOR_PRIMARY, Color.WHITE);
        JButton showAllBtn = styledButton("Show All", COLOR_MUTED, Color.WHITE);

        issueBtn.addActionListener(e -> {
            if (currentMember == null) return;
            String bookId = getSelectedOrPrompt(memBookTable, 0, "Enter Book ID to issue:");
            if (bookId == null) return;
            try {
                library.issueBook(bookId, currentMember);
                showSuccess("Book issued successfully. Enjoy your reading!");
                refreshMemberTables();
            } catch (BookNotFoundException | BookNotAvailableException | BorrowLimitException ex) {
                showError(ex.getMessage());
            }
        });

        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            List<Book> results = library.searchBook(keyword);
            refreshBooksModel(memBookModel, results);
            if (results.isEmpty()) showInfo("No matching books found.");
        });

        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            refreshBooksModel(memBookModel, library.getBooks());
        });

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(FONT_LABEL_BOLD);

        controls.add(issueBtn);
        controls.add(searchLbl);
        controls.add(searchField);
        controls.add(searchBtn);
        controls.add(showAllBtn);
        panel.add(controls, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel buildMemIssuedTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG);
        memMyIssuedModel = readOnlyModel(new String[]{"Book ID", "Title", "Author", "Issue Date"});
        memMyIssuedTable = new JTable(memMyIssuedModel);
        styleTable(memMyIssuedTable);
        JScrollPane scroll = new JScrollPane(memMyIssuedTable);
        scroll.getViewport().setBackground(Color.WHITE);
        panel.add(wrapScroll(scroll), BorderLayout.CENTER);
        return panel;
    }

    private static JPanel buildMemProfileTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        JPanel form = card();
        form.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 14, 10, 14);
        gc.anchor = GridBagConstraints.WEST;

        JLabel heading = sectionLabel("My Profile");
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2; form.add(heading, gc);
        gc.gridwidth = 1;

        profileIdLabel = new JLabel("-");
        profileNameLabel = new JLabel("-");
        profileDeptLabel = new JLabel("-");
        profileIssuedLabel = new JLabel("-");
        profileMaxLabel = new JLabel("-");

        String[] labels = {"Member ID:", "Name:", "Department:", "Books Issued:", "Max Allowed:"};
        JLabel[] values = {profileIdLabel, profileNameLabel, profileDeptLabel, profileIssuedLabel, profileMaxLabel};
        for (int i = 0; i < labels.length; i++) {
            JLabel keyLbl = new JLabel(labels[i]);
            keyLbl.setFont(FONT_LABEL_BOLD);
            keyLbl.setForeground(COLOR_MUTED);
            values[i].setFont(FONT_LABEL);
            values[i].setForeground(COLOR_TEXT);
            gc.gridx = 0; gc.gridy = i + 1; form.add(keyLbl, gc);
            gc.gridx = 1; gc.gridy = i + 1; form.add(values[i], gc);
        }

        panel.add(form);
        return panel;
    }


    private static void refreshLibrarianTables() {
        refreshBooksModel(libBookModel, library.getBooks());
        refreshMembersModel(libMemberModel, library.getMembers());
        refreshIssuesModel(libIssueModel, library.getIssueRecords());
    }

    private static void refreshMemberTables() {
        if (currentMember == null) return;
        refreshBooksModel(memBookModel, library.getBooks());
        refreshMyIssuedModel(memMyIssuedModel, currentMember);
        profileIdLabel.setText(currentMember.getId());
        profileNameLabel.setText(currentMember.getName());
        profileDeptLabel.setText(currentMember.getDepartment());
        profileIssuedLabel.setText(String.valueOf(currentMember.getIssuedBookIds().size()));
        profileMaxLabel.setText(String.valueOf(currentMember.getMaxBooks()));
    }

    private static void refreshBooksModel(DefaultTableModel model, List<Book> books) {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getStatus()});
        }
    }

    private static void refreshMembersModel(DefaultTableModel model, List<Member> members) {
        model.setRowCount(0);
        for (Member m : members) {
            model.addRow(new Object[]{m.getId(), m.getName(), m.getDepartment(), m.getIssuedBookIds().size()});
        }
    }

    private static void refreshIssuesModel(DefaultTableModel model, List<IssueRecord> records) {
        model.setRowCount(0);
        for (IssueRecord r : records) {
            Book b = library.findBookById(r.getBookId());
            Member m = library.findMemberById(r.getMemberId());
            String title = (b != null) ? b.getTitle() : "(unknown)";
            String memberName = (m != null) ? m.getName() : "(unknown)";
            model.addRow(new Object[]{r.getBookId(), title, r.getMemberId(), memberName, r.getIssueDate().format(DATE_FMT)});
        }
    }

    private static void refreshMyIssuedModel(DefaultTableModel model, Member member) {
        model.setRowCount(0);
        for (String bookId : member.getIssuedBookIds()) {
            Book b = library.findBookById(bookId);
            IssueRecord record = null;
            for (IssueRecord r : library.getIssueRecords()) {
                if (r.getBookId().equalsIgnoreCase(bookId) && r.getMemberId().equalsIgnoreCase(member.getId())) {
                    record = r;
                    break;
                }
            }
            String title = (b != null) ? b.getTitle() : "(unknown)";
            String author = (b != null) ? b.getAuthor() : "(unknown)";
            String date = (record != null) ? record.getIssueDate().format(DATE_FMT) : "-";
            model.addRow(new Object[]{bookId, title, author, date});
        }
    }


    private static DefaultTableModel readOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private static String[] promptForFields(String title, String... fieldLabels) {
        JPanel panel = new JPanel(new GridLayout(fieldLabels.length, 2, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        JTextField[] fields = new JTextField[fieldLabels.length];
        for (int i = 0; i < fieldLabels.length; i++) {
            JLabel lbl = new JLabel(fieldLabels[i] + ":");
            lbl.setFont(FONT_LABEL_BOLD);
            panel.add(lbl);
            fields[i] = new JTextField();
            styleTextField(fields[i]);
            panel.add(fields[i]);
        }
        int result = JOptionPane.showConfirmDialog(frame, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        String[] values = new String[fieldLabels.length];
        for (int i = 0; i < fieldLabels.length; i++) {
            values[i] = fields[i].getText().trim();
        }
        return values;
    }

    private static String getSelectedOrPrompt(JTable table, int column, String promptMessage) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Object val = table.getModel().getValueAt(row, column);
            return val != null ? val.toString() : null;
        }
        String input = JOptionPane.showInputDialog(frame, promptMessage);
        if (input == null) return null;
        return input.trim();
    }

    private static void showSuccess(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
