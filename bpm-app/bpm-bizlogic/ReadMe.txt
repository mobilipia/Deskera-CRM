To include this module in a spring project -

add these lines in your web configuration file -

    <servlet>
        <servlet-name>processor</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
           <param-name>contextConfigLocation</param-name>
           <param-value>classpath:com/krawler/br/processor-servlet.xml</param-value>
        </init-param>
        <init-param>
            <param-name>threadContextInheritable</param-name>
            <param-value>true</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>processor</servlet-name>
        <url-pattern>/br/*</url-pattern>
    </servlet-mapping>
