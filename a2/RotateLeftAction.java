package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RotateLeftAction extends AbstractAction {

	Code c;
	
	RotateLeftAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.rotateLeft();
	}

}
