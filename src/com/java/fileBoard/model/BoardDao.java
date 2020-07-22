package com.java.fileBoard.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.websocket.Session;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.java.database.ConnectionProvider;
import com.java.database.JdbcUtil;
import com.java.myBatis.SqlManager;

public class BoardDao {
	private static SqlSessionFactory sqlSessionFactory = SqlManager.getInstance();
	private SqlSession session;

	private static BoardDao instance = new BoardDao();

	public static BoardDao getInstance() {
		return instance;
	}
	public int insert(BoardDto boardDto) {
		Connection conn = null;
		PreparedStatement pstmt = null;


		int value = 0;
		writeNumber(boardDto,conn);

		String sql=null;
		try {
			if(boardDto.getFileSize()==0) {
				sql = "insert into board(board_number,writer,subject,email,content,password,write_date,read_count,group_number,sequence_number,sequence_level) values(board_board_number_seq.nextval,?,?,?,?,?,?,?,?,?,?)";
				conn = ConnectionProvider.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, boardDto.getWriter());
				pstmt.setString(2, boardDto.getSubject());
				pstmt.setString(3, boardDto.getEmail());
				pstmt.setString(4, boardDto.getContent().replace("\r\n","<br/>"));
				pstmt.setString(5, boardDto.getPassword());

				//			Date date = boardDto.getWriteDate();
				//			long time = date.getTime();
				//			Timestamp ts = new Timestamp(time);
				pstmt.setTimestamp(6, new Timestamp(boardDto.getWriteDate().getTime()));


				pstmt.setInt(7, boardDto.getReadCount());
				pstmt.setInt(8, boardDto.getGroupNumber());
				pstmt.setInt(9, boardDto.getSequenceNumber());
				pstmt.setInt(10, boardDto.getSequenceLevel());
			}else {
				sql = "insert into board(board_number,writer,subject,email,content,password,write_date,read_count,group_number,sequence_number,sequence_level,file_name, path, file_size) values(board_board_number_seq.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				conn = ConnectionProvider.getConnection();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, boardDto.getWriter());
				pstmt.setString(2, boardDto.getSubject());
				pstmt.setString(3, boardDto.getEmail());
				pstmt.setString(4, boardDto.getContent().replace("\r\n","<br/>"));
				pstmt.setString(5, boardDto.getPassword());
				pstmt.setTimestamp(6, new Timestamp(boardDto.getWriteDate().getTime()));
				pstmt.setInt(7, boardDto.getReadCount());
				pstmt.setInt(8, boardDto.getGroupNumber());
				pstmt.setInt(9, boardDto.getSequenceNumber());
				pstmt.setInt(10, boardDto.getSequenceLevel());
				pstmt.setString(11, boardDto.getFileName());
				pstmt.setString(12, boardDto.getPath());
				pstmt.setLong(13, boardDto.getFileSize());
			}

			value = pstmt.executeUpdate();


		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(conn);
			JdbcUtil.close(pstmt);
		}
		return value;
	}

	public void writeNumber(BoardDto boardDto, Connection conn) {
		// 그룹번호(ROOT), 글순서(자식), 글레벨(자식)


		int boardNumber = boardDto.getBoardNumber();	//0	
		int groupNumber = boardDto.getGroupNumber();	//1	
		int sequenceNumber = boardDto.getSequenceNumber();	//0
		int sequenceLevel = boardDto.getSequenceLevel();	//0



		try {
			if(boardNumber == 0) { //groupNumber,0,0	//Root:그룹번호
				session = sqlSessionFactory.openSession();

				int max =session.selectOne("fileBoard_group_number_max");
				if(max!=0)boardDto.setGroupNumber(max+1);

			}else {	//답글: 글순서, 글레벨
				//				sql ="update board set sequence_number = sequence_number+1 where group_number=? and sequence_number > ?";
				//
				//				conn = ConnectionProvider.getConnection();
				//				pstmt = conn.prepareStatement(sql);
				//				pstmt.setInt(1, groupNumber);
				//				pstmt.setInt(2, sequenceNumber);
				//
				//				sequenceNumber=sequenceNumber+1;
				//				sequenceLevel=sequenceLevel+1;
				//
				//				boardDto.setSequenceNumber(sequenceNumber);
				//				boardDto.setSequenceLevel(sequenceLevel);
				//
				//				pstmt.executeUpdate();
				//

			}


		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
	}


	public int getCount() {
		int value = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "select count(*) from board";
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs=pstmt.executeQuery();

			if(rs.next()) value = rs.getInt(1); 	//1컬럼 가지고 오기 



		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(conn);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(rs);
		}
		return value;
	}


	public ArrayList<BoardDto> getBoardList(int startRow, int endRow) {
		ArrayList<BoardDto> boardList = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		boardList = new ArrayList<BoardDto>();

		try {
			String sql = "select * from (select rownum as rnum, a.* from (select * from board order by group_number desc, sequence_number asc)a)b where b.rnum >=? and b.rnum <= ?";


			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();

			while(rs.next()) {
				BoardDto boardDto = new BoardDto();
				boardDto.setBoardNumber(rs.getInt("board_number"));
				boardDto.setWriter(rs.getString("writer"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setEmail(rs.getString("email"));
				boardDto.setContent(rs.getString("content"));

				boardDto.setPassword(rs.getString("password"));
				boardDto.setWriteDate(new Date(rs.getTimestamp("write_date").getTime()));
				boardDto.setReadCount(rs.getInt("read_count"));
				//내부적으로 밑의 3개는 필요하다
				boardDto.setGroupNumber(rs.getInt("group_number"));
				boardDto.setSequenceNumber(rs.getInt("sequence_number"));
				boardDto.setSequenceLevel(rs.getInt("sequence_level"));

				//System.out.println(boardDto);

				boardList.add(boardDto);
			}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(conn);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(rs);
		}
		return boardList;
	}

	public BoardDto read(int boardNumber) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDto boardDto = null;

		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false); //auto커밋 안해주기

			String sqlUpdate = "update board set read_count=read_count+1 where board_number=?";
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setInt(1, boardNumber);
			int value = pstmt.executeUpdate();
			if(value > 0) JdbcUtil.close(pstmt);

			String sqlSelct = "select * from board where board_number=?";
			pstmt = conn.prepareStatement(sqlSelct);
			pstmt.setInt(1, boardNumber);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				boardDto = new BoardDto();
				boardDto.setBoardNumber(rs.getInt("board_number"));
				boardDto.setWriter(rs.getString("writer"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setEmail(rs.getString("email"));
				boardDto.setContent(rs.getString("content"));

				boardDto.setPassword(rs.getString("password"));
				boardDto.setWriteDate(new Date(rs.getTimestamp("write_date").getTime()));
				boardDto.setReadCount(rs.getInt("read_count"));
				boardDto.setGroupNumber(rs.getInt("group_number"));
				boardDto.setSequenceNumber(rs.getInt("sequence_number"));
				boardDto.setSequenceLevel(rs.getInt("sequence_level"));

				boardDto.setFileName(rs.getString("file_name"));
				boardDto.setPath(rs.getString("path"));
				boardDto.setFileSize(rs.getLong("file_size"));

			}

			conn.commit();

		}catch (Exception e) {
			e.printStackTrace();
			JdbcUtil.rollBack(conn);
		}finally {
			JdbcUtil.close(conn);
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

		return boardDto;
	}


	public BoardDto readSelect(int boardNumber) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDto boardDto = null;

		try {
			conn = ConnectionProvider.getConnection();
			String sqlUpdate = "update board set read_count=read_count+1 where board_number=?";
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setInt(1, boardNumber);
			int value = pstmt.executeUpdate();
			if(value > 0) JdbcUtil.close(pstmt);

			String sqlSelct = "select * from board where board_number=?";
			pstmt = conn.prepareStatement(sqlSelct);
			pstmt.setInt(1, boardNumber);
			rs = pstmt.executeQuery();

			if(rs.next()) {
				boardDto = new BoardDto();
				boardDto.setFileName(rs.getString("file_name"));
				boardDto.setPath(rs.getString("path"));
				boardDto.setFileSize(rs.getLong("file_size"));

			}
		}catch (Exception e) {
			e.printStackTrace();
			JdbcUtil.rollBack(conn);
		}finally {
			JdbcUtil.close(conn);
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);

		}

		return boardDto;
	}


	public int delete(int boardNumber, String password) {
		Connection conn=null;
		PreparedStatement pstmt=null;
		int value = 0;
		try {
			conn = ConnectionProvider.getConnection();
			String sql = "delete from board where board_number=? and password=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNumber);
			pstmt.setString(2, password);

			value = pstmt.executeUpdate();

		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(pstmt);
			JdbcUtil.close(conn);

		}

		return value;
	}
	public BoardDto select(int boardNumber) {
		BoardDto boardDto = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			conn = ConnectionProvider.getConnection();
			String sql = "select * from board where board_number = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNumber);

			rs= pstmt.executeQuery();

			if(rs.next()) {
				boardDto = new BoardDto();
				boardDto.setBoardNumber(rs.getInt("board_number"));
				boardDto.setWriter(rs.getString("writer"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setEmail(rs.getString("email"));
				boardDto.setContent(rs.getString("content"));

				boardDto.setPassword(rs.getString("password"));
				boardDto.setWriteDate(new Date(rs.getTimestamp("write_date").getTime()));
				boardDto.setReadCount(rs.getInt("read_count"));
				boardDto.setGroupNumber(rs.getInt("group_number"));
				boardDto.setSequenceNumber(rs.getInt("sequence_number"));
				boardDto.setSequenceLevel(rs.getInt("sequence_level"));

				boardDto.setFileName(rs.getString("file_name"));
				boardDto.setPath(rs.getString("path"));
				boardDto.setFileSize(rs.getLong("file_size"));
			}

		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(conn);
		}


		return boardDto;
	}
	public int updateCheck(BoardDto boardDto) {
		int value = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			//			conn = ConnectionProvider.getConnection();
			//			String sqlSelect = "select * from board where boardNumber = ?";
			//			
			//			pstmt = conn.prepareStatement(sqlSelect);
			//			pstmt.setInt(1, boardDto.getBoardNumber());
			//			rs = pstmt.executeQuery();
			//			
			//			if(rs.next()) value = 1;
			//			if(value > 0 ) JdbcUtil.close(pstmt);

			conn= ConnectionProvider.getConnection();
			String sqlUpdate = "update board set subject=?, content=?, email=?, password=? where board_number=? ";
			pstmt = conn.prepareStatement(sqlUpdate);
			pstmt.setString(1, boardDto.getSubject());
			pstmt.setString(2, boardDto.getContent());
			pstmt.setString(3, boardDto.getEmail());
			pstmt.setString(4, boardDto.getPassword());
			pstmt.setInt(5, boardDto.getBoardNumber());

			value = pstmt.executeUpdate();


		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(conn);

		}



		return value;
	}
	public BoardDto selectBoard(int boardNumber) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardDto boardDto = null;


		try {
			String sql = "select * from board where board_number=?";
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, boardNumber);
			rs = pstmt.executeQuery();


			if(rs.next()) {
				boardDto = new BoardDto();
				boardDto.setBoardNumber(rs.getInt("board_number"));
				boardDto.setWriter(rs.getString("writer"));
				boardDto.setSubject(rs.getString("subject"));
				boardDto.setEmail(rs.getString("email"));
				boardDto.setContent(rs.getString("content"));

				boardDto.setPassword(rs.getString("password"));
				boardDto.setWriteDate(new Date(rs.getTimestamp("write_date").getTime()));
				boardDto.setReadCount(rs.getInt("read_count"));
				boardDto.setGroupNumber(rs.getInt("group_number"));
				boardDto.setSequenceNumber(rs.getInt("sequence_number"));
				boardDto.setSequenceLevel(rs.getInt("sequence_level"));
			}


		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
			JdbcUtil.close(conn);

		}

		return boardDto;
	}

	public int modify(BoardDto boardDto) {
		int value = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			String sql = "update board set subject=?, content=?, email=? where board_number= ? ";
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, boardDto.getSubject());
			pstmt.setString(2, boardDto.getContent());
			pstmt.setString(3, boardDto.getEmail());
			pstmt.setInt(4, boardDto.getBoardNumber());
			value = pstmt.executeUpdate();


		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtil.close(pstmt);
			JdbcUtil.close(conn);

		}


		return value;
	}


	//	public void select(BoardDto boardDto, Connection conn) {
	//		PreparedStatement pstmt = null;
	//		ResultSet rs = null;
	//		
	//		try {
	//			conn = ConnectionProvider.getConnection();
	//			String sql = "select * from board where boardNumber = ?";
	//			pstmt = conn.prepareStatement(sql);
	//			pstmt.setInt(1, boardDto.getBoardNumber());
	//			rs = pstmt.executeQuery();
	//			
	//			
	//			
	//			
	//		}catch(Exception e) {
	//			e.printStackTrace();
	//		}finally {
	//			JdbcUtil.close(rs);
	//			JdbcUtil.close(pstmt);
	//			JdbcUtil.close(conn);
	//		}
	//		
	//	}

}























