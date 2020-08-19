$(document).ready(function() {
	function deselect(e) {
		$('.pop').slideFadeToggle(function() {
			e.removeClass('selected');
		});
	}

	$(function() {
		$('#reasonCatLabel').on('click', function() {
			if ($(this).hasClass('selected')) {
				deselect($(this));
			} else {
				$(this).addClass('selected');
				$('.pop').slideFadeToggle();
			}
			return false;
		});

		$('.hideCatPopup').on('click', function() {
			deselect($('#reasonCatLabel'));
			return false;
		});
	});

	$.fn.slideFadeToggle = function(easing, callback) {
		return this.animate({
			opacity : 'toggle',
			height : 'toggle'
		}, 'fast', easing, callback);
	};

	$(".create-ticket-group").hover(function() {
		$(".create-ticket-options").fadeIn(100);
	}, function() {
		$(".create-ticket-options").fadeOut(100);
	});

});

function getReasonCategory(level,type) {
	var child = level + 1;
	for (; child <= 4; child++) {
		$('#reasonCat_' + child).val('');
	}
	$('.help-block').remove();
	var isBlank = validateHierarchy(level);
	if (!isBlank) {
		if ($('#reasonCat_' + level).val() !== '') {

			var serverUrl = ACC.config.encodedContextPath
					+ '/my-account/getRelatedCategories';
			var selectedelement = $('#reasonCat_' + level).val();
			var dataString = 'categoryGuid=' + selectedelement +'&type=' + type;
			$
					.ajax({
						type : "GET",
						url : serverUrl,
						data : dataString,
						cache : false,
						success : function(response) {
							var childLevel = level + 1;
							if (response.length <= 0) {
								for (; childLevel <= 4; childLevel++) {
									$('#reasonCat_' + childLevel).val('')
									$('#reasonCat_' + childLevel).attr(
											'disabled', 'disabled');
								}
							} else {
								$('#reasonCat_' + childLevel).removeAttr(
										'disabled');
								// populate child categories
								var select = $('#reasonCat_' + childLevel);
								select.find('option').remove();
								$.each(response, function(index, value) {
									$('<option>').val(value.guid).text(
											value.name).appendTo(select);
								});
								select
										.prepend("<option value='' selected='selected'></option>"); // add
																									// blank
																									// option
								for (; childLevel <= 4; childLevel++) {
									$('#reasonCat_' + childLevel).val('');
								}
							}
						}
					});
		}

	}
}

function showSelectedValue() {
	var counter = 4;
	while (counter >= 1) {
		var selectBox = document.getElementById('reasonCat_' + counter);
		if (selectBox) {
			if (selectBox.selectedIndex !==null) {
				var displayVal = $(selectBox).find(":selected").text();
				var select = $(".selectedReasonCat");
				if (displayVal !== "") {
					select.find('option').remove();
					var option = new Option(displayVal, displayVal);
					select.append($(option));
					break;
				}
			}
		}
		counter = counter - 1;
	}
}

function validateHierarchy(level) {
	var isBlank = false;
	var parent = level - 1;
	for (; parent >= 1; parent--) {
		if ($('#reasonCat_' + parent).val() === '')
			isBlank = true;
	}
	if (isBlank)
		$('.popupWindowCategory')
				.before(
						"<label class = help-block errorReasonCat>Please select categories by level</label>")
				.css("color:red;");
	return isBlank;
}

function getAssociatedOrderEntries(element) {
	var orderCode = parseInt($(element).val());
	var url = ACC.config.encodedContextPath
			+ '/my-account/getAssociatedOrderEntries';
	if (!Number.isNaN(orderCode)) {
		var dataString = 'orderCode=' + $(element).val();
		$.ajax({
			type : "GET",
			url : url,
			data : dataString,
			cache : false,
			success : function(response) {
				populateOrderEntriesDropdown(response);
			}
		});
	} else {
		emptyOrderEntriesDropdown();
	}
}
function populateOrderEntriesDropdown(response) {
	var select = $(".orderEntriesDropDown");
	select.find('option').remove();
	$.each(response, function(index, value) {
		$('<option>').val(value.entryNumber).text(
				"Product: " + value.productCode + "; Quantity: "
						+ value.quantity).appendTo(select);
	});
}
function emptyOrderEntriesDropdown() {
	var select = $(".orderEntriesDropDown");
	select.find('option').remove();
}
