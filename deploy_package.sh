rm -rf /opt/jetty/webapps/root/WEB-INF/lib/*
rm -rf /opt/jetty/webapps/root/WEB-INF/classes/
cp -ar target/webapp/* /opt/jetty/webapps/root/
sudo chown -R jetty:jetty /opt/jetty/webapps/root/projects
sudo service jetty restart
