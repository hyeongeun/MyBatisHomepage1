package com.java.board.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

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
		int value = 0;
		writeNumber(boardDto);
		
		try {
			String sql = "insert into board(board_number,writer,subject,email,content,password,write_date,read_count,group_number,sequence_number,sequence_level) values(board_board_number_seq.nextval,?,?,?,?,?,?,?,?,?,?)";
			session = sqlSessionFactory.openSession();
			value = session.insert("board_insert",boardDto);
			session.commit();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	public void writeNumber(BoardDto boardDto) {
		// 그룹번호(ROOT), 글순서(자식), 글레벨(자식)


		int boardNumber = boardDto.getBoardNumber();	//0	
		int groupNumber = boardDto.getGroupNumber();	//1	
		int sequenceNumber = boardDto.getSequenceNumber();	//0
		int sequenceLevel = boardDto.getSequenceLevel();	//0

		try {
			if(boardNumber==0) { //groupNumber,0,0	//Root:그룹번호
				
				session = sqlSessionFactory.openSession();
				
				int max =session.selectOne("board_group_number_max");
				if(max!=0) boardDto.setGroupNumber(max+1);
			}else {	//답글: 글순서, 글레벨
				
				session = sqlSessionFactory.openSession();
				session.update("board_reply",boardDto);
				
				sequenceNumber=sequenceNumber+1;
				sequenceLevel=sequenceLevel+1;
				
				boardDto.setSequenceNumber(sequenceNumber);
				boardDto.setSequenceLevel(sequenceLevel);
				session.commit();
			    
			
			}
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
	}
	
	
	public int getCount() {
		int value = 0;

		try {
			session = sqlSessionFactory.openSession();
			value = session.selectOne("board_count");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	
	public List<BoardDto> getBoardList(int startRow, int endRow) {
		List<BoardDto> boardList = null;
		HashMap<String, Integer> hMap = new HashMap<String, Integer>();
		hMap.put("startRow", startRow);
		hMap.put("endRow", endRow);
		
		
		
		try {
			String sql = "select * from (select rownum as rnum, a.* from (select * from board order by group_number desc, sequence_number asc)a)b where b.rnum >=? and b.rnum <= ?";
			session = sqlSessionFactory.openSession();
			boardList = session.selectList("board_list",hMap);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return boardList;
	}
	
	
	
	public BoardDto read(int boardNumber) {
		BoardDto boardDto = null;
		
		try {
			session =sqlSessionFactory.openSession();
			session.update("board_update",boardNumber);
			boardDto = session.selectOne("board_read",boardNumber);
			session.commit();
			
		}catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		return boardDto;
	}
	
	
	public int delete(int boardNumber, String password) {
		HashMap<String, Object> hMap= new HashMap<String, Object>();
		hMap.put("boardNumber", boardNumber);
		hMap.put("password", password);
		int value = 0;
		
		try {
			session = sqlSessionFactory.openSession();
			value = session.delete("board_delete",hMap);
			session.commit();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
		return value;
	}
	
	
	public BoardDto select(int boardNumber) {
		BoardDto boardDto = null;
		try {
			session= sqlSessionFactory.openSession();
			boardDto = session.selectOne("board_read",boardNumber);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return boardDto;
	}
	
	
	public int updateCheck(BoardDto boardDto) {
		int value = 0;
		try {
			session = sqlSessionFactory.openSession();
			value = session.update("board_modify",boardDto);
			session.commit();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	
	public BoardDto selectBoard(int boardNumber) {
		 Connection conn = null;
	      PreparedStatement pstmt = null;
	      ResultSet rs = null;
	      BoardDto boardDto = null;
	      
	      
	      try {
	         String sql = "select * from board where board_number = ?";
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























