<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    
<!DOCTYPE html>
<c:set var="root" value="${pageContext.request.contextPath }"/>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${root}/xhr/xhr.js"></script>
<script type="text/javascript" src="${root}/javascript/reply/replyWrite.js"></script>
<link rel="stylesheet" href="${root}/css/reply/reply.css"/> 
</head>
<body>
	<div>실시간 댓글이 가능합니다.</div>
	<br><br>
	
	<div>
		<input id="writeReply" type="text" name="write" size="50"/>
		<input type="button" value="한줄댓글" onclick="writeToServer('${root}')"/> 
	</div>
	
	<div id="listAllDiv">
	
		<!-- 실시간 댓글(새로운 글) -->
		
		<!-- 기존글 -->
		<c:forEach var="replyDto" items="${replyList}">
			<div class="replyDiv" id = "${replyDto.bunho}">
				<span class ="cssBunho">${replyDto.bunho }</span>
				<span class ="cssBunho">${replyDto.line_reply }</span>
				<span class="cssUpDel">
					<a href = "javascript:deleteToServer('${replyDto.bunho}','${root}')">삭제</a>
					<a href = "javascript:selectToServer('${replyDto.bunho}','${root}')">수정</a>
				</span>
			</div>
		
		</c:forEach>
	
	</div>
	
</body>
</html>