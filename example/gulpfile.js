/* global require */

var gulp = require('gulp');
var webserver = require('gulp-webserver');
var templateCache = require('gulp-angular-templatecache');
var minifyHtml = require('gulp-minify-html');
var concat = require('gulp-concat');
var uglify = require('gulp-uglify');
var streamqueue = require('streamqueue');
var jscs = require('gulp-jscs');
var jshint = require('gulp-jshint');
var gulpFilter = require('gulp-filter');
var rename = require('gulp-rename');
var minifycss = require('gulp-minify-css');
var flatten = require('gulp-flatten');
var mainBowerFiles = require('main-bower-files');

var publishdir = 'dist';
var dist = {
    all: [publishdir + '/**/*'],
    css: publishdir + '/',
    js: publishdir + '/',
    font: publishdir + '/',
    vendor: publishdir + '/'
};

gulp.task('bower', function() {
    var jsFilter = gulpFilter('**/*.js')
    var cssFilter = gulpFilter('**/*.css')
    var fontFilter = gulpFilter(['*.eot', '*.woff', '*.svg', '*.ttf']);

    return gulp.src(mainBowerFiles())
        .pipe(jsFilter)
        .pipe(concat('vendor.js'))
        .pipe(gulp.dest(dist.js))
        .pipe(uglify())
        .pipe(rename({
            suffix: ".min"
        }))
        .pipe(gulp.dest(dist.js))
        .pipe(jsFilter.restore())
        .pipe(cssFilter)
        .pipe(concat('vendor.css'))
        .pipe(gulp.dest(dist.css))
        .pipe(minifycss())
        .pipe(rename({
            suffix: ".min"
        }))
        .pipe(gulp.dest(dist.css))
        .pipe(cssFilter.restore())
        .pipe(fontFilter)
        .pipe(flatten())
        .pipe(gulp.dest(dist.font))
        .pipe(gulp.dest(dist.vendor))
});

gulp.task('css', function () {
    return gulp.src('app/styles/**/*.css')
        .pipe(minifycss())
        .pipe(concat('style.css'))
        .pipe(gulp.dest(dist.css));
});

gulp.task('js', function () {
    return gulp.src('app/scripts/**/*.js')
        .pipe(uglify())
        .pipe(concat('app.js'))
        .pipe(gulp.dest(dist.js));
});

gulp.task('copy', function() {
    gulp.src('app/index.html')
        .pipe(gulp.dest(dist.js));
});

gulp.task('minify', function() {
  var stream = streamqueue({objectMode: true});
  stream.queue(gulp.src('app/scripts/**/*.js')
          .pipe(jshint())
          .pipe(jshint.reporter('default'))
  );
  stream.queue(
        gulp.src('./app/views/*.html')
            .pipe(minifyHtml({
                empty: true,
                spare: true,
                quotes: true
            }))
            .pipe(templateCache({
                module: 'lightApp',
                root: 'views/'
            }))
    );

  stream.done()
        .pipe(concat('app.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest(dist.js));
});

gulp.task('non-minified', function() {
  var stream = streamqueue({objectMode: true});
  stream.queue(gulp.src('app/scripts/**/*.js'));

  stream.queue(
        gulp.src('./app/views/*.html')
            .pipe(templateCache({
                module: 'lightApp',
                root: 'views/'
            }))
    );

  stream.done()
        .pipe(concat('app.js'))
        .pipe(gulp.dest(dist.js));

});

gulp.task('jscs', function() {
  gulp.src('./src/**/*.js')
      .pipe(jscs());
});

gulp.task('default', [
    'bower',
    'css',
    'js',
    'copy'
]);

gulp.task('watch', function() {
  gulp.watch('./app/**/*', ['default']);
});

gulp.task('webserver', function() {
  gulp.src('./dist')
    .pipe(webserver({
      livereload: true,
      fallback: 'index.html',
      proxies: [
        {
          source: '/api',
          target: 'http://example:8080/api'
        }
      ],
      port: 8001,
      open: true
    }));
});
