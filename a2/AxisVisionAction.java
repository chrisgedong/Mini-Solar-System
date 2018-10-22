package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AxisVisionAction extends AbstractAction {

	Code c;
	
	AxisVisionAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.axisToggle();
	}

}
