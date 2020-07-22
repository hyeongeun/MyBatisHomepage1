/**
 * 
 */
var arr = new Array();
	function writeToServer(root){
		var writeReply = document.getElementById("writeReply").value;
		arr.push(root+" , "+writeReply);
		//alert(arr.join("\n"));
		var url = root +"/reply/replyWrite.do";
		var params ="writeReply="+writeReply;
		
		sendRequest("post",url,params,writeFromServer);
		
		
	}
	
	function writeFromServer(){
		if(xhr.readyState==4 && xhr.status==200){
			arr.push("result"+xhr.responseText);
			
			//alert(arr.join("\n"));
			
			//받은 데이터 분리해서 읽기, 확실하게 공백을 제거해야 한다. 
			var result = xhr.responseText.split(",");
			var bunho = result[0].trim();
			var reply = result[1].trim();
			
			//입력창 비워주기
			document.getElementById("writeReply").value = "";
			
			//답글 1개당 총 6개 태그 생성
			//생성한 태그 붙일 div태그 찾아오기
			var listDiv = document.getElementById("listAllDiv");
			
			//한개 답글을 담을 div 태그 생성. 아이디를 시퀀스 번호로 지정
			var div = document.createElement("div");
			div.className = "replyDiv";
			div.id = bunho;
			
			
			
			
			//태그 붙이기
			listDiv.appendChild(div);
			
			
			
		}
	}