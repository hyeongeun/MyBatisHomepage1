package com.java.member.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.java.myBatis.SqlManager;

public class MemberDao {	//Data Access Object
	
	
	private static SqlSessionFactory sqlSessionFactory = SqlManager.getInstance();
	private SqlSession session;
	
	//singleton pattern : 단 한개의 객체만을 가지고 구현(설계)
	private static MemberDao instance = new MemberDao();
	public static MemberDao getInstance() {
		
		return instance;
	}
	
	
	public int insert(MemberDto memberDto) {
		int value = 0;
		try {
			session = sqlSessionFactory.openSession();
			value = session.insert("member_insert", memberDto);
			session.commit();
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	
	public int idCheck(String id) {
		int value = 0;
		
		try {
		
			session = sqlSessionFactory.openSession();
			String checkId = session.selectOne("id_check",id);
			
			if(checkId!=null) value=1;
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	
	
	public List<ZipcodeDto> zipcodeReader(String checkDong){
		List<ZipcodeDto> arrayList = null;
		
		try {
			session = sqlSessionFactory.openSession();
			arrayList=session.selectList("member_zipcode",checkDong);
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return arrayList;
	}
	
	
	public String loginCheck(String id,String password) {
		String value = null;
		
		try {
			HashMap<String, String> hMap = new HashMap<String, String>();
			hMap.put("id", id);
			hMap.put("password", password);

			session = sqlSessionFactory.openSession();
			value = session.selectOne("member_login", hMap);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		return value;
	}

	public MemberDto updateId(String id) {
		MemberDto memberDto = null;

		
		try {
			session = sqlSessionFactory.openSession();
			memberDto = session.selectOne("member_select",id);
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		return memberDto;
	}

	public int update(MemberDto memberDto) {
		
		int value = 0;
		
		try {
			String sql = "update member set password =?, email=?, zipcode=?, address=?, job=?, mailing=?, interest=? where num=?";
			session = sqlSessionFactory.openSession();
			
			value = session.update("member_update",memberDto);
			session.commit();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		return value;
	}
	
	
	public int delete(String id, String password) {
		int value = 0;
		try {
			Map<String, String> hMap = new HashMap<String, String>();
			hMap.put("id",id);
			hMap.put("password",password);
			
			session = sqlSessionFactory.openSession();
			value = session.delete("member_delete",hMap);
			session.commit();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			session.close();
		}
		return value;
	}
	

}
