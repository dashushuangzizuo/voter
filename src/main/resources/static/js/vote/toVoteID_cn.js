$("#toVoteID").keydown(function () {
    if (event.keyCode == "13") {//keyCode=13是回车键
        toVoteID();
    }
});

$("#gotoVoteID").click(function () {
    toVoteID();
});

function toVoteID() {
    $("#toVoteID").modal("hide");
    var voteID = $("#voteID").val();
    var token = $("#token").val();
    params = {};
    params.voteID = voteID;
    params.token = token;
    $.ajax({
        url: "/checkVoteID",
        data: params,
        success: function (data) {
            sessionStorage.clear();
            if (data == 1) {
                sessionStorage.setItem("token",token);
                //Exist
                location.href = "/vote/cn/" + voteID+"/"+token;
            } else if (data == 2) {
                sessionStorage.setItem("token",token);
                location.href = "/vote/adm/" + voteID;
            }else if (data == 3) {
                //Not exist
                alert("口令无效！请重新输入。")
            }else if (data == 0) {
                //Not exist
                alert("投票ID 未找到！请检查是否正确。")
            }else if(data == 4){
                sessionStorage.setItem("token",token);
                location.href = "/vote/view/" + voteID;
            }else if(data == 5){
                alert("投票活动已过期！")
            }else if(data == 6){
                alert("投票进行中，请稍后查看！");
            }
        }
    });
}