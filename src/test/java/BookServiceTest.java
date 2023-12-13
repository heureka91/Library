import org.example.BookService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;



import java.sql.*;

import static org.mockito.Mockito.*;

public class BookServiceTest {

    @Test
    public void testAddBook() throws SQLException {
        // Arrange
        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        BookService bookService = new BookService(connection);

        // Act
        bookService.addBook("Test Title", "Test Author");

        // Assert
        verify(preparedStatement, times(1)).setString(1, "Test Title");
        verify(preparedStatement, times(1)).setString(2, "Test Author");
        verify(preparedStatement, times(1)).executeUpdate();
    }
    @Test
    public void testBorrowBook() throws SQLException {
        // Arrange
        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement checkStmt = Mockito.mock(PreparedStatement.class);
        PreparedStatement borrowStmt = Mockito.mock(PreparedStatement.class);
        PreparedStatement updateBookStmt = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        when(connection.prepareStatement(anyString())).thenReturn(checkStmt, borrowStmt, updateBookStmt);
        when(checkStmt.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(borrowStmt.executeUpdate()).thenReturn(1);
        when(updateBookStmt.executeUpdate()).thenReturn(1);

        BookService bookService = new BookService(connection);

        // Act
        bookService.borrowBook(1, "Test User");

        // Assert
        verify(checkStmt, times(1)).setInt(1, 1);
        verify(borrowStmt, times(1)).setInt(1, 1);
        verify(borrowStmt, times(1)).setString(2, "Test User");
        verify(borrowStmt, times(1)).executeUpdate();
        verify(updateBookStmt, times(1)).setInt(1, 1);
        verify(updateBookStmt, times(1)).executeUpdate();
    }
    @Test
    public void testReturnBook() throws SQLException {
        // Arrange
        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement returnStmt = Mockito.mock(PreparedStatement.class);
        PreparedStatement updateBookStmt = Mockito.mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(returnStmt, updateBookStmt);
        when(returnStmt.executeUpdate()).thenReturn(1);
        when(updateBookStmt.executeUpdate()).thenReturn(1);

        BookService bookService = new BookService(connection);

        // Act
        bookService.returnBook(1);

        // Assert
        verify(returnStmt, times(1)).setInt(1, 1);
        verify(returnStmt, times(1)).executeUpdate();
        verify(updateBookStmt, times(1)).setInt(1, 1);
        verify(updateBookStmt, times(1)).executeUpdate();
    }

        @Test
        public void testPrintAllBooks() throws SQLException {
            // Arrange
            Connection connection = Mockito.mock(Connection.class);
            Statement statement = Mockito.mock(Statement.class);
            ResultSet resultSet = Mockito.mock(ResultSet.class);

            when(connection.createStatement()).thenReturn(statement);
            when(statement.executeQuery(anyString())).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, false); // Simulate one row in the result set
            when(resultSet.getInt("ID")).thenReturn(1);
            when(resultSet.getString("TITLE")).thenReturn("Test Title");
            when(resultSet.getString("AUTHOR")).thenReturn("Test Author");
            when(resultSet.getBoolean("AVAILABLE")).thenReturn(true);

            BookService bookService = new BookService(connection);

            // Act
            bookService.printAllBooks();

            // Assert
            verify(resultSet, times(1)).getInt("ID");
            verify(resultSet, times(1)).getString("TITLE");
            verify(resultSet, times(1)).getString("AUTHOR");
            verify(resultSet, times(1)).getBoolean("AVAILABLE");
        }

        @Test
        public void testPrintAllBorrowings() throws SQLException {
            // Arrange
            Connection connection = Mockito.mock(Connection.class);
            Statement statement = Mockito.mock(Statement.class);
            ResultSet resultSet = Mockito.mock(ResultSet.class);

            when(connection.createStatement()).thenReturn(statement);
            when(statement.executeQuery(anyString())).thenReturn(resultSet);
            when(resultSet.next()).thenReturn(true, false); // Simulate one row in the result set
            when(resultSet.getInt("BOOK_ID")).thenReturn(1);
            when(resultSet.getString("TITLE")).thenReturn("Test Title");
            when(resultSet.getString("USER_NAME")).thenReturn("Test User");
            when(resultSet.getTimestamp("BORROW_DATE")).thenReturn(new Timestamp(System.currentTimeMillis()));
            when(resultSet.getTimestamp("RETURN_DATE")).thenReturn(new Timestamp(System.currentTimeMillis()));

            BookService bookService = new BookService(connection);

            // Act
            bookService.printAllBorrowings();

            // Assert
            verify(resultSet, times(1)).getInt("BOOK_ID");
            verify(resultSet, times(1)).getString("TITLE");
            verify(resultSet, times(1)).getString("USER_NAME");
            verify(resultSet, times(1)).getTimestamp("BORROW_DATE");
            verify(resultSet, times(1)).getTimestamp("RETURN_DATE");
        }
    }



