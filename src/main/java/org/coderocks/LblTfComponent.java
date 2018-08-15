package org.coderocks;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

public class LblTfComponent extends HorizontalLayout{

	TextField textField;
	private PasswordField passwordField;

	public LblTfComponent(String name, String width) {

		Label lblCaption = new Label(name);
		lblCaption.setWidth(width);
	

		if (name.equals("pwd")) {
			
			passwordField = new PasswordField(name);
			passwordField.setWidth(width);
			
			addComponent(lblCaption);
			addComponent(passwordField);
		} else {
			
			textField = new TextField();
			textField.setWidth(width);
			
			addComponent(lblCaption);
			addComponent(textField);
			
		}

	}


	public String getTfData() {
		return textField.getData().toString();
	}

}
