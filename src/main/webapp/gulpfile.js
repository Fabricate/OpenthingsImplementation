var gulp = require('gulp'),
    sass = require('gulp-sass'),
    autoprefixer = require('gulp-autoprefixer'),
    minifyCSS = require('gulp-minify-css'),
    uglify = require('gulp-uglify'),
    concat = require('gulp-concat'),
    babel = require('gulp-babel');

gulp.task('css', function () {
  gulp
    .src('src/sass/**/*.scss')
    .pipe(sass().on('error', sass.logError))
    .pipe(autoprefixer())
    .pipe(gulp.dest('public/stylesheets'));
});
gulp.task('js', function() {
    gulp.src([
      'src/js/vendors/dropzone.js',
      'src/js/*.js'
    ])
    .pipe(babel({
        presets: ['es2015']
    }))
    // concat pulls all our files together before minifying them
    .pipe( concat('main.min.js') )
    .pipe(uglify())
    .pipe(gulp.dest('public/js'))
});

gulp.task('watch', function () {
  gulp.watch('src/sass/**/*', ['css']);
  gulp.watch('src/js/*.js',['js']);
});

gulp.task('default', ['css', 'js']);
gulp.task('start', ['watch']);
