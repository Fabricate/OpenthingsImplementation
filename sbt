java -Xmx256M -Xss2M -XX:MaxPermSize=128m -XX:+CMSClassUnloadingEnabled -jar `dirname $0`/sbt-launch-0.13.1.jar "$@"
