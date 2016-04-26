var gulp = require('gulp'),
    sass = require('gulp-sass'),
    autoprefixer = require('gulp-autoprefixer'),
    minifyCSS = require('gulp-minify-css');

gulp.task('css', function () {
  gulp
    .src('sass/**/*.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(autoprefixer())
    .pipe(gulp.dest('public/stylesheets'));
});

gulp.task('watch', function () {
   gulp.watch('sass/**/*', ['css']);
});

gulp.task('default', ['css']);
gulp.task('start', ['watch']);
