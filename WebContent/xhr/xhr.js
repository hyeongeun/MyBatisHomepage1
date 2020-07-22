/**
 * 
 */

/* 객체 생성 */
function createXHR() {
   if(window.XMLHttpRequest) {
      return new XMLHttpRequest;   
   }else {
      return new ActiveXObject("Microsoft.XMLHTTP");
   }
}

var xhr=null;
function sendRequest(method,url,params,callback) { // GET, xhr.txt, message=message, fromServer

   var httpMethod=method.toUpperCase();
   var httpUrl=url;
   var httpParams=(params==null || params=="")?null:params;
   
   if(httpMethod=="GET" && httpMethod!=null) {
      httpUrl+="?"+httpParams;
   }
   
   arr.push(httpMethod+httpUrl+"/"+httpParams);
   
   alert(arr.join("\n"));
   
   xhr=createXHR();
   xhr.open(httpMethod,httpUrl,true);
   xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
   xhr.send(httpMethod=="POST"?httpParams:null);
   xhr.onreadystatechange=callback;
}