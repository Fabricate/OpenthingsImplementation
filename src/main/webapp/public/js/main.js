jQuery(document).ready(function() {
  $("#contactinitiator").removeAttr('href');
  // add the wysiwyg via markitup
  $('.wysiwyg').markItUp(myTextileSettings);

    if ($('.userDes p').is(':empty')){
    $('.userDes').remove();
  }
});

$("#contactinitiator").click(function(){
  $(".contactMaker").slideToggle();
});

$('.editMenu ul li a').click(function(){
  $(".editMenu").toggleClass("activeMenu");
  $(".projectNavBtn").toggleClass("projectNavBtnActive");
  $('.projectNavBtn').html("<span class='icon-pencil'></span> Project menu");
});

$(".projectNavBtn").click(function() {
  $(".editMenu").toggleClass("activeMenu");
  $(".projectNavBtn").toggleClass("projectNavBtnActive");
  var $this = $(this);
  $this.toggleClass("editOn");
  if ($this.hasClass("editOn")) {
      $this.html("Close menu");
  } else {
      $this.html("Project menu");
  }
});

$('.onoffswitch-label').click(function(){
    $(this).parent().toggleClass('onoffswitch-checked');
    if ($(".onoffswitch").hasClass("onoffswitch-checked")) {
      $(".editPage").fadeIn("fast");
    }
    else{
      $(".editPage").fadeOut("fast");
    }
}); 

$('.login__tab').click(function(){
  $('.login__tab').addClass('active');
  $('.signup__tab').removeClass('active');
  $('.home__signup').hide();
  $('.home__login').fadeIn();
});

$('.signup__tab').click(function(){
  $('.signup__tab').addClass('active');
  $('.login__tab').removeClass('active');
  $('.home__login').hide();
  $('.home__signup').fadeIn();
});



// Remove elements when user is not loggedIn.
$(".languageSelect, .notLoggedIn .commentInput, .notLoggedIn #newrating, .notLoggedIn .editMenu,.notLoggedIn .projectNavBtn, .notLoggedIn .editProjectBtn, .notLoggedIn .editMenuHorizontal").remove();

$(".down__container").click(function() {
    $('html, body').animate({
        scrollTop: $(".mainContent").offset().top
    }, 700);
});

// Check if there are comments available
if( $('.comment').length){
}
else{
  $(".viewcomments").remove();
}

// Mobile menu functions
$('.openMenu p').click(function(){
  var $openmenu = $('.openMenu');
  var $openContent = $('.openMenu p');

  $('.mainNavigation ul').slideToggle();
  $openmenu.toggleClass("activeMenu");
  if ($openmenu.hasClass("activeMenu")) {
      $openContent.html('<span class="icon-delete"></span> Close menu');
  } else {
      $openContent.html('<span class="icon-down"></span> Open menu');
  }
});

// Add active class to menu, doesnt work without ajax.
$('.mainNavigation li a').click(function(){
    $('.mainNavigation li a').removeClass("active");
    $(this).addClass("active");
});

$('.mainfest-point').click(function(){
  $(this).find('p').slideToggle();
  $(this).find('p').toggleClass('openacc');

  var $openacc = $('.mainfest-point p');

  if ($openacc.hasClass("openacc")) {
      (this).slideUp;
  } else {

  }

});


$(window).resize(function() {
  menuHeight();
});

$(document).ready(function() {
  menuHeight();

  //Count amount of comments
  var n = $( ".comment" ).size();
  $('.viewcomments i').prepend(n + " ");

  // Scroll to an element
  $('a').click(function() {
    var menuHeight = $('.editMenuHorizontal').height() + $('.mainNavigation').height();
    var elementClicked = $(this).attr("href");
    var destination = $(elementClicked).offset().top;
    $("html:not(:animated),body:not(:animated)").animate({ scrollTop: destination - menuHeight}, 500 );
    return false;
  });
});

// Pop-ups such as vimeo
$('.popup-youtube, .popup-vimeo, .popup-gmaps').magnificPopup({
  type: 'iframe',
  mainClass: 'mfp-fade',
  showCloseBtn: true,
  closeBtnInside:true,
  removalDelay: 160,
  preloader: false,
  fixedContentPos: false,

  markup: '<div class="mfp-iframe-scaler">'+
            '<div class="mfp-close"></div>'+
            '<iframe class="mfp-iframe" frameborder="0" allowfullscreen></iframe>'+
          '</div>'
});
