project.name=HibernateFramework

project.home=${entity.projectPath()}/

jsp.file.content=<%@ page contentType=\text/html\%>\n
        <%@ page pageEncoding=\UTF-8\%>\n
        <%@ page import=\com.krawler.common.session.SessionExpiredException\%>\n
        <%@ page import=\com.krawler.esp.database.dbcon\%>\n
        <jsp:useBean id=\sessionbean\ scope=\session\ class=\com.krawler.esp.handlers.SessionHandler\ />\n\n<%\n%>

jsp.file.path=${entity.projectPath()}/web/jspfiles/buttonJsps

package.path=com.krawler.esp.hibernate.impl

tpl.root=com/krawler/portal/tools/dependencies/

tpl.model=model.ftl

tpl.impl.model=model_impl.ftl

tpl.hbm.xml=hbm_xml.ftl

generate.dir.path=src/java/com/krawler/esp/hibernate/impl/

cfg.source.file.path=src/java/hibernate.cfg.xml

cfg.classes.file.path=build/web/WEB-INF/classes/hibernate.cfg.xml

package.file.path=com/krawler/esp/hibernate/impl/

report.hardcode.str=X_X

store.path=${entity.storePath()}/store/

module.properties=build/web/WEB-INF/classes/com/krawler/portal/tools/module.properties

module.build.xml=build/web/WEB-INF/classes/com/krawler/portal/tools/

module.source.dir=src/java/com/krawler/esp/hibernate/impl/

module.classes.dir=build/web/WEB-INF/classes/com/krawler/esp/hibernate/impl/

module.classes.dest.dir=build/web/WEB-INF/classes/

index.path=${entity.indexPath()}/Log/segments

tomcat.lib=${entity.tomcatPath()}/apache-tomcat-6.0.18/lib

project.lib=build/web/WEB-INF/lib