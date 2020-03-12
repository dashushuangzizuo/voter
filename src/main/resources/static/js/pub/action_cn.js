function submitVote() {
    clearAllError();
    setTimeout(function () {
        if (checkAll()) {
            //Build serial
            var build = "";
            var text = [];
            for (var i = 1; i <= options; ++i) {
                var num = i;
                var count = 0;
                var selectionText = $("#option" + i).val();
                build += "<%" + num + "<%" + count + "<%" + selectionText;

                var desc = $("#text"+i).val();
                text[i-1]=desc;
            }
            params = {};
            params.title = $("#voteTitle").val();
            params.describe = $("#voteDescribe").val();
            params.selection = build;
            params.type = mode;
            params.limit = $("#modeInput").val();
            params.pass = $("#pass").val();
            params.voternum = $("#voternum").val();
            params.textdesc = text;
            if (mode == 1) {
                params.limit = -1;
            }
            $.ajax({
                url: "/voterSubmit",
                data: params,
                success: function (data) {
                    setTimeout(function () {
                        if (data == -7426) {
                            $(".isError").html("<font color='red'>非法输入 :( </font>请检查。");
                        } else {
                            location.href = "/vote/adm/" + data+"/"+params.pass;
                        }
                    }, 1000);
                }
            });
        } else {
            $(".isError").html("<font color='red'>非法输入 :( </font>请检查。");
        }
    }, 800);
}

var mode = 0;

function singleElection() {
    mode = 1;
    $("#mode").html("单项选择 <span class=\"caret\"></span>");
    $("#modeInput").attr("disabled", "");
    $("#modeInput").val("-1");
}

function multipleSelection() {
    mode = 0;
    $("#mode").html("多项选择 <span class=\"caret\"></span>");
    $("#modeInput").removeAttr("disabled");
}

var options = 3;

function addOption() {
    $("#close" + options).remove();
    ++options;
    var append = "" +
        "    <div class=\"option" + options + "\">\n" +
        "        <div class=\"input-group\">\n" +
        "            <span class=\"input-group-addon\">选项 " + options + "</span>\n" +
        "            <input class=\"form-control\" id=\"option" + options + "\" type=\"text\">\n" +
        "            <div class=\"cls\"></div>\n" +
        "            <span class=\"input-group-btn\" id=\"close" + options + "\">\n" +
        "                <button class=\"btn btn-default\" type=\"button\" onclick=\"delOption()\">\n" +
        "                    <i class=\"fa fa-close\"></i>\n" +
        "                </button>\n" +
        "            </span>\n" +
        "        </div>\n" +
        "        <br>\n" +
        "    </div>";
    $(".add").before(append);
    append="" +"<li><a href=\"#detail" + options + "\" tt=\"\" data-toggle=\"tab\">选项 " + options +"</a></li>";
    $(".nav_add").before(append);
    append="" +"       <div class=\"tab-pane\" id=\"detail" + options + "\">" +
               "             <form role=\"form\">" +
                "                   <div class=\"form-group\">" +

                "                          <label for=\"text" + options +"\">简介" + options + "</label>" +
                 "                         <textarea class=\"form-control\" rows=\"5\" id=\"text" + options + "\"></textarea>" +

                 "                 </div>" +

                  "            </form>" +
                   "        </div>";
     $(".tab_add").before(append);
     $("#tabOption > li").siblings().removeClass("active");
     $("#tabOption > li:last").addClass("active");
     $("#myTabContent > div.tab-pane").removeClass("active");
     $("#myTabContent > div.tab-pane:last").addClass("active");

}

function delOption() {
    $(".option" + options).remove();
    $("#detail" + options ).remove();
    --options;
    $(".option" + options + " .cls").after("" +
        "            <span class=\"input-group-btn\" id=\"close" + options + "\">\n" +
        "                <button class=\"btn btn-default\" type=\"button\" onclick=\"delOption()\">\n" +
        "                    <i class=\"fa fa-close\"></i>\n" +
        "                </button>\n" +
        "            </span>");
    //
    $("#tabOption > li").siblings().removeClass("active");
    $("#tabOption > li > a:last").remove();
    $("#tabOption > li").first().addClass("active");
    $("#myTabContent > div.tab-pane").removeClass("active");
    $("#myTabContent > div.tab-pane:first").addClass("active");
}

function showTabView(){
 $(".option")
}
//Maximum limit check
$("#modeInput").on("input propertychange", function () {
    var input = Number($("#modeInput").val());
    if (isNaN(input)) {
        errorS();
    } else {
        if (input <= options) {
            successS();
        } else {
            errorS();tab_add
        }
    }
});

function errorS() {
    $("#status").removeClass("has-success");
    $("#status").addClass("has-error");
}

function successS() {
    $("#status").removeClass("has-error");
    $("#status").addClass("has-success");
}

//All input check
function checkAll() {
    var allDone = true;
    //Options check
    for (var i = 1; i <= options; ++i) {
        var selectionText = $("#option" + i).val();
        if (selectionText == "") {
            allDone = false;
            $(".option" + i).addClass("has-error");
        }
    }
    //Build params
    params = {};
    params.title = $("#voteTitle").val();
    params.describe = $("#voteDescribe").val();
    params.type = mode;
    params.limit = $("#modeInput").val();
    params.voternum = $("#voternum").val();
    params.pass = $("#pass").val();
    //Verify
    if (params.title == "") {
        allDone = false;
        $("#borderTitle").addClass("has-error");
    }
    if (params.describe == "") {
        allDone = false;
        $("#borderDescribe").addClass("has-error");
    }

    if (params.voternum == "") {
        allDone = false;
        $("#borderVoternum").addClass("has-error");
    }

    if (params.pass == "") {
        allDone = false;
        $("#borderPass").addClass("has-error");
    }
    //Limit
    if ($("#modeInput").val() != "") {
        if (!isNaN(Number($("#modeInput").val()))) {
            if (Number($("#modeInput").val()) > options) {
                allDone = false;
                errorS();
            }
        } else {
            allDone = false;
            errorS();
        }
    } else {
        allDone = false;
        errorS();
    }
    return allDone;
}

function clearAllError() {
    for (var i = 1; i <= options; ++i) {
        $(".option" + i).removeClass("has-error");
    }
    $("#borderTitle").removeClass("has-error");
    $("#borderDescribe").removeClass("has-error");
    $("#status").removeClass("has-error");
    $("#borderVoternum").removeClass("has-error");
    $("#borderPass").removeClass("has-error");
    $(".isError").html("");
}