$('document').ready(function(){
	$('body').on('click','#closeMenu',function(){
        $('.sidebar').hide(200);
        $('#closeMenu').hide(200);
        $('#openMenu').show(200);
        $('#openMenu').css('left','3px');
    });
    $('body').on('click','#openMenu',function(){
        $('.sidebar').show(200);
        $('#closeMenu').show(200);
        $('#openMenu').hide(200);
        $('#closeMenu').css('left','200px');
    });
});

$(function() {
    var action;
    $(".number-spinner button").click(function () {
        btn = $(this);
        input = btn.closest('.number-spinner').find('input');
        btn.closest('.number-spinner').find('button').prop("disabled", false);

    	if (btn.attr('data-dir') == 'up') {
            // action = setInterval(function(){
                if ( input.attr('max') == undefined || parseInt(input.val()) < parseInt(input.attr('max')) ) {
                    input.val(parseInt(input.val())+1);
                }else{
                    btn.prop("disabled", true);
                    // clearInterval(action);
                }
            // }, 50);
    	} else {
            // action = setInterval(function(){
                if ( input.attr('min') == undefined || parseInt(input.val()) > parseInt(input.attr('min')) ) {
                    input.val(parseInt(input.val())-1);
                }else{
                    btn.prop("disabled", true);
                    // clearInterval(action);
                }
            // }, 50);
    	}
    }).mouseup(function(){
        // clearInterval(action);
    });
});

$(function(){
	$("body").on('click','.dropdown-toggle',function(e) {
        $('.dropDownMenu.dropdown-menu', this).stop( true, true ).fadeIn("fast");
        $(this).toggleClass('open');
        $('b', this).toggleClass("caret caret-up");
		console.log(this);
    });
});

!function ($) {
    $(document).on("click","ul.nav li.parent > a > span.icon", function(){          
        $(this).find('em:first').toggleClass("glyphicon-minus");      
    }); 
    $(".sidebar span.icon").find('em:first').addClass("glyphicon-plus");
}(window.jQuery);
$(window).on('resize', function () {
  if ($(window).width() > 768) $('#sidebar-collapse').collapse('show')
});
$(window).on('resize', function () {
  if ($(window).width() <= 767) $('#sidebar-collapse').collapse('hide')
});