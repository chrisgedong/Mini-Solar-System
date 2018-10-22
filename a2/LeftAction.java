package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class LeftAction extends AbstractAction {

	Code c;
	
	LeftAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveLeft();
	}

}
