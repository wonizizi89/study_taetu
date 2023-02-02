package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jdbc.dao.AccountDAO;
import jdbc.vo.AccountVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JDBCTest {

  @Test
  @DisplayName("jdbc db  연결 실습")
  void jdbcTest() throws SQLException {
    // given

    // docker run -p 5432:5432 -e POSTGRES_PASSWORD=pass -e POSTGRES_USER=hyewon -e POSTGRES_DB=messenger --name postgres_boot -d postgres

    // docker exec -i -t postgres_boot bash 도커를 실행하겠다.  포스트그레이스_부트라는 이름으로 컨테이너를 만들었는데 , 이 네임으로 컨테이너에 접속해서 배시명령을 날리수 있도록 배시를 띄우겠다.
    //  를 날리면 루트 계정으로 접속해서 실제 우리가 컨테이너에 접속한 것을 알 수 있다.
    // su - postgres // 이미 계정이 포인트 어쩌고 로 떠있어 계정을 포스트 그래스로 바꾸고

    //psql만 하면 유저네임이 없어 존재하지 않는다고 에러남
    // psql --username hyewon --dbname messenger
    // \list (데이터 베이스 조회)
    // \dt (테이블 조회)

    // IntelliJ Database 에서도 조회
    String url = "jdbc:postgresql://localhost:5432/messenger";
    String username = "hyewon";
    String password = "pass";

    //when
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      String createSql = "CREATE TABLE ACCOUNT (id SERIAL PRIMARY KEY, username varchar(255), password varchar(255))";
      try (PreparedStatement statement = connection.prepareStatement(createSql)) {
        statement.execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Test
  @DisplayName("JDBC 삽입/조회 실습")
  void jdbcInsertSelectTest() throws SQLException {
    // given
    String url = "jdbc:postgresql://localhost:5432/messenger";
    String username = "hyewon";
    String password = "pass";

    // when
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      System.out.println("Connection created: " + connection);

      String insertSql = "INSERT INTO ACCOUNT (id, username, password) VALUES ((SELECT coalesce(MAX(ID), 0) + 1 FROM ACCOUNT A), 'user1', 'pass1')";
      try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
        statement.execute();
      }

      // then
      String selectSql = "SELECT * FROM ACCOUNT";
      try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
        var rs = statement.executeQuery();
        while (rs.next()) {
          System.out.printf("%d, %s, %s", rs.getInt("id"), rs.getString("username"),
              rs.getString("password"));
        }
      }
    }
  }
  @Test
  @DisplayName("JDBC DAO 삽입/조회 실습")
  void jdbcDAOInsertSelectTest() throws SQLException {
    // given
    AccountDAO accountDAO = new AccountDAO();

    // when
    var id = accountDAO.insertAccount(new AccountVO("new user", "new password"));

//    // then
    var account = accountDAO.selectAccount(id);
    assert account.getUsername().equals("new user");
  }
}
