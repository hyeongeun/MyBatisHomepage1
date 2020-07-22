package com.java.reply.command;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.java.command.Command;
import com.java.reply.model.ReplyDao;
import com.java.reply.model.ReplyDto;

public class ReplyWriteCommand implements Command {

	@Override
	public String proRequest(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		String writeReply = request.getParameter("writeReply");
		logger.info(logMsg+writeReply);
		
		ReplyDto replyDto = new ReplyDto();
		replyDto.setLine_reply(writeReply);
		
		int check = ReplyDao.getInstance().insert(replyDto);
		logger.info(logMsg+check);
		
		
		if(check>0) {
			int bunho = ReplyDao.getInstance().getBunho();
			logger.info(logMsg+writeReply+"," + bunho);
			
			String str = bunho + "," + writeReply;	//-> JSON - SPRING
			response.setContentType("application/text;charset=utf-8"); //application/json json이라면 저렇게 쓰기
			PrintWriter pw = response.getWriter();
			pw.print(str);
		}
		
		
		return null;
	}

}
