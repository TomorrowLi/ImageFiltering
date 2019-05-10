

var iconOn = "../imgs/iconOn.png";
var iconOff = "../imgs/iconOff.png";
// Refresh

var imageResult=false;
//------------------------------------------------
//刷新标签页
function refreshTab(tabId) {
    chrome.tabs.reload(tabId);
}
// 当用户单击浏览器操作时调用。
chrome.browserAction.onClicked.addListener(function(tab) {

    //判断切换按钮是否全部启用
    toggleIsEnableAll();
    //设置监听事件
    setListeners();
    /*if(getIsEnableAll()){
        sendMessage();
    }*/
    //更新图标
    updateUI();
    //要重新加载的标签页标识符，默认为当前窗口的选定标签页
    //
    refreshTab(tab.id);
    // chrome.tabs.executeScript(
    // 	tab.id,
    // 	// {code:"document.body.style.background='red !important'"},
    // 	{code:"-webkit-filter: grayscale(100%) !important;"},
    // 	null
    // 	);
    // chrome.tabs[tab.id].insertCSS(
    // 	tab.id,
    // 	// {code:"document.body.style.background='red !important'"},
    // 	{code:"body {-webkit-filter: grayscale(100%) !important;}"},
    // 	null
    // 	);
});

//------------------------------------------------
// Setup
//设置监听

function setListeners() {

    //获取是否开启
    var isEnabled = getIsEnableAll();
    //在控制台上打印替换图片的id号
    console.log("setListeners: getReplacementImageID=" + getReplacementImageID());

    if(isEnabled&&getReplacementImageID()>=0){


        // 仅当为当前上下文启用扩展时才设置侦听器
// onBeforeRequest：当请求即将发出时产生。这一事件在 TCP 连接建立前发送，可以用来取消或重定向请求
        chrome.webRequest.onBeforeRequest.addListener(
            onBeforeRequestImage,
            // filters 参数允许通过不同的方式限制为哪些请求产生事件
            {urls: ["http://*/*",
                    "https://*/*"
                ],
                /* "http://!*!/!*",
                 "https://!*!/!*"*/
                // Possible values:
                // "main_frame", "sub_frame", "stylesheet", "script",
                // "image", "object", "xmlhttprequest", "other"
                //请求类型，例如 "main_frame"（为顶层框架加载的文档）、
                //"sub_frame"（为内嵌框架加载的文档）和 "image"（网站上的图片）
                types: ["image"]
            },
            ["blocking"]
        )
    }else {
        chrome.webRequest.onBeforeRequest.removeListener( onBeforeRequestImage );
    }

}

onBeforeRequestImage = function(details) {
    //页面
    var str=JSON.stringify(details);

    if(getIsEnableAll()){
        var serverUrl="http://192.168.111.129/image/findAllImage?path="+JSON.parse(str)["url"];
        httpRequestImg(serverUrl);

        return {redirectUrl: getBlankReplacementImage()};
    }

}


function httpRequestImg(url){
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, false);
    //每当 readyState 改变时，就会触发 onreadystatechange 事件。
    //readyState 属性存有 XMLHttpRequest 的状态信息
    xhr.onreadystatechange = function() {
        //当 readyState 等于 4 且状态为 200 时，表示响应已就绪
        if (xhr.readyState === 4 && xhr.status === 200) {
            var result = JSON.parse(xhr.responseText);

            //
            /*if (result["conclusion"] === "性感") {
                imageResult=true;
            } else {
                imageResult=false;
            }*/
            //alert(result["result"]["antiporn"]["conclusion"])

            if (result["result"]["antiporn"]["conclusion"] === "性感") {
                imageResult=true;
            } else {
                imageResult=false;
            }
        }

    }

    xhr.send();
}


//获取要替换的图片格式的value属性
function getReplacementImageID() {
    var currImageReplacementDefault = 1;
    var currImageReplacementID = localStorage["replacement_image"] || currImageReplacementDefault;

    return currImageReplacementID;
}
//将扩展安装目录内的文件的相对路径转为FQDN URL
function getBlankReplacementImage() {

    var currImageReplacementID = getReplacementImageID();
    if(imageResult){
        var imageReplacement = chrome.extension.getURL("imgs/bg/bg_"+currImageReplacementID+".png");
    }
    return imageReplacement;
}
function sendMessage() {
    chrome.tabs.query({active: true, currentWindow: true}, function(tabs){
        chrome.tabs.sendMessage(tabs[0].id, { action: tabs[0].url });
    });
}

function getIsEnableAll()
{
    return localStorage['enable_all'] === "true";
}
function setIsEnableAll(enable)
{
    localStorage['enable_all'] = enable;
    return enable;
}
function toggleIsEnableAll()
{
    return setIsEnableAll(!getIsEnableAll());
}

//设置监听

function updateUI() {
    var isEnabled = getIsEnableAll();
    var iconCurr = isEnabled ? iconOn : iconOff;
    chrome.browserAction.setIcon({path:iconCurr});
}


//------------------------------------------------
// Replacement Image
//-----------------------------------------------
setListeners();
updateUI();
