package utilities;

import bms.notificationsEngine.notificatiosnManager.NotificationsManager;
import constants.Constants;
import bms.engine.userManager.UserManager;

import javax.servlet.ServletContext;

public class ServletUtils {
        public static UserManager getUserManager(ServletContext servletContext) {
            return (UserManager) servletContext.getAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME);
        }

        public static NotificationsManager getNotificationsManager (ServletContext servletContext){
            return (NotificationsManager) servletContext.getAttribute(Constants.NOTIFICATIONS_MANAGER_ATTRIBUTE_NAME);

        }
    }

