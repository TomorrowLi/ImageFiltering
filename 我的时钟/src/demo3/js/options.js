
/*************************************************

	OPTIONS.js
	------
	This code is executed on the OPTIONS.html page
	It runs in the context of the extension Options page (not the page, not the background)
	Is fired when the user opens the extension options page

	Communication
		* Options.js <-> Background.js via localStorage

*************************************************/


//-----------------------------------------------------------------------------
// [OPTIONS] IMAGE REPLACEMENT 

// Add listeners

ii = document.querySelectorAll('#imageReplacement input');

for(var i=0, max = ii.length; i<max; i++) {
    ii[i].addEventListener('click', handleImageReplacementClick);
}

// Get image # to use
var currImageReplacementDefault = 0;
var currImageReplacement = localStorage["replacement_image"] || currImageReplacementDefault;
//console.log("currImageReplacement: "+currImageReplacement);

// Save value on Local Storage
function handleImageReplacementClick() {
    localStorage["replacement_image"] = this.value;
    //parent() 获得当前匹配元素集合中每个元素的父元素
    //siblings() 获得匹配集合中每个元素的同胞，通过选择器进行筛选是可选的。
    $(this).parent().addClass('selected').siblings().removeClass('selected');
}

// Oh, you silly CSS, starting indexes with 1 instead of 0...
// 设置索引从1开始而不是从0开始
currImageReplacementCSS = parseInt(currImageReplacement) + 1;
//选取imageReplacement下的第currImageReplacementCSS个li元素添加selected类
$("#imageReplacement li:nth-child("+currImageReplacementCSS+")").addClass('selected');




//-----------------------------------------------------------------------------
// [OPTIONS] Desaturation

function getIsDesaturated() {
	// return localStorage['is_desaturated'] === "true";
	return !(localStorage['is_desaturated'] === "false");
}
function setIsDesaturated(value) {
	localStorage['is_desaturated'] = value;
}
//设置desat复选框的checked的属性值，选中或者是没有选中
$("#desat").prop('checked', getIsDesaturated());
// $("#desat").addEventListener('click', handleDesaturateClick);
$("#desat").click(function() {
    var $this = $(this);
    // $this will contain a reference to the checkbox   
    if ($this.is(':checked')) {
        // the checkbox was checked 
	    setIsDesaturated(true);
    } else {
        // the checkbox was unchecked
	    setIsDesaturated(false);
    }
});


//-----------------------------------------------------------------------------
// [OPTIONS] White BG

function getUseWhiteBg() {
	return !(localStorage['use_white_bg'] === "false");
}
function setUseWhiteBg(value) {
	localStorage['use_white_bg'] = value;
}
//设置whiteBg复选框的checked的属性值，选中或者是没有选中
$("#whiteBg").prop('checked', getUseWhiteBg());
$("#whiteBg").click(function() {
    var $this = $(this);
    // $this will contain a reference to the checkbox
    // Jquery利用is(“:checked”)判断多选框是不是被选中 
    if ($this.is(':checked')) {
        // the checkbox was checked 
	    setUseWhiteBg(true);
    } else {
        // the checkbox was unchecked
	    setUseWhiteBg(false);
    }
});



//-----------------------------------------------------------------------------
// DELETE ME
// old code sample

// // Saves options to localStorage.
// function save_options() {
//   var select = document.getElementById("color");
//   var color = select.children[select.selectedIndex].value;
//   localStorage["favorite_color"] = color;

//   // Update status to let user know options were saved.
//   var status = document.getElementById("status");
//   status.innerHTML = "Options Saved.";
//   setTimeout(function() {
//     status.innerHTML = "";
//   }, 750);
// }

// // Restores select box state to saved value from localStorage.
// function restore_options() {
//   var favorite = localStorage["favorite_color"];
//   if (!favorite) {
//     return;
//   }
//   var select = document.getElementById("color");
//   for (var i = 0; i < select.children.length; i++) {
//     var child = select.children[i];
//     if (child.value == favorite) {
//       child.selected = "true";
//       break;
//     }
//   }
// }
// document.addEventListener('DOMContentLoaded', restore_options);
// document.querySelector('#save').addEventListener('click', save_options);
