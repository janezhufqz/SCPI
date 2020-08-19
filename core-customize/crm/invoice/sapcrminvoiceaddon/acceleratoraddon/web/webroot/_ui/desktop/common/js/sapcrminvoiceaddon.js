$(document).ready(function() {
    ACC.invoicecrm.bindAll();
});
ACC.invoicecrm = {
 
    $errorMessage: $('div#errorMessage'),
 
    bindAll: function() {
        this.bindInvoiceCRMPDF();
    },
 
    bindInvoiceCRMPDF: function() {
        $(document).on("click", '.invoiceCRMClass', function(event) {
            $("#errorMessage").hide();
            ACC.invoicecrm.doAjaxCallForCRMInvoice(this, event);
        });
    },
    doAjaxCallForCRMInvoice: function(element, event) {
        event.preventDefault();
        var invoiceCode = $(element).data("index");
        $.ajax({
            url: ACC.config.contextPath + '/my-account/invoicedocument/invoicedownload',
 
            data: {
                invoiceCode: invoiceCode
            },
            type: 'GET',
            contentType: 'application/json',
            sync: true,
            success: function() {
 
                ACC.invoicecrm.$errorMessage.hide();
                window
                    .open(
                        this.url,
                        '_blank',
                        "width=1024,height=768,resizable=yes,scrollbars=yes,toolbar=no,location=no,directories=no,status=no,menubar=no,copyhistory=no");
 
            },
            error: function() {
                ACC.invoicecrm.$errorMessage.show();
 
                $("#errorMessage").css("color", "red");
 
                var body = $("html, body");
                body.stop().animate({
                    scrollTop: 0
                }, '500', 'swing', function() {
 
                });
            }
        });
 
        return false;
    }
};