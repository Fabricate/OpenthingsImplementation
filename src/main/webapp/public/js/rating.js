//Project rating
$('.rating1').hover(function() {
  $('.rating').toggleClass('rated1'); // add class when mouseover happen
});
$('.rating2').hover(function() {
  $('.rating').toggleClass('rated2'); // add class when mouseover happen
});
$('.rating3').hover(function() {
  $('.rating').toggleClass('rated3'); // add class when mouseover happen
});
$('.rating4').hover(function() {
  $('.rating').toggleClass('rated4'); // add class when mouseover happen
});
$('.rating5').hover(function() {
  $('.rating').toggleClass('rated5'); // add class when mouseover happen
});
$(".rating1").click(function(){
  $(".rating").addClass("isRated1");
  $(".rating").removeClass("isRated2 isRated3 isRated4 isRated5");
});
$(".rating2").click(function(){
  $(".rating").addClass("isRated2");
  $(".rating").removeClass("isRated3 isRated4 isRated5");
});
$(".rating3").click(function(){
  $(".rating").addClass("isRated3");
  $(".rating").removeClass("isRated4 isRated5");
});
$(".rating4").click(function(){
  $(".rating").addClass("isRated4");
  $(".rating").removeClass("isRated5");
});
$(".rating5").click(function(){
  $(".rating").addClass("isRated5");
});
