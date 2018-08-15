package org.coderocks;


import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Configurable
@SpringView(name = LoginView.NAME)
@StyleSheet("../../../VAADIN/themes/mytheme/login.css") //http://localhost:8080/vaadinServlet/APP/PUBLISHED//VAADIN/themes/mytheme/sliderpanel.css
public class LoginView extends CustomComponent implements View {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Login";
	
	
	@Autowired
	UserService userService;// = ApplicationContextHolder.getContext().getBean(UserService.class);
	
    @PostConstruct
    void init() {
		setSizeFull();
		addStyleName("login-background"); 
		
		
		Panel panel = new Panel("Login");
		panel.setSizeUndefined();
		
		FormLayout content = new FormLayout();
		final TextField username = new TextField("Username");
		content.addComponent(username);
		final PasswordField password = new PasswordField("Password");
		content.addComponent(password);
		
		Button signUp = new Button("SignUp");
		signUp.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
	
					getUI().getNavigator().navigateTo(SignUpView.NAME);
					Page.getCurrent().setUriFragment("!"+SignUpView.NAME);
		
			}
			
		});
		
		Button send = new Button("Enter");
		send.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				
				List<CADPartUserBean> list = userService.getUserData(username.getValue());
				
				System.out.println(list.get(0).getUserName());
				
				if(LoginUI.AUTH.authenticate(username.getValue(), password.getValue(), list.get(0).getUserName(),list.get(0).getToken())){
					VaadinSession.getCurrent().setAttribute("user", username.getValue());
					getUI().getNavigator().navigateTo(Mp3GridView.NAME);
					Page.getCurrent().setUriFragment("!"+Mp3GridView.NAME);
				}else{
					Notification.show("Invalid credentials", Notification.Type.ERROR_MESSAGE);
				}
			}
			
		});
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(send);
		hl.addComponent(signUp);
		
		content.addComponent(hl);
		content.setSizeUndefined();
		content.setMargin(true);
		panel.setContent(content);
		
        // The view root layout
        VerticalLayout viewLayout = new VerticalLayout(panel);
        viewLayout.setSizeFull();
        viewLayout.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
       // viewLayout.setStyleName(Valo.LAYOUT_BLUE);
        setCompositionRoot(viewLayout);		
	
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		
	}	
 
}
