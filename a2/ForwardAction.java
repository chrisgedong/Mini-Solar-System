package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ForwardAction extends AbstractAction {

	Code c;
	
	ForwardAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveForward();
	}

}
