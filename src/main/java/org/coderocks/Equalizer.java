package org.coderocks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.*;
import com.vaadin.shared.ui.JavaScriptComponentState;
import com.vaadin.ui.AbstractJavaScriptComponent;

@JavaScript({"equalizer.js"})
public class Equalizer extends AbstractJavaScriptComponent {
    public interface ValueChangeListener extends Serializable {
        void valueChange();
    }
}
 /*   @Override
    protected EqualizerState getState() {
        return (EqualizerState) super.getState();
    }
    
  }


class EqualizerState extends JavaScriptComponentState {

	}*/
