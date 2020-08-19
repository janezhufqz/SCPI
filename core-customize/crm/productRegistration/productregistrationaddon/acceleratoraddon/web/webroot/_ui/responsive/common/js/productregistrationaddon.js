ACC.autocompletes = {

	_autoload: [
		"bindRegistrationSearchAutocomplete"
	],

	bindRegistrationSearchAutocomplete: function ()
	{
		var isAutoComplete = false;
		// extend the default autocomplete widget, to solve issue on multiple instances of the searchbox component
		$.widget( "custom.yautocomplete", $.ui.autocomplete, {
			
			_create:function(){
				
				// get instance specific options form the html data attr
				// set the options to the widget
				this._setOptions({
					autocompleteUrl:ACC.config.encodedContextPath + '/my-account/register-a-product/getSuggestions' ,
					source: this.source
				});
				
				// call the _super()
				$.ui.autocomplete.prototype._create.call(this);
				
			},
			_renderItem : function (ul, item){
				
				 if (item.type === "productResultt"){

					var renderHtmlContent = "<div class='productRegAutoComplete'>" ;

					if (item.image != null){
						renderHtmlContent += "<div class='thumb1' id="+item.code+ "><img src='" + item.image +  "'  />" + item.value +"</div>";
					}else{
						renderHtmlContent += "<div class='thumb1'>"+ item.value + "</div>";
					}
					renderHtmlContent+= "</div>"

					return $("<li>").data("item.autocomplete", item).append(renderHtmlContent).appendTo(ul);
				}
			},
			
			source: function (request, response)
			{
				var searchurl = ACC.config.encodedContextPath + '/my-account/register-a-product/getSuggestions';

				$.getJSON(searchurl, {searchText: request.term}, function (data)
				{
					var autoSearchData = [];
					
					if(data != null){
						$.each(data, function (i, obj)
						{
							autoSearchData.push({
								value: obj.name,
								code: obj.code,
								desc: obj.description,
								manufacturer: obj.manufacturer,
								url:  ACC.config.encodedContextPath + obj.url,
								type: "productResultt",
								image: (obj.images!=null) ? obj.images[0].url : null // prevent errors if obj.images = null
							});
						});
					}
					return response(autoSearchData);
				});
			}
		});

		$('input[name=productDesc]') .on( "autocompleteselect", function( event, ui ) {
			var selectedProductCode = ui.item.code;
			var selectedProductName = ui.item.value;
			isAutoComplete = true;
			$(this).val(selectedProductName);
			$("#productID").val(selectedProductCode);
			return false;
		} );
		
		
		$(".productRegTopBackBtn").on("click", function() {
			var sUrl = $(this).data("backToRegistrations");
			window.location = sUrl;
		});
		
		//if user did not use auto complete feature instead provided typed input
		$("#addProductRegistration").on("click", function() {
			if(!isAutoComplete){
				$("#productID").val($('input[name=productDesc]').val());
			}
		});
		
		
		var $searchRegProduct = $('input[name=productDesc]');
		if($searchRegProduct.length>0){
			$searchRegProduct.yautocomplete()
		}
	}
};