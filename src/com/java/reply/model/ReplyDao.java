package com.java.reply.model;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.java.myBatis.SqlManager;

public class ReplyDao {
	private static SqlSessionFactory sqlSessionFactory = SqlManager.getInstance();
	private SqlSession session;
	
	private static ReplyDao instance = new ReplyDao();
	public static ReplyDao getInstance() {
		return instance;
	}
	
	public int insert(ReplyDto replyDto) {
		int value = 0;
		
		try {
			session = sqlSessionFactory.openSession();
			value = session.insert("reply_writer",replyDto);
			session.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	public int getBunho() {
		int value = 0;
		
		try {
			session= sqlSessionFactory.openSession();
			value = session.selectOne("reply_bunho");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		
		return value;
	}

	public List<ReplyDto> getList() {
		List<ReplyDto> list = null;
		
		try {
			session = sqlSessionFactory.openSession();
			list = session.selectList("reply_list");
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return list;
	}

}
