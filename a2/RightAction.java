package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RightAction extends AbstractAction {

	Code c;
	
	RightAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveRight();
	}

}
