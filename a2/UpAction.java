package a2;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpAction extends AbstractAction {

	Code c;
	
	UpAction(Code c) {
		this.c = c;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		c.moveUp();
	}

}
