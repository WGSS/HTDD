$(document).ready(function(){
    var pathName = window.document.location.pathname;
    var projectName = pathName.substring(1, pathName.substr(1).indexOf('/') + 1);
    var project = pathName.substr(1);
    if(pathName=="/"){
        $("#firstpage").addClass("active");
    }
    else if(projectName=="/"){
        $("#" + project).addClass("active");
    }else {
        $("#" + projectName).addClass("active");
    }
});