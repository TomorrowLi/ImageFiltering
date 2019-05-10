 //控制层 
app.controller('imagesController' ,function($scope,uploadService){

	$scope.fileUpload=function () {
		uploadService.uploadFile().success(function (response) {

			if(response.success){
                $scope.image_entity.url=response.message
			}else {
				alert(response.message)
			}
        }).error(function() {
            alert("上传发生错误");
        });

    }

});	
