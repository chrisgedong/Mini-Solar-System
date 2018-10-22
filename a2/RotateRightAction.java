package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RotateRightAction extends AbstractAction {

	Code c;
	
	RotateRightAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.rotateRight();
	}

}
