package jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdbc.vo.AccountVO;

public class AccountDAO {
//jdbc 관련변수 관리

  private Connection conn = null;
  private PreparedStatement stmt = null;
  private ResultSet rs = null;

  // IntelliJ Database 에서도 조회
  private static final String url = "jdbc:postgresql://localhost:5432/messenger";
  private static final String username = "hyewon";
  private static final String password = "pass";
  //SQL쿼리
  private final String ACCOUNT_INSERT = "INSERT INTO account(ID, USERNAME, PASSWORD) "
      + "VALUES((SELECT coalesce(MAX(ID), 0) + 1 FROM ACCOUNT A), ?, ?)";

  private final String ACCOUNT_GET = "SELECT * FROM account WHERE ID = ?";

  //CRud 기능 메소드
  public Integer insertAccount(AccountVO vo) {
    var id = -1; //   아이디 값이 없을 경우 -1 로하고 . 이건 갠취일듯?

    try {
      String[] returnId = {"id"} ; // 1. 이 아이디로 넣겠다라는 걸 알려주기 위함
      conn = DriverManager.getConnection(url, username, password);
      stmt = conn.prepareStatement(ACCOUNT_INSERT,returnId);  // 2. 받아줌으로써 getGeneratedKeys() 여기에서 값을 받아 올수 있음
      stmt.setString(1, vo.getUsername());
      stmt.setString(2, vo.getPassword());
      stmt.executeUpdate();// 업데이트 하지 않으면 데이터는 생성되나 그전의 값 user, pass로 만들어짐

      try(ResultSet rs = stmt.getGeneratedKeys()){  //todo getGeneratedKeys() 뭔지
        if (rs.next()) {
          id = rs.getInt(1);
        }
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return id;
  }

  public AccountVO selectAccount(Integer id) {

    AccountVO vo = null;

    try {
      conn = DriverManager.getConnection(url, username, password);
      stmt = conn.prepareStatement(ACCOUNT_INSERT);
      stmt.setInt(1, id);
      var rs = stmt.executeQuery();// 쿼리를 날리면 응답값으로 ResultSet값이 온다.  resultSet은 응답값을 담아와 주는 객체
      //리절트셋을 받아가지고 맴핑
      //var 는 객체의 클래스명을 안쓰고 바로 받을 수 있어서 사용함 . 갠취
      if (rs.next()) {
        vo = new AccountVO();
        vo.setId(rs.getInt("ID"));
        vo.setUsername(rs.getString("USERNAME"));
        vo.setPassword(rs.getString("PASSWORD"));
      }

      stmt.executeUpdate();// 업데이트 하지 않으면 데이터는 생성되나 그전의 값 user, pass로 만들어짐

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return vo;
  }

}
