<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<c:set var ="root" value="${pageContext.request.contextPath }"/>
<html>
<head>
<script type="text/javascript" src ="${root}/javascript/member/register.js" ></script>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

	<form action = "${root}/member/zipcode.do" method = "post">
		<div align = "center">
			<table>
				<tr>
					<td>우편번호를 검색하세요</td>
					<td></td>
				</tr>
				<tr>
					<td>
						<input type="text" name = "dong"/>
						<input type="submit" value = "검색"/>
					</td>
				</tr>
			</table>
		
		</div>
	</form>
	
	<div align="center">
		<table>
		<c:choose>
			<c:when test="${zipcodeList.size()==0}">
				<tr>
					<td>검색된 결과가 없습니다. </td>
					<td></td>
				</tr>
			</c:when>
			<c:when test="${zipcodeList.size()>0}">
				<tr>
					<td> 아래 우편번호를 클릭하세요</td>
				</tr>
				
				<c:forEach var="zipDto" items="${zipcodeList}">
					<tr>
						<td>
						<%-- getter메소드 안써줘도 된다. 대소문자 구분도 안한다. --%>
						<a name = "addressa" onclick="addressClick(obj)" href="javascript:sendAddress('${zipDto.zipcode}' , '${zipDto.sido}' , '${zipDto.gugun}','${zipDto.dong}','${zipDto.ri}','${zipDto.bunji}')">
							${zipDto.zipcode}
							${zipDto.sido}
							${zipDto.gugun}
							${zipDto.dong}
							${zipDto.ri}
							${zipDto.bunji}
							</a>
						</td>
						
						
					</tr>
				</c:forEach>
			
			</c:when>
		
		</c:choose>
	
	</table>
	
	</div>
	
	<div align="center">
		<a href="javascript:self.close()">닫기</a>	
	</div>
	
</body>
</html>