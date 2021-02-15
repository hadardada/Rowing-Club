package utilities;

import constants.Constants;
import userManager.UserManager;

import javax.servlet.ServletContext;

public class ServletUtils {
        public static UserManager getUserManager(ServletContext servletContext) {
            return (UserManager) servletContext.getAttribute(Constants.USER_MANAGER_ATTRIBUTE_NAME);
        }
    }

