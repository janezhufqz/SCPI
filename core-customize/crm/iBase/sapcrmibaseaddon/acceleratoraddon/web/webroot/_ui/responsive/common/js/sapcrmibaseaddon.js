ACC.sapcrmibaseaddon = {
	bindIbaseTreeArrow : function() {
		$(".treearrow").on("click", function() {
			var span = $(this).children("span");
			var collapseId = $(this).attr("href");
			if (span.hasClass("glyphicon-collapse-down")) {
				span.removeClass("glyphicon-collapse-down");
				span.addClass("glyphicon-expand");
			} else if (span.hasClass("glyphicon-expand")) {
				span.removeClass("glyphicon-expand");
				span.addClass("glyphicon-collapse-down");
			}
			$(collapseId).collapse("toggle");
		});
	},
	bindServiceTicketBtnMessage : function() {
		$(".service-ticket-disable").on("click", function(event) {
			event.preventDefault();
		});
	},
	bindContractBtnMessage : function() {
		$(".service-contract-disable").on("click", function(event) {
			event.preventDefault();
		});
	}
};
$(document).ready(function() {
	ACC.sapcrmibaseaddon.bindIbaseTreeArrow();
	ACC.sapcrmibaseaddon.bindServiceTicketBtnMessage();
	ACC.sapcrmibaseaddon.bindContractBtnMessage();
});