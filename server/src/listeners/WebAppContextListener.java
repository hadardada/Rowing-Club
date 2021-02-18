package listeners;

import bms.engine.Engine;
import bms.engine.userManager.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import static constants.Constants.ENGINE_ATTRIBUTE_NAME;
import static constants.Constants.USER_MANAGER_ATTRIBUTE_NAME;

@WebListener("WebApp Context Listener")
public class WebAppContextListener implements ServletContextListener {

  @Override
  public void contextInitialized(ServletContextEvent servletContextEvent) {
    ServletContext context = servletContextEvent.getServletContext();
    Engine engine = new Engine();
    context.setAttribute(ENGINE_ATTRIBUTE_NAME,engine);
    context.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager(engine));
    //context.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
  }

  @Override
  public void contextDestroyed(ServletContextEvent servletContextEvent) {
  }
}
