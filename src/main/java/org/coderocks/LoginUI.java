package org.coderocks;


import javax.servlet.annotation.WebServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.server.Page.UriFragmentChangedListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@SpringUI
@SpringViewDisplay
@SuppressWarnings("serial")
@Theme("mytheme")
//@StyleSheet("/VAADIN/themes/mytheme/sliderpanel.css") 
@EnableAutoConfiguration
@ComponentScan
public class LoginUI extends UI {

	@Autowired
	SpringViewProvider viewProvider;	
	
	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = LoginUI.class)
	public static class MyLoginServlet extends VaadinServlet {
	}

	@Autowired
	public LoginUI(SpringViewProvider viewProvider) {
	    this.viewProvider = viewProvider;
	}
	public static Authentication AUTH;

	@Override
	protected void init(VaadinRequest request) {
		AUTH = new Authentication();
		Navigator nav = new Navigator(this, this);
		nav.addProvider(viewProvider);
		setNavigator(nav);
		
		nav.addView(LoginView.NAME, LoginView.class);
		nav.setErrorView(LoginView.class);
		
		
		
		Page.getCurrent().addUriFragmentChangedListener(new UriFragmentChangedListener() {
			
			public void uriFragmentChanged(UriFragmentChangedEvent event) {
				router(event.getUriFragment());
			}
		});
		
		
		router("");
	}
	
	private void router(String route){
		//Notification.show(route);
		if(getSession().getAttribute("user") != null){
			
			//setContent(new MainView());
			
			//getNavigator().addView(CADPartGeoView.NAME, CADPartGeoView.class);			
			getNavigator().addView(UploadView.NAME, UploadView.class);
			if(route.equals("!"+UploadView.NAME)){
				getNavigator().navigateTo(UploadView.NAME);
			}else if (route.equals("!"+SignUpView.NAME)){
				getNavigator().navigateTo(getNavigator().getState());
				getNavigator().navigateTo(SignUpView.NAME);
				}
		}else{
			getNavigator().navigateTo(LoginView.NAME);
		}
	}

}