ACC.serviceorder = {

	bindAll : function() {
		this.backToServiceOrderList();
	},

	backToServiceOrderList : function() {
		$(".serviceOrderTopBackBtn").on("click", function() {
			var sUrl = $(this).data("backToOrderlist");
			window.location = sUrl;
		});
	}
}

$(document).ready(function() {
	ACC.serviceorder.bindAll();
});