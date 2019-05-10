//main.js中代码
// function sendmessage(){
//     var msg = {content:'从main.js发送给background.js',};
//     chrome.runtime.sendMessage(msg,function(response) {
//         console.log('content get response:',response);
//     });
// }
// sendmessage()
//main.js添加一个监听器，收到来自popup.js的消息后，在浏览器的console中显示出来
chrome.extension.onMessage.addListener(
    function(request, sender, sendResponse){
        var imgs = document.getElementsByTagName("img");
        var strings = request.action.split("/");
        for(var i=0;i< imgs.length ;i++){
            var mg=imgs[i];
            var attribute = imgs[i].getAttribute("src");
            if(attribute.match("http://")||attribute.match("https://")){
                image=attribute;
            }else {
                image="http://"+strings[2]+"/"+attribute;
            }
            url="http://localhost:8090/image/findAll?path="+image;
            httpRequestImg(url,mg,function (mg,result) {
                result = JSON.parse(result);
                resultImage=result["conclusion"];
                if(resultImage==="正常"){
                    mg.setAttribute("src","");
                    //mg.style.backgroundImage='url(holder.js/300x200?auto=yes&text=此图片不是一张绿色图片)'

                }
            })


        }
    }
);

function httpRequestImg(url,mg, callback){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4) {
            callback(mg,xhr.responseText);
        }
    }
    xhr.send();
}

