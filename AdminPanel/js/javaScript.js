function initSpinners() {
    var action;
	$(".number-spinner button").unbind('click');
	$(".number-spinner input").unbind('keypress');
    $(".number-spinner button")
	.click(function (e) {
        btn = $(this);
        input = btn.closest('.number-spinner').find('input');
        btn.closest('.number-spinner').find('button').prop("disabled", false);

    	if (btn.attr('data-dir') == 'up') {
            //action = setInterval(function(){
                if ( input.attr('max') == undefined || parseInt(input.val()) < parseInt(input.attr('max')) ) {
					var val = input.val();
                    input.val(parseInt(val)+1);
					input.trigger('input');
                }else{
                    btn.prop("disabled", true);
                   // clearInterval(action);
                }
            //}, 75);
    	} else {
            //action = setInterval(function(){
                if ( input.attr('min') == undefined || parseInt(input.val()) > parseInt(input.attr('min')) ) {
					var val = input.val();
                    input.val(parseInt(val)-1);
					input.trigger('input');
                }else{
                    btn.prop("disabled", true);
                  //  clearInterval(action);
                }
           // }, 75);
    	}
    });
	$(".number-spinner input")
	.keypress(function (e) {
        input = $(this);
        //input = btn.closest('.number-spinner').find('input');
        console.log(e.keyCode);
    	if (e.keyCode == 38) {
                if ( input.attr('max') == undefined || parseInt(input.val()) < parseInt(input.attr('max')) ) {
					var val = input.val();
                    input.val(parseInt(val)+1);
					input.trigger('input');
                }else{
                    btn.prop("disabled", true);
                }
    	} else if (e.keyCode == 40){
                if ( input.attr('min') == undefined || parseInt(input.val()) > parseInt(input.attr('min')) ) {
					var val = input.val();
                    input.val(parseInt(val)-1);
					input.trigger('input');
                }else{
                    btn.prop("disabled", true);
                }
    	}
    })
	.mouseup(function(){
       // clearInterval(action);
    });
}

var temp=0;
function moreCategory(){
    $('.moreCategory').hide();
    if(temp==0){
            $('.moreCategory').fadeIn(1000);
            $('#moreCategory').html('<i class="fa fa-hand-o-up"></i> Less');
            temp=1;
            
    }
    else if(temp==1){
            $('.moreCategory').fadeOut(2000);
            $('#moreCategory').html('<i class="fa fa-hand-o-down"></i> More');
            temp=0;
    }
}