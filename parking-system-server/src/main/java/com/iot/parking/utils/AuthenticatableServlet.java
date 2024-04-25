package com.iot.parking.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iot.parking.models.User;
import com.iot.parking.servlet.SpotServlet;

public class AuthenticatableServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(SpotServlet.class);
	
	protected static List<String> authPaths = new ArrayList<>();
	
	private static String createRegexFromGlob(String glob)
	{
	    String out = "^";
	    for(int i = 0; i < glob.length(); ++i)
	    {
	        final char c = glob.charAt(i);
	        switch(c)
	        {
	        case '*': out += ".*"; break;
	        case '?': out += '.'; break;
	        case '.': out += "\\."; break;
	        case '\\': out += "\\\\"; break;
	        default: out += c;
	        }
	    }
	    out += '$';
	    return out;
	}
	protected List<String> getPaths() {
		return authPaths;
	}
	private boolean needsAuthentication(String path) {
		LOG.info("Checking authentication");
		if (path == null) {
			path = "/";
		}
		for(String template: getPaths()) {
			LOG.info("Paths {}", template);
			if(path.matches(createRegexFromGlob(template))) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
	        throws ServletException, IOException
	    {
			if(!req.getMethod().equals("OPTIONS")) {
				if(needsAuthentication(req.getPathInfo())) {
					User user = Authentication.checkLoggedIn(req.getHeader("Authorization"));
					if(user == null) {
						LOG.info("Unauthorized");
			            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
			            return;
					}
					else {
						req.setAttribute("user", user);
					}
				}
			}
			super.service(req, resp);
	    }
	
}
