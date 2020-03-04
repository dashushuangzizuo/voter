$(document).ready(function () {
    $("#QR").qrcode(window.location.href.split('vote')[0]);
});