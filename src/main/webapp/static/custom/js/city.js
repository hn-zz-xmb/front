/**
 * Created by rsp on 14-11-17.
 */

function findCitys(el) {
    var provinceId = $(el).val();
    var param = {
        "privinceId" : provinceId
    }
    $.ajax({
        url : MSConfig.BaseURL+"/city/city",
        method : "get",
        data : param,
        dataType : "json",
        success : function(data) {
            var htmlStr = "<option selected value=''>请选择...</option>";
            for (var i = 0; i < data.length; i++) {
                htmlStr += "<option value='"+data[i].id+"'>" + data[i].name
                + "</option>"
            }
            $(el).next("select").html(htmlStr);
        },
        error : function() {

        }
    });
}

function findAreas(el) {
    var cityId = $(el).val();
    var param = {
        "cityId" : cityId
    }
    $.ajax({
        url : MSConfig.BaseURL+"/city/area",
        method : "get",
        data : param,
        dataType : "json",
        success : function(data) {
            var htmlStr = "<option selected value=''>请选择...</option>";
            for (var i = 0; i < data.length; i++) {
                htmlStr += "<option value='"+data[i].id+"'>" + data[i].name
                + "</option>"
            }
            $(el).next("select").html(htmlStr);
        },
        error : function() {
        }
    });
}

function findStreets(el) {
    var areaId = $(el).val();
    var param = {
        "areaId" : areaId
    }
    $.ajax({
        url : MSConfig.BaseURL+"/city/street",
        method : "get",
        data : param,
        dataType : "json",
        success : function(data) {
            var htmlStr = "<option selected value=''>请选择...</option>";
            for (var i = 0; i < data.length; i++) {
                htmlStr += "<option value='"+data[i].id+"'>" + data[i].name
                + "</option>"
            }
            $(el).next("select").html(htmlStr);
        },
        error : function() {

        }
    });
}
