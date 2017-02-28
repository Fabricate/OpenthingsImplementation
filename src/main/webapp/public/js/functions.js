function ShowComments(){
  $(".projectComments ul").slideToggle();
  $(".viewcomments").addClass( "commentsopen" );
}

function openComments(){
  $(".projectComments ul").slideDown();
  $(".viewcomments").addClass( "commentsopen" );
  $('html, body').animate({
      scrollTop: $("#comments li:last").offset().top
  }, 500);
}

function AddProject(){
  $(".postProject").slideDown();
  $(".close-icon").click(function(){
    $(".postProject").slideUp();
  });
}

function EditProject(){
  $(".editProject").fadeIn();
  document.body.style.overflow = 'hidden';
  $(".close-icon").click(function(){
    $(".editProject").fadeOut();
    document.body.style.overflow = 'auto';
  });
}
function LoginScreen(){
  $(".loginScreen").slideDown();
  $(".close-icon").click(function(){
    $(".loginScreen").slideUp();
  });
}
function LogoutScreen(){
  $(".logoutScreen").slideDown();
  $(".close-icon").click(function(){
    $(".logoutScreen").slideUp();
  });
}
function AccountScreen(){
  $(".accountScreen").slideDown();
  $(".close-icon").click(function(){
    $(".accountScreen").slideUp();
  });
}
function AddMaterial(){
  $(".addMaterial").slideToggle();
}

//Scroll down to projects
function GoToProjects(){
  $('html, body').animate({
    scrollTop: $(".mainContent").offset().top
  }, 500);
}

function ViewDetails(){
  $(".versionDetails").slideToggle();
}

function AdvancedSearch(){
  $(".advancedSearch").slideToggle();
  $(".close-icon").click(function(){
    $(".accountScreen").fadeOut();
  });
}

function AddTutorial(){
  $(".addTutorial").slideToggle();
  $('html, body').animate({
      scrollTop: $(".addTutorial").offset().top
  }, 500);
}

// Measure margin of the main container according the menu height
function menuHeight(){
  var menuHeight = $('.mainNavigation').height();
  var mainMenuHeight = $('.mainNavigation').height();
  $('#main').css({'margin-top' : menuHeight + 'px'});
}
