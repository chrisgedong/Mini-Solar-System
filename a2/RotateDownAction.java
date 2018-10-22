package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RotateDownAction extends AbstractAction {

	Code c;
	
	RotateDownAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.rotateDown();
	}

}
